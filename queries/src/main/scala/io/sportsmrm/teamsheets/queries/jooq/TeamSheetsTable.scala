package io.sportsmrm.teamsheets.queries.jooq

import org.jooq.TableField
import org.jooq.impl.DSL.name
import org.jooq.impl.{CustomRecord, CustomTable, SQLDataType}

import java.time.LocalDate
import java.util.UUID

class TeamSheetsRecord extends CustomRecord[TeamSheetsRecord](TEAM_SHEETS) {}

class TeamSheetsTable
    extends CustomTable[TeamSheetsRecord](name("team_sheets")) {
  val ID: TableField[TeamSheetsRecord, UUID] =
    createField(name("id"), SQLDataType.UUID)
  val SEQUENCE_NR: TableField[TeamSheetsRecord, java.lang.Long] =
    createField(name("sequence_nr"), SQLDataType.BIGINT)
  val DATE: TableField[TeamSheetsRecord, LocalDate] =
    createField(name("date"), SQLDataType.LOCALDATE)
  val TEAM_ID: TableField[TeamSheetsRecord, UUID] =
    createField(name("team_id"), SQLDataType.UUID)
  val TEAM_NAME: TableField[TeamSheetsRecord, String] =
    createField(name("team_name"), SQLDataType.VARCHAR)
  val OPPONENT_ID: TableField[TeamSheetsRecord, UUID] =
    createField(name("opponent_id"), SQLDataType.UUID)
  val OPPONENT_NAME: TableField[TeamSheetsRecord, String] =
    createField(name("opponent_name"), SQLDataType.VARCHAR)

  override def getRecordType: Class[? <: TeamSheetsRecord] =
    classOf[TeamSheetsRecord]
}

val TEAM_SHEETS = new TeamSheetsTable()
