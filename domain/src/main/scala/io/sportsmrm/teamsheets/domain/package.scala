package io.sportsmrm.teamsheets

import io.sportsmrm.teamsheets.events.Event

package object domain {
  type ReplyEffect = org.apache.pekko.persistence.typed.scaladsl.ReplyEffect[Event, TeamSheetState]
}