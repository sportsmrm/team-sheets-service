package io.sportsmrm.teamsheets.queries

import io.sportsmrm.teamsheets.events.Event
import io.sportsmrm.teamsheets.valueobjects.{Opponent, Team}
import org.apache.pekko.Done
import org.apache.pekko.persistence.query.typed.EventEnvelope
import org.apache.pekko.projection.r2dbc.scaladsl.{R2dbcHandler, R2dbcSession}

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

object TeamSheetsProjectionHandler {
  def upsertTeamSheet(session: R2dbcSession, id: UUID, sequenceNr: Long, date: LocalDate, team: Team, opponent: Opponent)(using ec: ExecutionContext): Future[Done] = {
    val stmt = session.createStatement(
      """
        |INSERT INTO team_sheets (
        | id, sequence_nr, date, team_id, team_name, opponent_id, opponent_name
        |) VALUES (
        | :id, :sequenceNr, :date, :teamId, :teamName, :opponentId, :opponentName
        |) ON CONFLICT (id) DO UPDATE SET (
        | sequence_nr, date, team_id, team_name, opponent_id, opponent_name
        |) = (
        | EXCLUDED.sequence_nr, EXCLUDED.date, EXCLUDED.team_id, EXCLUDED.team_name, EXCLUDED.opponent_id, EXCLUDED.opponent_name
        |) WHERE
        | team_sheets..sequence_nr < :sequenceNr""".stripMargin
    )
      .bind("id", id)
      .bind("sequenceNr", sequenceNr)
      .bind("date", date)
      .bind("teamId", team.id)
      .bind("teamName", team.displayName)
      .bind("opponentId", opponent.id)
      .bind("opponentName", opponent.displayName)


    session.updateOne(stmt).map(_ => Done)
  }
}

class TeamSheetsProjectionHandler()(using ec: ExecutionContext) extends R2dbcHandler[EventEnvelope[Event]]{
  import io.sportsmrm.teamsheets.events._
  import TeamSheetsProjectionHandler._

  override def process(session: R2dbcSession, envelope: EventEnvelope[Event]): Future[Done] = {
    envelope.event match {
      case TeamSheetCreated(id, date, team, opponent) =>
        upsertTeamSheet(session, id, envelope.sequenceNr, date, team, opponent)
      case _ => Future.successful(Done)
    }
  }
}
