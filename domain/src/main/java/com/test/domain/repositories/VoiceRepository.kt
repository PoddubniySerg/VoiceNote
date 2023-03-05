package com.test.domain.repositories

import com.test.domain.entities.VoiceRecord
import com.test.domain.model.params.SaveVoiceParams

interface VoiceRepository {

    suspend fun saveVoice(param: SaveVoiceParams)

    suspend fun getVoices(): List<VoiceRecord>
}