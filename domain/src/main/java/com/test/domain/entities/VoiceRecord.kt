package com.test.domain.entities

import java.time.LocalDate

interface VoiceRecord {

    val uri: String
    val name: String
    val date: LocalDate
    val time: String
}