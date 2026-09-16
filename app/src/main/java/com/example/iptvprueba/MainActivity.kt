package com.example.iptvprueba

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.iptvprueba.data.datasource.RemotePlaylistDataSource
import com.example.iptvprueba.data.parser.DefaultM3uParser
import com.example.iptvprueba.data.repository.DefaultChannelRepository
import com.example.iptvprueba.domain.usecase.GetChannelsUseCase
import com.example.iptvprueba.domain.usecase.RefreshChannelsUseCase
import com.example.iptvprueba.player.DefaultTvPlayerController
import com.example.iptvprueba.ui.screens.TvMainScreen
import com.example.iptvprueba.ui.theme.IptvPruebaTheme
import com.example.iptvprueba.ui.viewmodel.IptvViewModel
import com.example.iptvprueba.ui.viewmodel.IptvViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: IptvViewModel by viewModels {
        val dataSource = RemotePlaylistDataSource()
        val parser = DefaultM3uParser()
        val repository = DefaultChannelRepository(dataSource, parser)
        val getChannelsUseCase = GetChannelsUseCase(repository)
        val refreshChannelsUseCase = RefreshChannelsUseCase(repository)
        val playerController = DefaultTvPlayerController(applicationContext)

        IptvViewModelFactory(
            getChannelsUseCase = getChannelsUseCase,
            refreshChannelsUseCase = refreshChannelsUseCase,
            playerController = playerController
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            IptvPruebaTheme {
                TvMainScreen(viewModel = viewModel)
            }
        }
    }
}