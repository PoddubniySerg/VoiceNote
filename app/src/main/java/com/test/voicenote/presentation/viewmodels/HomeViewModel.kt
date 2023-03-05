package com.test.voicenote.presentation.viewmodels

import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.domain.entities.VoiceRecord
import com.test.domain.model.request.Voice
import com.test.domain.usecases.GetVoicesUseCase
import com.test.domain.usecases.SaveVoiceUseCase
import com.test.voicenote.handlers.Recorder
import com.test.voicenote.utils.LoadStates
import com.test.voicenote.utils.UriGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
open class HomeViewModel @Inject constructor() : ViewModel() {

    @Inject
    protected lateinit var saveVoiceUseCase: SaveVoiceUseCase

    @Inject
    protected lateinit var getVoicesUseCase: GetVoicesUseCase

    @Inject
    protected lateinit var recorder: Recorder

    @Inject
    protected lateinit var uriGenerator: UriGenerator

    private val _recorderChannel = Channel<Boolean>()
    val recorderStateFlow = _recorderChannel.receiveAsFlow()

    private val _loadStateFlow = MutableStateFlow(LoadStates.COMPLETE)
    val loadStateFlow = _loadStateFlow.asStateFlow()

    private val _voicesChannel = Channel<List<VoiceRecord>>()
    val voiceFlow = _voicesChannel.receiveAsFlow()

    private val _durationStateFlow = MutableStateFlow(0)
    val durationStateFlow = _durationStateFlow.asStateFlow()

    private val _progressStateFlow = MutableStateFlow(0)
    val progressStateFlow = _progressStateFlow.asStateFlow()

    private val _seekBarIsEnabledFlow = MutableStateFlow(false)
    val seekBarIsEnabledFlow = _seekBarIsEnabledFlow.asStateFlow()

    private var uri: String? = null

    fun getSavedVoices() {
        viewModelScope.launch {
            _loadStateFlow.value = LoadStates.LOADING
            try {
                _voicesChannel.send(getVoicesUseCase.execute().list)
            } catch (e: Exception) {
//                TODO handle
                print(e.stackTrace)
            } finally {
                _loadStateFlow.value = LoadStates.COMPLETE
            }
        }
    }

    fun recordOnPressed() {
        viewModelScope.launch {
            try {
                _loadStateFlow.value = LoadStates.LOADING
                if (recorder.isStarted) recorder.stop()
                else {
                    uri = "${Environment.getExternalStorageDirectory()}/${uriGenerator.execute()}"
                    recorder.start(uri ?: return@launch)
                }
                _recorderChannel.send(recorder.isStarted)
            } catch (e: Exception) {
//                TODO handle
            } finally {
                _loadStateFlow.value = LoadStates.COMPLETE
            }
        }
    }

    fun saveVoice(name: String) {
        try {
            viewModelScope.launch {
                _loadStateFlow.value = LoadStates.LOADING
                saveVoiceUseCase.execute(
                    Voice(
                        name,
                        uri ?: return@launch
                    )
                )
                uri = null
                getSavedVoices()
            }
        } catch (e: Exception) {
//                TODO handle
        } finally {
            _loadStateFlow.value = LoadStates.COMPLETE
        }
    }

    fun playPauseOnClick() {}

    fun seekTo(position: Int) {}
}