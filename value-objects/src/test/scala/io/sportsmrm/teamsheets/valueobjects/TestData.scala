package io.sportsmrm.teamsheets.valueobjects

import java.util.UUID

object TestData {
  val FAREHAM_MENS_SECOND_TEAM: Opponent = Opponent(
    id = UUID.randomUUID(),
    displayName = "Fareham 2nd Team"
  )

  val MENS_FIRST_TEAM: Team = Team(
    id = UUID.randomUUID(),
    displayName = "Men's 1st Team"
  )

}
