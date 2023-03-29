package io.sportsmrm.teamsheets.commands

import io.sportsmrm.teamsheets.valueobjects.{Opponent, Team}
import org.apache.pekko.Done
import org.apache.pekko.actor.typed.ActorRef
import org.apache.pekko.pattern.StatusReply

import java.time.LocalDate
import java.util.UUID
import scala.annotation.unused

sealed trait Command[T](@unused replyTo: ActorRef[StatusReply[T]])

case class CreateTeamSheet(id: UUID, date: LocalDate, team: Team, opponent: Opponent, replyTo: ActorRef[StatusReply[Done]]) extends Command[Done](replyTo)
