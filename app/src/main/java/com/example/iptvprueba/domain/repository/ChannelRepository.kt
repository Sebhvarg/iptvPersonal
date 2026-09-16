package com.example.iptvprueba.domain.repository

import com.example.iptvprueba.domain.model.Channel
import kotlinx.coroutines.flow.Flow

interface ChannelRepository {
    fun getChannels(): Flow<List<Channel>>
    suspend fun refreshChannels(): Result<List<Channel>>
}
