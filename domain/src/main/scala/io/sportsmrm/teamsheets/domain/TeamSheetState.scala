package io.sportsmrm.teamsheets.domain

import io.sportsmrm.teamsheets.commands.{Command, CreateTeamSheet}
import io.sportsmrm.teamsheets.events.{Event, TeamSheetCreated}
import io.sportsmrm.teamsheets.valueobjects.{Opponent, Team}
import org.apache.pekko.pattern.StatusReply
import org.apache.pekko.persistence.typed.scaladsl.Effect

import java.time.LocalDate
import java.util.UUID

sealed trait TeamSheetState {
  def applyCommand(command: Command[?]): ReplyEffect
  def applyEvent(event: Event): TeamSheetState
}

case object EmptyTeamSheet extends TeamSheetState {
  override def applyCommand(command: Command[?]): ReplyEffect = {
    command match {
      case CreateTeamSheet(id, date, team, opponent, replyTo) =>
        Effect
          .persist(TeamSheetCreated(id, date, team, opponent))
          .thenReply(replyTo)(_ => StatusReply.Ack)
      case _ =>
        Effect.unhandled.thenNoReply()
    }
  }

  override def applyEvent(event: Event): TeamSheetState =
    event match {
      case TeamSheetCreated(id, date, team, opponent) =>
        CreatedTeamSheet(id, date, team, opponent)
      case _ =>
        throw new IllegalStateException(
          s"unsupported event [$event] in state [EmptyTeamSheet]"
        )
    }
}

case class CreatedTeamSheet(
    uuid: UUID,
    date: LocalDate,
    team: Team,
    opponent: Opponent
) extends TeamSheetState {
  override def applyCommand(command: Command[?]): ReplyEffect = ???

  override def applyEvent(event: Event): TeamSheetState = ???
}
