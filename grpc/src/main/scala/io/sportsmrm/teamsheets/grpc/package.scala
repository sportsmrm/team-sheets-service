package io.sportsmrm.teamsheets

import scalapb.TypeMapper

import java.nio.ByteBuffer
import java.util.{Base64, UUID}

package object grpc {
  given uuidTypeMapper: TypeMapper[String, UUID] = TypeMapper[String, UUID](grpcString => {
    val bb = ByteBuffer.wrap(Base64.getUrlDecoder.decode(grpcString))
    new UUID(bb.getLong(), bb.getLong())
  })(uuid => {
    val bb = ByteBuffer.allocate(16)
    bb.putLong(uuid.getMostSignificantBits)
    bb.putLong(uuid.getLeastSignificantBits)
    Base64.getUrlEncoder.encodeToString(bb.array())
  })
}
