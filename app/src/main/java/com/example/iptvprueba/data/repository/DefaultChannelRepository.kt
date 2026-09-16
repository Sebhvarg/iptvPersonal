package com.example.iptvprueba.data.repository

import com.example.iptvprueba.data.datasource.DefaultPlaylistDataSource
import com.example.iptvprueba.data.datasource.PlaylistDataSource
import com.example.iptvprueba.data.parser.DefaultM3uParser
import com.example.iptvprueba.data.parser.M3uParser
import com.example.iptvprueba.domain.model.Channel
import com.example.iptvprueba.domain.repository.ChannelRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class DefaultChannelRepository(
    private val playlistDataSource: PlaylistDataSource = DefaultPlaylistDataSource(),
    private val parser: M3uParser = DefaultM3uParser(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ChannelRepository {

    private val _channelsFlow = MutableStateFlow<List<Channel>>(emptyList())

    override fun getChannels(): Flow<List<Channel>> = _channelsFlow.asStateFlow()

    override suspend fun refreshChannels(): Result<List<Channel>> = withContext(ioDispatcher) {
        runCatching {
            val rawPlaylist = playlistDataSource.getPlaylistRaw()
            val parsedChannels = parser.parse(rawPlaylist)
            _channelsFlow.value = parsedChannels
            parsedChannels
        }
    }
}
