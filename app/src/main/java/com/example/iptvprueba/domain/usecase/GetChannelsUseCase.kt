package com.example.iptvprueba.domain.usecase

import com.example.iptvprueba.domain.model.Channel
import com.example.iptvprueba.domain.repository.ChannelRepository
import kotlinx.coroutines.flow.Flow

class GetChannelsUseCase(
    private val repository: ChannelRepository
) {
    operator fun invoke(): Flow<List<Channel>> = repository.getChannels()
}
