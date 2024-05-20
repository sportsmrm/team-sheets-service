package io.sportsmrm.teamsheets.grpc.server

import com.typesafe.config.ConfigFactory
import io.sportsmrm.teamsheets.grpc.TeamSheetsServiceHandler
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.http.scaladsl.Http
import org.apache.pekko.http.scaladsl.model.{HttpRequest, HttpResponse}
import picocli.CommandLine
import picocli.CommandLine.Command

import scala.concurrent.Future

object TeamSheetsServer {
  def main(args: Array[String]): Unit = {
    val cli = new CommandLine(new TeamSheetsServer())
    System.exit(cli.execute(args*))
  }
}


@Command(subcommands = Array(
  classOf[Serve]
))
class TeamSheetsServer {}

