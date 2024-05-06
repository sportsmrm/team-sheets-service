package io.sportsmrm.teamsheets.queries.r2dbc

import io.sportsmrm.teamsheets.queries.TeamSheetsRepository
import io.sportsmrm.teamsheets.valueobjects.{Opponent, Team, TeamSheet}
import org.apache.pekko.projection.r2dbc.scaladsl.R2dbcSession
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.{Done, NotUsed}

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Future

class R2dbcTeamSheetsRepository(private val session: R2dbcSession)
    extends TeamSheetsRepository {

  override def teamSheetsForTeam(team: Team): Source[TeamSheet, NotUsed] = ???

  override def upsertTeamSheet(
      id: UUID,
      sequenceNr: Long,
      date: LocalDate,
      team: Team,
      opponent: Opponent
  ): Future[Done] = {
    val upsertString =
      """INSERT INTO team_sheets (
        |  id, sequence_nr, date, team_id, team_name, opponent_id, opponent_name
        |) VALUES (
        |  $1, $2, $3, $4, $5, $6, $7
        |) ON CONFLICT (id) DO UPDATE SET (
        |  sequence_nr, date, team_id, team_name, opponent_id, opponent_name
        |) = (
        |  EXCLUDED.sequence_nr, EXCLUDED.date, EXCLUDED.team_id, EXCLUDED.team_name, EXCLUDED.opponent_id, EXCLUDED.opponent_name
        |) WHERE
        |  team_sheets.sequence_nr < $2
        |""".stripMargin

    val stmt = session
      .createStatement(upsertString)
      .bind("$1", id)
      .bind("$2", sequenceNr)
      .bind("$3", date)
      .bind("$4", team.id)
      .bind("$5", team.displayName)
      .bind("$6", opponent.id)
      .bind("$7", opponent.displayName)

    session.updateOne(stmt).map(_ => Done)(session.ec)
  }

}
