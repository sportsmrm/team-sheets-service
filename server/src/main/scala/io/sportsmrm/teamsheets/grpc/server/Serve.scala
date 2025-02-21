package io.sportsmrm.teamsheets.grpc.server

import com.typesafe.config.{
  ConfigFactory,
  ConfigParseOptions,
  ConfigResolveOptions
}
import io.grpc.Server
import io.grpc.netty.NettyServerBuilder
import io.sportsmrm.teamsheets.grpc.TeamSheetsServiceGrpc
import io.sportsmrm.teamsheets.queries.TeamSheetsRepository
import io.sportsmrm.util.config.DockerSecretConfigResolver
import org.apache.pekko.Done
import org.apache.pekko.actor.{ActorSystem, CoordinatedShutdown}
import org.slf4j.{Logger, LoggerFactory}
import picocli.CommandLine.{Command, Option}

import java.net.InetSocketAddress
import java.util.concurrent.Callable
import scala.annotation.nowarn
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, blocking}

@Command(
  name = "serve",
  mixinStandardHelpOptions = true,
  description = Array("Start the Team Sheets Service")
)
class Serve extends Callable[Int] {
  private val logger: Logger = LoggerFactory.getLogger(classOf[Serve])

  @Option(
    names = Array("--http-interface"),
    description = Array("interface to listen for HTTP requests on")
  )
  @nowarn("msg=unset private variable, consider using an immutable val instead")
  private var interface: String = "127.0.0.1"

  @Option(
    names = Array("--http-port"),
    description = Array("port to listen for HTTP request on on")
  )
  @nowarn("msg=unset private variable, consider using an immutable val instead")
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

    val server: Server = NettyServerBuilder
      .forAddress(new InetSocketAddress(interface, port))
      .addService(
        TeamSheetsServiceGrpc.bindService(
          TeamSheetsServiceImpl(
            system.correlatorLocator,
            TeamSheetsRepository(system.actorSystem),
            system.actorSystem
          ),
          ec
        )
      )
      .build()

    server.start()

    logger
      .atInfo()
      .log("Started GRPC server, listening on {}:{}", interface, port)

    CoordinatedShutdown(classicSystem).addTask(
      CoordinatedShutdown.PhaseServiceUnbind,
      "shutdownGrpcServer"
    ) { () =>
      Future {
        blocking {
          logger
            .atInfo()
            .log("Initiating graceful shutdown of GRPC server")

          server.shutdown()
          Done
        }
      }
    }

    CoordinatedShutdown(classicSystem)
      .addTask(
        CoordinatedShutdown.PhaseServiceRequestsDone,
        "awaitGrpcServerTerminsaion"
      ) { () =>
        Future {
          blocking {
            logger
              .atInfo()
              .log("Awaiting termination of GRPC server")

            server.awaitTermination()

            logger
              .atInfo()
              .log("GRPC server terminated")
            Done
          }
        }
      }

    CoordinatedShutdown(classicSystem).addTask(
      CoordinatedShutdown.PhaseServiceStop,
      "forceShutdownGrpcServer"
    ) { () =>
      Future {
        blocking {
          if (!server.isTerminated) {
            logger
              .atInfo()
              .log("Initiating forceful shutdown of GRPC server")

            server.shutdownNow()
            server.awaitTermination()

            logger
              .atInfo()
              .log("GRPC server terminated")

          }
          Done
        }
      }
    }

    Await.result(system.actorSystem.whenTerminated, Duration.Inf)

    0
  }
}
