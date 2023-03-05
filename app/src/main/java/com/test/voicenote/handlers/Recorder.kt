package com.test.voicenote.handlers

import android.app.Application
import android.media.MediaRecorder
import android.os.Build
import javax.inject.Inject

open class Recorder @Inject constructor() {

    @Inject
    protected lateinit var application: Application

    private var recorder: MediaRecorder? = null

    private var _isStarted = false
    val isStarted get() = _isStarted

    fun start(uri: String) {
        recorder =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) MediaRecorder(application)
            else MediaRecorder()
        println()
        recorder?.let { mediaRecorder ->
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            mediaRecorder.setOutputFile(uri)
            mediaRecorder.prepare()
            mediaRecorder.start()
            _isStarted = true
        }
    }

    fun stop() {
        recorder?.let { mediaRecorder ->
            if (isStarted) {
                mediaRecorder.stop()
                mediaRecorder.release()
                _isStarted = false
            }
        }
        recorder = null
    }
}