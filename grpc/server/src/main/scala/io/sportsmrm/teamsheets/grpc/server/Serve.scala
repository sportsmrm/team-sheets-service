package io.sportsmrm.teamsheets.grpc.server

import com.typesafe.config.ConfigFactory
import io.sportsmrm.teamsheets.grpc.TeamSheetsServiceHandler
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{HttpRequest, HttpResponse}
import org.slf4j.{Logger, LoggerFactory}
import picocli.CommandLine.{Command, Option}

import java.util.concurrent.Callable
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContext, Future}

@Command(name = "serve", mixinStandardHelpOptions = true,
  description = Array("Start the Team Sheets Service"))
class Serve extends Callable[Int] {

  private val log: Logger = LoggerFactory.getLogger(classOf[Serve])

  @Option(names = Array("--http-interface"), description = Array("interface to listen for HTTP requests on"))
  private var interface: String = "127.0.0.1"

  @Option(names=Array("--http-port"), description = Array("port to listen for HTTP request on on"))
  private var port: Int = 8080

  override def call(): Int = {
    val config = ConfigFactory.defaultApplication()

    given system: ActorSystem = ActorSystem("TeamSheetsService", config)
    given ec: ExecutionContext = system.dispatcher

    val service: HttpRequest => Future[HttpResponse] =
      TeamSheetsServiceHandler(TeamSheetsServiceImpl(system))(system.classicSystem)

    val binding = Http(system.classicSystem)
      .newServerAt(
        interface,
        port
      ).bind(service)
      .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 30.seconds))

    binding.foreach { binding =>
      log.atInfo()
        .setMessage("gRPC server bound to {}")
        .addArgument(binding.localAddress)
        .log()
    }

    Await.result(system.whenTerminated, Duration.Inf)

    0
  }
}
