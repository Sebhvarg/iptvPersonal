package com.example.iptvprueba.domain.usecase

import com.example.iptvprueba.domain.model.Channel
import com.example.iptvprueba.domain.repository.ChannelRepository

class RefreshChannelsUseCase(
    private val repository: ChannelRepository
) {
    suspend operator fun invoke(): Result<List<Channel>> = repository.refreshChannels()
}
