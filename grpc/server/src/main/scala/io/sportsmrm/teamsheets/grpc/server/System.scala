package io.sportsmrm.teamsheets.grpc.server

import com.typesafe.config.Config
import io.sportsmrm.teamsheets.commands.Command
import io.sportsmrm.teamsheets.domain.TeamSheetEntity
import io.sportsmrm.teamsheets.events.Event
import io.sportsmrm.teamsheets.queries.TeamSheetsProjectionHandler
import org.apache.pekko.actor.typed.ActorSystem
import org.apache.pekko.actor.typed.scaladsl.Behaviors
import org.apache.pekko.cluster.sharding.typed.scaladsl.{
  ClusterSharding,
  Entity,
  EntityTypeKey,
  ShardedDaemonProcess
}
import org.apache.pekko.cluster.typed.{Cluster, Join}
import org.apache.pekko.persistence.query.Offset
import org.apache.pekko.persistence.query.typed.EventEnvelope
import org.apache.pekko.persistence.r2dbc.query.scaladsl.R2dbcReadJournal
import org.apache.pekko.projection.eventsourced.scaladsl.EventSourcedProvider
import org.apache.pekko.projection.r2dbc.scaladsl.R2dbcProjection
import org.apache.pekko.projection.scaladsl.SourceProvider
import org.apache.pekko.projection.{
  Projection,
  ProjectionBehavior,
  ProjectionId
}

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

  private def sourceProvider(
      sliceRange: Range
  ): SourceProvider[Offset, EventEnvelope[Event]] =
    EventSourcedProvider.eventsBySlices[Event](
      system,
      readJournalPluginId = R2dbcReadJournal.Identifier,
      entityType = "TeamSheet",
      sliceRange.min,
      sliceRange.max
    )

  private def projection(
      sliceRange: Range
  ): Projection[EventEnvelope[Event]] = {
    val minSlice = sliceRange.min
    val maxSlice = sliceRange.max
    val projectionId =
      ProjectionId("TeamSheets", s"team-sheets-$minSlice-$maxSlice")

    given system: ActorSystem[?] = actorSystem

    R2dbcProjection.exactlyOnce(
      projectionId,
      settings = None,
      sourceProvider(sliceRange),
      handler = () => new TeamSheetsProjectionHandler()
    )
  }

  private val numberOfSliceRanges: Int = 4
  private val sliceRanges = EventSourcedProvider.sliceRanges(
    system,
    R2dbcReadJournal.Identifier,
    numberOfSliceRanges
  )

  ShardedDaemonProcess(system).init(
    name = "TeamSheetsProjection",
    numberOfInstances = sliceRanges.size,
    behaviorFactory = i => ProjectionBehavior(projection(sliceRanges(i))),
    stopMessage = ProjectionBehavior.Stop
  )

  val correlatorLocator: CorrelatorLocator = (correlationId: String) =>
    sharding.entityRefFor(CorrelaterTypeKey, correlationId)
}
