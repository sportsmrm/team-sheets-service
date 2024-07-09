package io.sportsmrm.teamsheets.grpc.server

import com.typesafe.config.Config
import io.sportsmrm.teamsheets.commands.Command
import io.sportsmrm.teamsheets.domain.TeamSheetEntity
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.sharding.typed.scaladsl.{
  ClusterSharding,
  Entity,
  EntityTypeKey
}
import org.apache.pekko.cluster.typed.{Cluster, Join}

import java.util.UUID
import scala.concurrent.ExecutionContextExecutor

object System {
  private val CorrelaterTypeKey = EntityTypeKey[Correlator.Command]("Creator")
  private val TeamSheetTypeKey = EntityTypeKey[Command[?]]("teamSheet")

  def apply(config: Config): System = {
    val system = ActorSystem(Behaviors.empty, "TeamSheetsSystem", config)

    val cluster = Cluster(system)
    cluster.manager ! Join(cluster.selfMember.address)

    new System(system)
  }
}

class System private (private val system: ActorSystem[?]) {
  import System.*

  val actorSystem: ActorSystem[?] = system
  val classicSystem: org.apache.pekko.actor.ActorSystem = system.classicSystem
  val executionContext: ExecutionContextExecutor = system.executionContext

  val sharding: ClusterSharding = ClusterSharding(system)

  sharding.init(
    Entity(TeamSheetTypeKey)(createBehavior =
      entityContext => TeamSheetEntity(UUID.fromString(entityContext.entityId))
    )
  )
  private val teamSheetLocator = (teamSheetId: UUID) => {
    sharding.entityRefFor(TeamSheetTypeKey, teamSheetId.toString)
  }

  sharding.init(
    Entity(CorrelaterTypeKey)(createBehavior =
      Correlator(this.teamSheetLocator)
    )
  )
  val correlatorLocator: CorrelatorLocator = (correlationId: String) =>
    sharding.entityRefFor(CorrelaterTypeKey, correlationId)
}
