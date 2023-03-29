package io.sportsmrm.teamsheets.events

import io.sportsmrm.teamsheets.valueobjects.{Opponent, Team}

import java.time.LocalDate
import java.util.UUID

sealed trait Event
case class TeamSheetCreated(id: UUID, date: LocalDate, team: Team, opponent: Opponent) extends Event
