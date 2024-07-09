package io.sportsmrm.teamsheets.grpc.server

import picocli.CommandLine
import picocli.CommandLine.Command

object TeamSheetsServer {
  def main(args: Array[String]): Unit = {
    val cli = new CommandLine(new TeamSheetsServer())
    java.lang.System.exit(cli.execute(args*))
  }
}

@Command(subcommands =
  Array(
    classOf[Serve]
  )
)
class TeamSheetsServer {}
