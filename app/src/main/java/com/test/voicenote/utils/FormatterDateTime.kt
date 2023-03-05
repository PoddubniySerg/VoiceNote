package com.test.voicenote.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.milliseconds

class FormatterDateTime {

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy ")

    fun getDate(date: LocalDate): String = date.format(formatter)

    fun getDuration(duration: Int): String {
        duration.milliseconds.toComponents { minutes, seconds, _ ->
            return String.format("%02d:%02d", minutes, seconds)
        }
    }
}