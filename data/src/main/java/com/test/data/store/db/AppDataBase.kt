package com.test.data.store.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.test.data.store.db.dto.VoiceDto

@Database(entities = [VoiceDto::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun voicesDao(): VoicesDao
}