package com.home.stateflowdemo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
class MainViewModel : ViewModel() {

    private companion object {
        const val START_SECONDS = 70L
        const val END_SECONDS = 0
    }

    private val _countdownStateFlow= MutableStateFlow(MainCountdownState())
    val countdownStateFlow: StateFlow<MainCountdownState> = _countdownStateFlow
    private val actionChannel =
        BroadcastChannel<MainCountdownAction>(Channel.BUFFERED)

    private var _countStateFlow = MutableStateFlow(MainAddState())
    val countStateFlow: StateFlow<MainAddState> = _countStateFlow

    init {
        actionChannel
            .asFlow()
            .onEach { MainCountdownAction.COUNTDOWN_START }
            .flatMapLatest { countdownStateFlow() }
            .onEach { _countdownStateFlow.value = it }
            .launchIn(viewModelScope)
    }

    private fun countdownStateFlow(): Flow<MainCountdownState> {
        return generateSequence(START_SECONDS - 1) {
            it - 1
        }.asFlow()
            .onEach { delay(1_000) }
            .onStart { emit(START_SECONDS) }
            .takeWhile { it >= END_SECONDS }
            .map {
                MainCountdownState(
                    watchState = MainCountdownState.WatchState.COUNTDOWN_START,
                    seconds = it
                )
            }
            .onCompletion {
                val state =
                    MainCountdownState(watchState = MainCountdownState.WatchState.COUNTDOWN_END)
                emit(state)
            }
    }

    suspend fun countdownStart(action: MainCountdownAction) = actionChannel.send(action)

    fun addCount() {
        val count = _countStateFlow.value.count + 1
        _countStateFlow.value = MainAddState(count)
    }
}