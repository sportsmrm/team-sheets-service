package io.sportsmrm.teamsheets.grpc.server

import io.sportsmrm.teamsheets.grpc.CreateTeamSheetResponse
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.cluster.sharding.typed.scaladsl.EntityTypeKey
import org.apache.pekko.pattern.StatusReply

object TeamSheetCreator {
  val TypeKey: EntityTypeKey[Command] =
    EntityTypeKey[Command]("TeamSheetCreator")

  sealed trait Command
  final case class Create(
      replyTo: ActorRef[StatusReply[CreateTeamSheetResponse]]
  ) extends Command
}
