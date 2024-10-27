package io.sportsmrm.teamsheets.grpc.server

import io.sportsmrm.teamsheets.grpc.CreateTeamSheetResponse
import io.sportsmrm.teamsheets.valueobjects.{Opponent, Team}
import io.sportsmrm.teamsheets.{commands => TeamSheet}
import org.apache.pekko.Done
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.actor.typed.{ActorRef, Behavior}
import org.apache.pekko.cluster.sharding.typed.scaladsl.{
  EntityContext,
  EntityRef
}
import org.apache.pekko.pattern.StatusReply

import java.time.LocalDate
import java.util.UUID

object Correlator {

  sealed trait Command {}
  final case class CreateTeamSheet(
      date: LocalDate,
      team: Team,
      opponent: Opponent,
      replyTo: ActorRef[StatusReply[CreateTeamSheetResponse]]
  ) extends Command
  private final case class WrappedResponse(response: StatusReply[Done])
      extends Command

  def apply(
      teamSheetLocator: UUID => EntityRef[TeamSheet.Command[?]]
  ): (EntityContext[Command] => Behavior[Command]) =
    entityContext =>
      Behaviors.setup[Command] { context =>
        {
          val adaptor: ActorRef[StatusReply[Done]] =
            context.messageAdapter(WrappedResponse.apply)
          new Correlator(teamSheetLocator, adaptor).init()
        }
      }
}

private class Correlator(
    private val teamSheetLocator: UUID => EntityRef[TeamSheet.Command[?]],
    adapter: ActorRef[StatusReply[Done]]
) {
  import Correlator.*

  def init(): Behavior[Command] = Behaviors.receiveMessagePartial[Command] {
    case CreateTeamSheet(date, team, opponent, replyTo) =>
      send(date, team, opponent, replyTo)
  }

  private def send(
      date: LocalDate,
      team: Team,
      opponent: Opponent,
      replyTo: ActorRef[StatusReply[CreateTeamSheetResponse]]
  ): Behavior[Command] = {
    val teamSheetId = UUID.randomUUID()
    val teamSheetEntity = teamSheetLocator(teamSheetId)

    teamSheetEntity ! TeamSheet.CreateTeamSheet(
      teamSheetId,
      date,
      team,
      opponent,
      adapter
    )

    sent(Set(replyTo), teamSheetId)
  }

  private def sent(
      replyTos: Set[ActorRef[StatusReply[CreateTeamSheetResponse]]],
      teamSheetId: UUID
  ): Behavior[Command] = Behaviors.receiveMessage[Command] {
    case CreateTeamSheet(_, _, _, replyTo) =>
      sent(replyTos + replyTo, teamSheetId)
    case WrappedResponse(response) => sendDone(replyTos, teamSheetId, response)
  }

  private def sendDone(
      replyTos: Set[ActorRef[StatusReply[CreateTeamSheetResponse]]],
      teamSheetId: UUID,
      response: StatusReply[Done]
  ) = {
    val reply = response match
      case StatusReply.Ack =>
        StatusReply.success(CreateTeamSheetResponse(teamSheetId))
      case StatusReply.Error(error) =>
        StatusReply.Error[CreateTeamSheetResponse](error)

    replyTos.foreach { _ ! reply }

    done(reply)
  }

  private def done(
      reply: StatusReply[CreateTeamSheetResponse]
  ): Behavior[Command] = Behaviors.receiveMessagePartial[Command] {
    case CreateTeamSheet(_, _, _, replyTo) => {
      replyTo ! reply
      Behaviors.same
    }
  }
}
