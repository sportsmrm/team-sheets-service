package io.sportsmrm.teamsheets.queries

import io.sportsmrm.teamsheets.events.Event // scalafix:ok
import io.sportsmrm.teamsheets.valueobjects.{Opponent, Team}
import org.apache.pekko.Done
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.persistence.query.typed.EventEnvelope
import org.apache.pekko.projection.r2dbc.scaladsl.{R2dbcHandler, R2dbcSession}

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Future

class TeamSheetsProjectionHandler()(using system: ActorSystem[?])
    extends R2dbcHandler[EventEnvelope[Event]] {
  import io.sportsmrm.teamsheets.events.*

  override def process(
      session: R2dbcSession,
      envelope: EventEnvelope[Event]
  ): Future[Done] = {
    envelope.event match {
      case TeamSheetCreated(id, date, team, opponent) =>
        TeamSheetsRepository(session, system)
          .upsertTeamSheet(id, envelope.sequenceNr, date, team, opponent)
      case _ => Future.successful(Done)
    }
  }
}
