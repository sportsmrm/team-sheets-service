package io.sportsmrm.teamsheets.stesps

import io.cucumber.scala.{EN, ScalaDsl}
import io.sportsmrm.teamsheets.grpc.{CreateTeamSheetRequest, Opponent, Team, TeamSheetsService, TeamSheetsServiceClient}
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.grpc.GrpcClientSettings

import java.time.LocalDate
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, DurationInt}

object StepDefinitions {
  given sys: ActorSystem = ActorSystem("TeamSheetsClient")

  private val clientSettings = GrpcClientSettings.connectToServiceAt("127.0.0.1", 8080).withTls(false)
  private val service: TeamSheetsService = TeamSheetsServiceClient(clientSettings)

  private val defaultDuration: Duration = 5.seconds
}

class StepDefinitions extends ScalaDsl with EN {
  import StepDefinitions.*

  private var team: Option[Team] = None
  Given("""A team""") { () =>
    team match
      case None => {
        team = Some(Team(
          UUID.randomUUID(),
          "Men's 1st Team"
        ))
      }
  }

  private var opponent: Option[Opponent] = None
  Given("""An opponent""") { () =>
    opponent match
      case None =>
        opponent = Some(Opponent(
          UUID.randomUUID(),
          "Fareham Men's 2nd Team"
        ))
  }

  When("""I create a team sheet""") { () =>
      val result = for
        t <- team
        o <- opponent
      yield {
        service.createTeamSheet(CreateTeamSheetRequest(
          UUID.randomUUID().toString,
          "2024-05-11",
          team, opponent
        ))
      }

      result match
        case Some(future) =>
          Await.result(future, defaultDuration)
        case None =>
          throw new IllegalStateException("Give a team and opponent")
  }

  Then("""It should be in the list of teams sheets for that team""") { () =>
    throw NotImplementedError()
  }
}
