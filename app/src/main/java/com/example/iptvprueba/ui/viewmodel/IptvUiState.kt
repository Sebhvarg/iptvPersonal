package com.example.iptvprueba.ui.viewmodel

import com.example.iptvprueba.domain.model.Channel
import com.example.iptvprueba.domain.model.PlaybackState

data class IptvUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val refreshMessage: String? = null,
    val channels: List<Channel> = emptyList(),
    val filteredChannels: List<Channel> = emptyList(),
    val categories: List<String> = emptyList(),
    val selectedCategory: String = "Todos",
    val selectedChannelIndex: Int = 0,
    val currentChannel: Channel? = null,
    val playbackState: PlaybackState = PlaybackState.Idle,
    val isOverlayVisible: Boolean = true,
    val errorMessage: String? = null
)
