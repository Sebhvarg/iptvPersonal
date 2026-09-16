package com.example.iptvprueba.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.iptvprueba.domain.model.Channel
import com.example.iptvprueba.domain.usecase.GetChannelsUseCase
import com.example.iptvprueba.domain.usecase.RefreshChannelsUseCase
import com.example.iptvprueba.player.TvPlayerController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class IptvViewModel(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val refreshChannelsUseCase: RefreshChannelsUseCase,
    val playerController: TvPlayerController
) : ViewModel() {

    private val _uiState = MutableStateFlow(IptvUiState())
    val uiState: StateFlow<IptvUiState> = _uiState.asStateFlow()

    init {
        observePlaybackState()
        observeChannels()
        loadChannels()
    }

    private fun observePlaybackState() {
        viewModelScope.launch {
            playerController.playbackState.collect { state ->
                _uiState.update { it.copy(playbackState = state) }
            }
        }
    }

    private fun observeChannels() {
        viewModelScope.launch {
            getChannelsUseCase().collect { channelsList ->
                if (channelsList.isNotEmpty()) {
                    val categories = listOf("Todos") + channelsList.map { it.groupTitle }.distinct()
                    _uiState.update { currentState ->
                        val filtered = filterChannels(channelsList, currentState.selectedCategory)
                        val initialChannel = currentState.currentChannel ?: channelsList.firstOrNull()
                        currentState.copy(
                            isLoading = false,
                            channels = channelsList,
                            filteredChannels = filtered,
                            categories = categories,
                            currentChannel = initialChannel,
                            errorMessage = null
                        )
                    }

                    if (playerController.currentChannel.value == null && channelsList.isNotEmpty()) {
                        playChannel(channelsList.first())
                    }
                }
            }
        }
    }

    fun loadChannels() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = refreshChannelsUseCase()
            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Error al cargar la lista de canales"
                    )
                }
            }
        }
    }

    fun refreshChannelsList() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRefreshing = true,
                    refreshMessage = "Descargando canales desde el repositorio..."
                )
            }
            val result = refreshChannelsUseCase()
            result.onSuccess { channelsList ->
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        refreshMessage = "Lista actualizada (${channelsList.size} canales cargados)"
                    )
                }
                delay(3000)
                _uiState.update { it.copy(refreshMessage = null) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        refreshMessage = "Error al conectar con el repositorio"
                    )
                }
                delay(3000)
                _uiState.update { it.copy(refreshMessage = null) }
            }
        }
    }

    fun selectChannel(channel: Channel) {
        val index = _uiState.value.channels.indexOf(channel)
        _uiState.update {
            it.copy(
                currentChannel = channel,
                selectedChannelIndex = if (index >= 0) index else it.selectedChannelIndex
            )
        }
        playChannel(channel)
    }

    fun nextChannel() {
        val channels = _uiState.value.channels
        if (channels.isEmpty()) return
        val currentIndex = _uiState.value.channels.indexOf(_uiState.value.currentChannel)
        val nextIndex = if (currentIndex < channels.size - 1) currentIndex + 1 else 0
        selectChannel(channels[nextIndex])
    }

    fun previousChannel() {
        val channels = _uiState.value.channels
        if (channels.isEmpty()) return
        val currentIndex = _uiState.value.channels.indexOf(_uiState.value.currentChannel)
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else channels.size - 1
        selectChannel(channels[prevIndex])
    }

    fun selectCategory(category: String) {
        _uiState.update { currentState ->
            val filtered = filterChannels(currentState.channels, category)
            currentState.copy(
                selectedCategory = category,
                filteredChannels = filtered
            )
        }
    }

    fun toggleOverlay() {
        _uiState.update { it.copy(isOverlayVisible = !it.isOverlayVisible) }
    }

    fun setOverlayVisible(visible: Boolean) {
        _uiState.update { it.copy(isOverlayVisible = visible) }
    }

    fun retryPlayback() {
        playerController.retry()
    }

    private fun playChannel(channel: Channel) {
        playerController.playChannel(channel)
    }

    private fun filterChannels(channels: List<Channel>, category: String): List<Channel> {
        return if (category == "Todos") {
            channels
        } else {
            channels.filter { it.groupTitle.equals(category, ignoreCase = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerController.release()
    }
}

class IptvViewModelFactory(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val refreshChannelsUseCase: RefreshChannelsUseCase,
    private val playerController: TvPlayerController
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IptvViewModel::class.java)) {
            return IptvViewModel(
                getChannelsUseCase = getChannelsUseCase,
                refreshChannelsUseCase = refreshChannelsUseCase,
                playerController = playerController
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
