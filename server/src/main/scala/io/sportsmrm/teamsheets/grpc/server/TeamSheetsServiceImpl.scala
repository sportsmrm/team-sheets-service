package io.sportsmrm.teamsheets.grpc.server

import io.grpc.Status
import io.sportsmrm.teamsheets.grpc.{
  CreateTeamSheetRequest,
  CreateTeamSheetResponse,
  ListTeamSheetsRequest,
  ListTeamSheetsResponse,
  TeamSheetsService
}
import io.sportsmrm.teamsheets.queries.TeamSheetsRepository
import io.sportsmrm.teamsheets.valueobjects.TeamId
import io.sportsmrm.teamsheets.{grpc, valueobjects}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.grpc.GrpcServiceException
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.Sink
import org.apache.pekko.util.Timeout

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.concurrent.duration.*
import scala.concurrent.{ExecutionContext, Future}

class TeamSheetsServiceImpl(
    val teamSheetCreator: CorrelatorLocator,
    val teamSheetsRepository: TeamSheetsRepository,
    val system: ActorSystem[?]
) extends TeamSheetsService {

  given ExecutionContext = system.executionContext

  given defaultTimeOut: Timeout = 5.seconds

  given string2LocalDate: Conversion[String, LocalDate] with
    def apply(str: String): LocalDate =
      LocalDate.parse(str, DateTimeFormatter.ISO_LOCAL_DATE)

  given pbTeam2VOTeam: Conversion[Option[grpc.Team], valueobjects.Team] with
    def apply(grpcTeam: Option[grpc.Team]): valueobjects.Team = grpcTeam match {
      case Some(grpc.Team(id, displayName, _)) =>
        valueobjects.Team(id, displayName)
      case None => throw IllegalArgumentException("team is required")
    }

  given pbOpponent2VOOpponent
      : Conversion[Option[grpc.Opponent], valueobjects.Opponent] with
    def apply(pbOpponent: Option[grpc.Opponent]): valueobjects.Opponent =
      pbOpponent match {
        case Some(grpc.Opponent(id, displayName, _)) =>
          valueobjects.Opponent(id, displayName)
        case None => throw IllegalArgumentException("opponent is required")
      }

  override def createTeamSheet(
      in: CreateTeamSheetRequest
  ): Future[CreateTeamSheetResponse] = {
    teamSheetCreator(in.correlationId).askWithStatus[CreateTeamSheetResponse](
      ref => Correlator.CreateTeamSheet(in.date, in.team, in.opponent, ref)
    )
  }

  override def listTeamSheets(
      in: ListTeamSheetsRequest
  ): Future[ListTeamSheetsResponse] =
    try
      given Materializer = Materializer(system)
      val teamId = TeamId(in.parent)

      val result = teamSheetsRepository
        .teamSheetsForTeam(teamId)
        .map((teamSheet) => {
          grpc.TeamSheet(
            teamSheet.id.toString,
            teamSheet.date.toString,
            Some(grpc.Team(teamSheet.team.id, teamSheet.team.displayName)),
            Some(
              grpc
                .Opponent(teamSheet.opponent.id, teamSheet.opponent.displayName)
            )
          )
        })
        .take(10)
        .runWith(Sink.seq)
        .map((teamSheets) => {
          grpc.ListTeamSheetsResponse(teamSheets = teamSheets)
        })
      result
    catch
      case iae: IllegalArgumentException =>
        Future.failed(
          new GrpcServiceException(
            Status.INVALID_ARGUMENT.withDescription("Invalid parent")
          )
        )

}
