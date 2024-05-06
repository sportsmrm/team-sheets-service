package io.sportsmrm.teamsheets.domain

import com.typesafe.config.ConfigFactory
import io.sportsmrm.teamsheets.commands.{Command, CreateTeamSheet}
import io.sportsmrm.teamsheets.events.{Event, TeamSheetCreated}
import org.apache.pekko.Done
import org.apache.pekko.actor.testkit.typed.scaladsl.{
  LogCapturing,
  ScalaTestWithActorTestKit
}
import org.apache.pekko.pattern.StatusReply
import org.apache.pekko.persistence.testkit.scaladsl.EventSourcedBehaviorTestKit
import org.scalatest.BeforeAndAfterEach
import org.scalatest.wordspec.AnyWordSpecLike

import java.time.LocalDate
import java.util.UUID

object TeamSheetEntitySpec {
  val TEAM_SHEET_ID: UUID = UUID.randomUUID()
}

class TeamSheetEntitySpec
    extends ScalaTestWithActorTestKit(
      ConfigFactory
        .parseResources("test.conf")
        .withFallback(EventSourcedBehaviorTestKit.config)
    )
    with AnyWordSpecLike
    with BeforeAndAfterEach
    with LogCapturing {

  import io.sportsmrm.teamsheets.valueobjects.TestData._
  import TeamSheetEntitySpec._

  private val teamSheetTestKit =
    EventSourcedBehaviorTestKit[Command[?], Event, TeamSheetState](
      system,
      TeamSheetEntity(TEAM_SHEET_ID)
    )

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    teamSheetTestKit.clear()
  }

  "A Team Sheet" when {
    "uninitialised" should {
      "handle the CreateTeamSheetCommand" in {
        val date = LocalDate.parse("2023-01-29")
        val result = teamSheetTestKit.runCommand[StatusReply[Done]](
          CreateTeamSheet(
            id = TEAM_SHEET_ID,
            date = date,
            team = MENS_FIRST_TEAM,
            opponent = FAREHAM_MENS_SECOND_TEAM,
            _
          )
        )

        result.reply shouldBe StatusReply.Ack
        result.event shouldBe TeamSheetCreated(
          TEAM_SHEET_ID,
          date,
          MENS_FIRST_TEAM,
          FAREHAM_MENS_SECOND_TEAM
        )
        result.state shouldBe CreatedTeamSheet(
          TEAM_SHEET_ID,
          date,
          MENS_FIRST_TEAM,
          FAREHAM_MENS_SECOND_TEAM
        )
      }
    }
  }

}
