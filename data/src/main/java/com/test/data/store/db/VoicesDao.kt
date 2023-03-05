package com.test.data.store.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.test.data.store.db.dto.VoiceDto

@Dao
interface VoicesDao {

    @Query("SELECT * FROM voices")
    suspend fun getVoices(): List<VoiceDto>

    @Insert
    suspend fun save(voice: VoiceDto)
}