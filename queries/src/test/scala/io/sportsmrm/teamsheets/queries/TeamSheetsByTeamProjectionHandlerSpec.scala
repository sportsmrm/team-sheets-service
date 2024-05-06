package io.sportsmrm.teamsheets.queries

import com.typesafe.config.{
  ConfigFactory,
  ConfigParseOptions,
  ConfigResolveOptions
}
import io.sportsmrm.teamsheets.events.{Event, TeamSheetCreated}
import io.sportsmrm.teamsheets.valueobjects.TeamSheet
import io.sportsmrm.util.config.DockerSecretConfigResolver
import org.apache.pekko.NotUsed
import org.apache.pekko.actor.testkit.typed.scaladsl.{
  LogCapturing,
  ScalaTestWithActorTestKit
}
import org.apache.pekko.persistence.query.Offset
import org.apache.pekko.persistence.query.typed.EventEnvelope
import org.apache.pekko.projection.ProjectionId
import org.apache.pekko.projection.r2dbc.scaladsl.R2dbcProjection
import org.apache.pekko.projection.testkit.scaladsl.{
  ProjectionTestKit,
  TestSourceProvider
}
import org.apache.pekko.stream.scaladsl.Source
import org.scalatest.enablers.Emptiness
import org.scalatest.wordspec.AnyWordSpecLike

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration._

class TeamSheetsByTeamProjectionHandlerSpec
    extends ScalaTestWithActorTestKit(
      ConfigFactory
        .load(
          "test.conf",
          ConfigParseOptions.defaults(),
          ConfigResolveOptions
            .defaults()
            .appendResolver(new DockerSecretConfigResolver())
        )
    )
    with AnyWordSpecLike
    with LogCapturing {
  private val projectionTestKit = ProjectionTestKit(system)

  given emptinessOfSource: Emptiness[Source[Any, NotUsed]] =
    new Emptiness[Source[Any, NotUsed]]() {
      override def isEmpty(source: Source[Any, NotUsed]): Boolean = {
        val result = Await.result(
          source.runFold(0)((acc, _) => acc + 1),
          3.seconds
        )
        result == 0
      }
    }

  import io.sportsmrm.teamsheets.valueobjects.TestData._

  "The TeamSheets Projection Handler" when {
    TeamSheetsProjectionHandler()
    val teamSheetId = UUID.randomUUID()

    "receiving a TeamSheetAddedEvent" should {
      "insert the team sheet if it does not already exist" in {
        TeamSheetsRepository(system)
          .teamSheetsForTeam(MENS_FIRST_TEAM) shouldBe empty

        val projection = R2dbcProjection.exactlyOnce(
          ProjectionId(UUID.randomUUID().toString, "001"),
          settings = None,
          TestSourceProvider(
            Source(
              EventEnvelope(
                Offset.sequence(0),
                teamSheetId.toString,
                0,
                TeamSheetCreated(
                  teamSheetId,
                  LocalDate.parse("2024-03-09"),
                  MENS_FIRST_TEAM,
                  FAREHAM_MENS_SECOND_TEAM
                ),
                0L,
                "TeamSheet",
                0
              ) :: Nil
            ),
            (envelop: EventEnvelope[Event]) => envelop.offset
          ),
          handler = () => new TeamSheetsProjectionHandler
        )(system)
        projectionTestKit.run(projection) {
          val result = TeamSheetsRepository(system)
            .teamSheetsForTeam(MENS_FIRST_TEAM)
            .runFold(List.empty[TeamSheet])((list, record) => {
              val result = list :+ record
              result
            })
            .futureValue
          assert(result.nonEmpty)
        }
      }
    }
  }
}
