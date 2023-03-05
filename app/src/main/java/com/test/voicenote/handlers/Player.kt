package com.test.voicenote.handlers

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.test.voicenote.utils.StatesPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

open class Player(private val context: Context, private val uri: String) {

    private var player: MediaPlayer? = null

    private var _state = StatesPlayer.STOPPED
    val state get() = _state

    private val _isStarted = MutableStateFlow(false)
    val isStarted = _isStarted.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _position = MutableStateFlow(0)
    val position = _position.asStateFlow()

    fun seekTo(position: Int) = player?.seekTo(position)

    fun getDuration(scope: CoroutineScope): Int {
        observePlaying(scope)
        player = MediaPlayer.create(context, Uri.parse(uri))
        return player!!.duration
    }

    fun start() {
        if (player == null) player = MediaPlayer.create(context, Uri.parse(uri))
        player?.let { mediaPlayer ->
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener {
                player = null
                _isStarted.value = false
                _isPlaying.value = false
                _state = StatesPlayer.STOPPED
            }
            _state = StatesPlayer.STARTED
            _isStarted.value = true
            _isPlaying.value = true
        }
    }

    fun pause() {
        player?.let { mediaPlayer ->
            mediaPlayer.pause()
            _state = StatesPlayer.PAUSED
            _isPlaying.value = false
        }
    }

    fun resume() {
        player?.let { mediaPlayer ->
            mediaPlayer.start()
            _state = StatesPlayer.STARTED
            _isPlaying.value = true
        }
    }

    private fun observePlaying(scope: CoroutineScope) {
        scope.launch {
            isPlaying.onEach {
                while (it) {
                    _position.value = player?.currentPosition ?: 0
                    delay(10)
                }
            }.launchIn(scope)
        }
    }
}