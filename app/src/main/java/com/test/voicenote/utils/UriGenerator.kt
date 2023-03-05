package com.test.voicenote.utils

import android.icu.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class UriGenerator @Inject constructor() {

    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-hh-mm-ss"
    }

    fun execute(): String {
        val dateTime = System.currentTimeMillis()
        return SimpleDateFormat(
            FILENAME_FORMAT,
            Locale.getDefault()
        ).format(dateTime)
    }
}