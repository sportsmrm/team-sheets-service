package io.sportsmrm.teamsheets.valueobjects

import java.time.LocalDate
import java.util.UUID

case class TeamSheet (id: UUID, date: LocalDate, team: Team, opponent: Opponent)
