package com.example.iptvprueba.domain.model

sealed interface PlaybackState {
    data object Idle : PlaybackState
    data object Buffering : PlaybackState
    data object Ready : PlaybackState
    data object Ended : PlaybackState
    data class Error(val message: String) : PlaybackState
}
