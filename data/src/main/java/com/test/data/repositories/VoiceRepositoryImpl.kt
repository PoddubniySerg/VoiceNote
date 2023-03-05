package com.test.data.repositories

import com.test.data.DataApp
import com.test.data.model.VoiceRecordImpl
import com.test.data.store.db.dto.VoiceDto
import com.test.domain.entities.VoiceRecord
import com.test.domain.model.params.SaveVoiceParams
import com.test.domain.repositories.VoiceRepository
import java.time.Instant
import java.time.ZoneId

class VoiceRepositoryImpl : VoiceRepository {

    private val dataSource = DataApp.getDataBase().voicesDao()

    override suspend fun saveVoice(param: SaveVoiceParams) {
        dataSource.save(
            VoiceDto(
                param.uri,
                param.name,
                param.date,
                param.time
            )
        )
    }

    override suspend fun getVoices(): List<VoiceRecord> {
//        val mockList = mutableListOf<VoiceRecord>()
//        for (i in 0 until 20) {
//            mockList.add(
//                object : VoiceRecord {
//                    override val uri: String
//                        get() = ""
//                    override val name: String
//                        get() = "Very best film!"
//                    override val date: LocalDate
//                        get() = LocalDate.now()
//                    override val time: String
//                        get() = "15:32"
//                    override val duration: String
//                        get() = "06:23"
//
//                }
//            )
//        }
//        return mockList

        return dataSource.getVoices().map { voice ->
            VoiceRecordImpl(
                voice.uri,
                voice.name,
                Instant.ofEpochMilli(voice.date).atZone(ZoneId.systemDefault()).toLocalDate(),
                voice.time
            )
        }
    }
}