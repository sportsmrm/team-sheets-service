package io.sportsmrm.teamsheets.queries.jooq

import io.sportsmrm.teamsheets.queries.TeamSheetsRepository
import io.sportsmrm.teamsheets.valueobjects.{Opponent, Team, TeamSheet}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.Source
import org.apache.pekko.{Done, NotUsed}
import org.jooq.DSLContext

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Future

class JooqTeamSheetsRepository(
    private val dsl: DSLContext,
    private val system: ActorSystem[?]
) extends TeamSheetsRepository {
  override def teamSheetsForTeam(team: Team): Source[TeamSheet, NotUsed] = {
    Source
      .fromPublisher(
        dsl.selectFrom(TEAM_SHEETS).where(TEAM_SHEETS.TEAM_ID.eq(team.id))
      )
      .map(record =>
        TeamSheet(
          record.get("id", classOf[UUID]),
          record.get("date", classOf[LocalDate]),
          Team(
            record.get("team_id", classOf[UUID]),
            record.get("team_name", classOf[String])
          ),
          Opponent(
            record.get("opponent_id", classOf[UUID]),
            record.get("opponent_name", classOf[String])
          )
        )
      )
  }

  override def upsertTeamSheet(
      id: UUID,
      sequenceNr: Long,
      date: LocalDate,
      team: Team,
      opponent: Opponent
  ): Future[Done] = {
    Source
      .fromPublisher(
        dsl
          .insertInto(TEAM_SHEETS)
          .set(TEAM_SHEETS.ID, id)
          .set(TEAM_SHEETS.SEQUENCE_NR, sequenceNr)
          .set(TEAM_SHEETS.DATE, date)
          .set(TEAM_SHEETS.TEAM_ID, team.id)
          .set(TEAM_SHEETS.TEAM_NAME, team.displayName)
          .set(TEAM_SHEETS.OPPONENT_ID, opponent.id)
          .set(TEAM_SHEETS.OPPONENT_NAME, opponent.displayName)
          .onConflict(TEAM_SHEETS.ID)
          .doUpdate()
          .set(TEAM_SHEETS.SEQUENCE_NR, sequenceNr)
          .set(TEAM_SHEETS.DATE, date)
          .set(TEAM_SHEETS.TEAM_ID, team.id)
          .set(TEAM_SHEETS.TEAM_NAME, team.displayName)
          .set(TEAM_SHEETS.OPPONENT_ID, opponent.id)
          .set(TEAM_SHEETS.OPPONENT_NAME, opponent.displayName)
          .where(TEAM_SHEETS.SEQUENCE_NR.le(sequenceNr))
      )
      .run()(Materializer.matFromSystem(system))
  }

}
