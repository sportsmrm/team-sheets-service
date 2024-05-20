package io.sportsmrm.teamsheets.grpc.server

import io.grpc.Status
import io.sportsmrm.teamsheets.grpc.{CreateTeamSheetRequest, CreateTeamSheetResponse, TeamSheetsService}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.AskPattern.*
import org.apache.pekko.grpc.GrpcServiceException
import org.apache.pekko.util.Timeout

import java.util.UUID
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class TeamSheetsServiceImpl(private val system: ActorSystem) extends TeamSheetsService {

  override def createTeamSheet(in: CreateTeamSheetRequest): Future[CreateTeamSheetResponse] =
    Future.failed(new GrpcServiceException(
      Status.UNIMPLEMENTED
    ))
}
