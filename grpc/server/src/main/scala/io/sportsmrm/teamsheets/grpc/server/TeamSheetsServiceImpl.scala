package io.sportsmrm.teamsheets.grpc.server

import io.sportsmrm.teamsheets.grpc.{
  CreateTeamSheetRequest,
  CreateTeamSheetResponse,
  TeamSheetsService
}
import io.sportsmrm.teamsheets.{grpc, valueobjects}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.util.Timeout

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import scala.concurrent.Future
import scala.concurrent.duration._

class TeamSheetsServiceImpl(
    val teamSheetCreator: CorrelatorLocator,
    val system: ActorSystem[?]
) extends TeamSheetsService {

  given ActorSystem[?] = system

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

}
