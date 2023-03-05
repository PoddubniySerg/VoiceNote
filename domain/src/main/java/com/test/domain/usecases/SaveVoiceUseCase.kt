package com.test.domain.usecases

import com.test.domain.model.params.SaveVoiceParams
import com.test.domain.model.request.Voice
import com.test.domain.repositories.VoiceRepository
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

open class SaveVoiceUseCase @Inject constructor() {

    @Inject
    protected lateinit var voiceRepository: VoiceRepository

    private val timeFormat = "hh:mm"

    suspend fun execute(voice: Voice) {
        val dateTime = System.currentTimeMillis()
        val time = SimpleDateFormat(
            timeFormat,
            Locale.getDefault()
        ).format(dateTime)
        voiceRepository.saveVoice(
            SaveVoiceParams(
                voice.uri,
                voice.name,
                dateTime,
                time
            )
        )
    }
}