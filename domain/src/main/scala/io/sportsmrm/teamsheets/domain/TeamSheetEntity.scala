package io.sportsmrm.teamsheets.domain

import io.sportsmrm.teamsheets.commands.Command
import io.sportsmrm.teamsheets.events.Event
import org.apache.pekko.actor.typed.Behavior
import org.apache.pekko.persistence.typed.PersistenceId
import org.apache.pekko.persistence.typed.scaladsl.EventSourcedBehavior

import java.util.UUID

object TeamSheetEntity {
  def apply(id: UUID): Behavior[Command[?]] =
    EventSourcedBehavior.withEnforcedReplies[Command[?], Event, TeamSheetState](
      persistenceId = PersistenceId("TeamSheet", id.toString),
      EmptyTeamSheet,
      (state, cmd) => state.applyCommand(cmd),
      (state, evt) => state.applyEvent(evt)
    )
}
