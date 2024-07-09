package io.sportsmrm.teamsheets.grpc.server

import com.typesafe.config.{
  ConfigFactory,
  ConfigParseOptions,
  ConfigResolveOptions
}
import io.sportsmrm.teamsheets.grpc.TeamSheetsServiceHandler
import io.sportsmrm.util.config.DockerSecretConfigResolver
import org.apache.pekko.actor.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{HttpRequest, HttpResponse}
import org.slf4j.{Logger, LoggerFactory}
import picocli.CommandLine.{Command, Option}

import java.util.concurrent.Callable
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, ExecutionContext, Future}

@Command(
  name = "serve",
  mixinStandardHelpOptions = true,
  description = Array("Start the Team Sheets Service")
)
class Serve extends Callable[Int] {

  private val log: Logger = LoggerFactory.getLogger(classOf[Serve])

  @Option(
    names = Array("--http-interface"),
    description = Array("interface to listen for HTTP requests on")
  )
  private var interface: String = "127.0.0.1"

  @Option(
    names = Array("--http-port"),
    description = Array("port to listen for HTTP request on on")
  )
  private var port: Int = 8080

  override def call(): Int = {
    val config = ConfigFactory.load(
      ConfigParseOptions.defaults(),
      ConfigResolveOptions
        .defaults()
        .appendResolver(new DockerSecretConfigResolver())
    )

    val system = System(config)
    given ec: ExecutionContext = system.executionContext
    given classicSystem: ActorSystem = system.classicSystem

    val service: HttpRequest => Future[HttpResponse] =
      TeamSheetsServiceHandler(
        TeamSheetsServiceImpl(system.correlatorLocator, system.actorSystem)
      )

    val binding = Http(system.classicSystem)
      .newServerAt(
        interface,
        port
      )
      .bind(service)
      .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 30.seconds))

    binding.foreach { binding =>
      log
        .atInfo()
        .setMessage("gRPC server bound to {}")
        .addArgument(binding.localAddress)
        .log()
    }

    Await.result(system.actorSystem.whenTerminated, Duration.Inf)

    0
  }
}
