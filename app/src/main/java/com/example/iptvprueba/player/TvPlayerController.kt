package com.example.iptvprueba.player

import android.content.Context
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.example.iptvprueba.domain.model.Channel
import com.example.iptvprueba.domain.model.PlaybackState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface TvPlayerController {
    val player: ExoPlayer
    val playbackState: StateFlow<PlaybackState>
    val isPlaying: StateFlow<Boolean>
    val currentChannel: StateFlow<Channel?>

    fun playChannel(channel: Channel)
    fun retry()
    fun pause()
    fun play()
    fun release()
}

@OptIn(UnstableApi::class)
class DefaultTvPlayerController(
    private val context: Context
) : TvPlayerController {

    private val tag = "TvPlayerController"

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    override val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentChannel = MutableStateFlow<Channel?>(null)
    override val currentChannel: StateFlow<Channel?> = _currentChannel.asStateFlow()

    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15000)
        .setReadTimeoutMs(15000)
        .setUserAgent("Mozilla/5.0 (Linux; Android TV) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")

    private val mediaSourceFactory = DefaultMediaSourceFactory(httpDataSourceFactory)

    private val loadControl = DefaultLoadControl.Builder()
        .setBufferDurationsMs(
            2500,  // minBufferMs
            15000, // maxBufferMs
            1000,  // bufferForPlaybackMs
            2000   // bufferForPlaybackAfterRebufferMs
        )
        .build()

    private val renderersFactory = DefaultRenderersFactory(context)
        .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF)

    override val player: ExoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(mediaSourceFactory)
        .setLoadControl(loadControl)
        .setRenderersFactory(renderersFactory)
        .build()
        .apply {
            playWhenReady = true
            addListener(createPlayerListener())
        }

    private fun createPlayerListener(): Player.Listener {
        return object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                Log.d(tag, "Playback state changed: $state")
                when (state) {
                    Player.STATE_IDLE -> {
                        if (_playbackState.value !is PlaybackState.Error) {
                            _playbackState.value = PlaybackState.Idle
                        }
                    }
                    Player.STATE_BUFFERING -> {
                        _playbackState.value = PlaybackState.Buffering
                    }
                    Player.STATE_READY -> {
                        _playbackState.value = PlaybackState.Ready
                    }
                    Player.STATE_ENDED -> {
                        _playbackState.value = PlaybackState.Ended
                    }
                }
            }

            override fun onIsPlayingChanged(playing: Boolean) {
                _isPlaying.value = playing
            }

            override fun onPlayerError(error: PlaybackException) {
                Log.e(tag, "Player error: ${error.errorCodeName}", error)
                val errorMessage = when (error.errorCode) {
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ->
                        "Error de conexion de red"
                    PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT ->
                        "Tiempo de espera agotado al conectar con la transmision"
                    PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS ->
                        "Transmision no disponible temporalmente (Servidor 404/500)"
                    PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
                        "Canal fuera de linea"
                    PlaybackException.ERROR_CODE_PARSING_CONTAINER_MALFORMED,
                    PlaybackException.ERROR_CODE_PARSING_MANIFEST_MALFORMED ->
                        "Formato de transmision no compatible"
                    else ->
                        "Transmision no disponible temporalmente"
                }
                _playbackState.value = PlaybackState.Error(errorMessage)
            }
        }
    }

    override fun playChannel(channel: Channel) {
        Log.d(tag, "Playing channel: ${channel.name} -> ${channel.streamUrl}")
        _currentChannel.value = channel
        _playbackState.value = PlaybackState.Buffering

        val mediaItem = MediaItem.Builder()
            .setUri(channel.streamUrl)
            .apply {
                if (channel.streamUrl.contains(".m3u8", ignoreCase = true)) {
                    setMimeType(MimeTypes.APPLICATION_M3U8)
                }
            }
            .build()

        player.stop()
        player.clearMediaItems()
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    override fun retry() {
        _currentChannel.value?.let { playChannel(it) }
    }

    override fun pause() {
        player.pause()
    }

    override fun play() {
        player.play()
    }

    override fun release() {
        player.stop()
        player.release()
    }
}
