package com.test.domain.usecases

import com.test.domain.model.response.Voices
import com.test.domain.repositories.VoiceRepository
import javax.inject.Inject

class GetVoicesUseCase @Inject constructor() {

    @Inject
    protected lateinit var voiceRepository: VoiceRepository

    suspend fun execute(): Voices {
        return Voices(voiceRepository.getVoices())
    }
}