package io.sportsmrm.teamsheets.stesps

import io.cucumber.scala.{EN, ScalaDsl}
import io.sportsmrm.teamsheets.grpc.{CreateTeamSheetRequest, CreateTeamSheetResponse, ListTeamSheetsRequest, Opponent, Team, TeamSheet, TeamSheetsService, TeamSheetsServiceClient}
import org.apache.pekko.actor.{ActorSystem, Scheduler}
import org.apache.pekko.grpc.GrpcClientSettings
import org.apache.pekko.pattern.retry
import org.scalactic.Equality
import org.scalatest.Succeeded
import org.scalatest.compatible.Assertion
import org.scalatest.enablers.Containing
import org.scalatest.matchers.should.Matchers.*

import java.nio.ByteBuffer
import java.util.{Base64, UUID}
import scala.Array.emptyByteArray
import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.{Duration, DurationInt}


object StepDefinitions {
  given sys: ActorSystem = ActorSystem("TeamSheetsClient")

  private val clientSettings = GrpcClientSettings.connectToServiceAt("127.0.0.1", 8080).withTls(false)
  private val service: TeamSheetsService = TeamSheetsServiceClient(clientSettings)

  private val defaultDuration: Duration = 5.minutes

  def uuidToBase64(uuid: UUID): String = {
    val bb = ByteBuffer.wrap(new Array[Byte](16))
    bb.putLong(uuid.getMostSignificantBits)
    bb.putLong(uuid.getLeastSignificantBits)
    val encoder = Base64.getUrlEncoder
    encoder.encodeToString(bb.array())
  }
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

  private var createResponse: Option[CreateTeamSheetResponse] = None
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
          createResponse = Some(Await.result(future, defaultDuration))
        case None =>
          throw new IllegalStateException("Give a team and opponent")
  }

  Then("""It should be in the list of teams sheets for that team""") { () =>
    given ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    given scheduler: Scheduler = StepDefinitions.sys.scheduler
    given Equality[TeamSheet] = (teamSheet: TeamSheet, other: Any) => other match {
      case uuid: UUID => teamSheet.id === uuid.toString
      case _: AnyVal => false
    }

    val result = for {
      response <- createResponse
      t <- team
    } yield {
      retry(attempt=() => { 
        service.listTeamSheets(ListTeamSheetsRequest(parent = "teams/" + uuidToBase64(t.id)))
          .map(result =>
            result.teamSheets `should` contain(response.id)
          )
      }, shouldRetry=(assertion: Assertion, exc: Throwable) => {
        assertion ne Succeeded
      }, attempts=10, delay=200.milliseconds)
    }

    result match {
      case None => throw new IllegalArgumentException("Give a team and create a team sheet")
      case Some(future) => Await.result(future, defaultDuration)
    }
  }
}
