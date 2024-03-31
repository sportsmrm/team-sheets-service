package io.sportsmrm.teamsheets.queries

import io.r2dbc.spi.{Connection, ConnectionFactory, ConnectionFactoryMetadata}
import io.sportsmrm.teamsheets.queries.jooq.JooqTeamSheetsRepository
import io.sportsmrm.teamsheets.valueobjects.{Opponent, Team, TeamSheet}
import org.apache.pekko.{Done, NotUsed}
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.persistence.r2dbc.ConnectionFactoryProvider
import org.apache.pekko.projection.r2dbc.scaladsl.R2dbcSession
import org.apache.pekko.stream.Materializer
import org.apache.pekko.stream.scaladsl.Source
import org.jooq.conf.Settings
import org.jooq.{Record6, RecordType, SQLDialect}
import org.jooq.impl.{CustomRecord, CustomTable, DSL, SQLDataType, TableImpl}
import org.jooq.impl.DSL.*
import org.jooq.tools.r2dbc.LoggingConnection
import org.reactivestreams.{Processor, Publisher, Subscriber, Subscription}

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Future

object TeamSheetsRepository:

  def apply(system: ActorSystem[?]): TeamSheetsRepository = {
    val connectionFactory =
      ConnectionFactoryProvider(system).connectionFactoryFor(
        "pekko.persistence.r2dbc.connection-factory"
      )

    val settings = new Settings();
    settings.setExecuteLogging(true);
    val dsl = DSL.using(connectionFactory, SQLDialect.POSTGRES, settings)

    new JooqTeamSheetsRepository(dsl, system)
  }

  def apply(
      session: R2dbcSession,
      system: ActorSystem[?]
  ): TeamSheetsRepository = {
    val dsl = DSL.using(session.connection, SQLDialect.POSTGRES)
    new JooqTeamSheetsRepository(dsl, system)
  }

trait TeamSheetsRepository:
  def teamSheetsForTeam(team: Team): Source[TeamSheet, NotUsed]
  def upsertTeamSheet(
      id: UUID,
      sequenceNr: Long,
      date: LocalDate,
      team: Team,
      opponent: Opponent
  ): Future[Done]
