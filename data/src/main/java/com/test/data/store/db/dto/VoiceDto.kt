package com.test.data.store.db.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voices")
class VoiceDto(
    @PrimaryKey
    @ColumnInfo(name = "uri")
    val uri: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "date")
    val date: Long,
    @ColumnInfo(name = "time")
    val time: String

)