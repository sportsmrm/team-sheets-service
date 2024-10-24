package io.sportsmrm.teamsheets.grpc

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

import java.util.UUID

class UUIDTypeMapperSpec extends AnyFlatSpec {
  "uuidTypeMapper" should "map a random UUID" in {
    val uuid = UUID.randomUUID()

    val str = uuidTypeMapper.toBase(uuid)
    val uuid2 = uuidTypeMapper.toCustom(str)

    uuid2 shouldBe uuid
  }
}
