package io.sportsmrm.teamsheets.queries

import io.sportsmrm.teamsheets.events.{Event, TeamSheetCreated}
import org.apache.pekko.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.apache.pekko.persistence.query.Offset
import org.apache.pekko.persistence.query.typed.EventEnvelope
import org.apache.pekko.persistence.typed.PersistenceId
import org.apache.pekko.projection.ProjectionId
import org.apache.pekko.projection.r2dbc.scaladsl.R2dbcProjection
import org.apache.pekko.projection.testkit.scaladsl.{ProjectionTestKit, TestProjection, TestSourceProvider}
import org.apache.pekko.stream.scaladsl.Source
import org.scalatest.wordspec.{AnyWordSpecLike, AsyncWordSpecLike}

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.ExecutionContext

class TeamSheetsByTeamProjectionHandlerSpec extends ScalaTestWithActorTestKit() with AnyWordSpecLike {
  private val projectionTestKit = ProjectionTestKit(system)
  given ec: ExecutionContext = system.executionContext

  import io.sportsmrm.teamsheets.valueobjects.TestData._

  "The TeamSheets Projection Handler" when {
    val handler = TeamSheetsProjectionHandler()
    val teamSheetId = UUID.randomUUID()

    "receiving a TeamSheetAddedEvent" should {
      "insert a team sheet row if one does not already exist" in {
        projectionTestKit.run(R2dbcProjection.atLeastOnce(
          ProjectionId("test", "001"),
          settings = None,
          TestSourceProvider(
            Source(EventEnvelope(Offset.sequence(0), teamSheetId.toString, 0, TeamSheetCreated(
              teamSheetId,
              LocalDate.parse("2024-03-09"),
              MENS_FIRST_TEAM,
              FAREHAM_MENS_SECOND_TEAM
            ), 0L, "TeamSheet", 0) :: Nil),
            (envelop: EventEnvelope[Event]) => envelop.offset
          ),
          handler = () => new TeamSheetsProjectionHandler
        )) {
          assert(false)
        }
      }
    }
  }
}
