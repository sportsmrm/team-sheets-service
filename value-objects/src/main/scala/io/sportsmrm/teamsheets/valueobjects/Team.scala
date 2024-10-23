package io.sportsmrm.teamsheets.valueobjects

import java.nio.ByteBuffer
import java.util.{Base64,UUID}

object TeamId {
  private val PREFIX = "teams/"

  def apply(id: String): UUID = {
    val byteString = if (id.startsWith(PREFIX)) {
      id.substring(PREFIX.length, id.length)
    } else {
      id
    }

    val bb = ByteBuffer.wrap(Base64.getUrlDecoder.decode(byteString))
    new UUID(bb.getLong(), bb.getLong())
  }
}

type TeamId = UUID

case class Team(id: TeamId, displayName: String)
