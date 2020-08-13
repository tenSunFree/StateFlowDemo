package com.home.stateflowdemo.viewmodel

data class MainCountdownState(
    val watchState: WatchState = WatchState.COUNTDOWN_START,
    val seconds: Long = 70
) {
    enum class WatchState {
        COUNTDOWN_START,
        COUNTDOWN_END
    }
}

enum class MainCountdownAction {
    COUNTDOWN_START
}

data class MainAddState(
    var count: Long = 0
)

enum class MainAddAction {
    ADD_COUNT
}