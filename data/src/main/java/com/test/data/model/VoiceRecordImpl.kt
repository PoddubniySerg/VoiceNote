package com.test.data.model

import com.test.domain.entities.VoiceRecord
import java.time.LocalDate

class VoiceRecordImpl(
    override val uri: String,
    override val name: String,
    override val date: LocalDate,
    override val time: String
) : VoiceRecord