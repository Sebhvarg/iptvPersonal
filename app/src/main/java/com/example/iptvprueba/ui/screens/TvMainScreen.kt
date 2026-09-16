package com.example.iptvprueba.ui.screens

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import com.example.iptvprueba.domain.model.Channel
import com.example.iptvprueba.ui.components.ChannelInfoBanner
import com.example.iptvprueba.ui.components.ChannelOverlay
import com.example.iptvprueba.ui.components.PlaybackStatusView
import com.example.iptvprueba.ui.components.VideoPlayerView
import com.example.iptvprueba.ui.viewmodel.IptvViewModel
import kotlinx.coroutines.delay

@Composable
fun TvMainScreen(
    viewModel: IptvViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    var showInfoBanner by remember { mutableStateOf(true) }

    // Auto-hide info banner after 6 seconds of channel switch
    LaunchedEffect(uiState.currentChannel) {
        showInfoBanner = true
        delay(6000)
        showInfoBanner = false
    }

    BackHandler(enabled = uiState.isOverlayVisible) {
        viewModel.setOverlayVisible(false)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (!uiState.isOverlayVisible) {
                                viewModel.previousChannel()
                                showInfoBanner = true
                                true
                            } else {
                                false
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (!uiState.isOverlayVisible) {
                                viewModel.nextChannel()
                                showInfoBanner = true
                                true
                            } else {
                                false
                            }
                        }
                        KeyEvent.KEYCODE_MENU, KeyEvent.KEYCODE_INFO -> {
                            viewModel.toggleOverlay()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            if (!uiState.isOverlayVisible) {
                                viewModel.setOverlayVisible(true)
                                showInfoBanner = true
                                true
                            } else {
                                false
                            }
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
    ) {
        // Video Player Background
        VideoPlayerView(
            player = viewModel.playerController.player,
            modifier = Modifier.fillMaxSize()
        )

        // Playback Status (Buffering / Error Overlay)
        PlaybackStatusView(
            playbackState = uiState.playbackState,
            onRetry = { viewModel.retryPlayback() }
        )

        // Top Channel Info Banner HUD
        val currentChannelIndex = if (uiState.currentChannel != null) {
            uiState.channels.indexOf(uiState.currentChannel) + 1
        } else {
            1
        }

        ChannelInfoBanner(
            channel = uiState.currentChannel,
            channelNumber = currentChannelIndex,
            isVisible = showInfoBanner || uiState.isOverlayVisible,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Bottom Channel Guide Overlay
        ChannelOverlay(
            channels = uiState.filteredChannels,
            currentChannel = uiState.currentChannel,
            categories = uiState.categories,
            selectedCategory = uiState.selectedCategory,
            isVisible = uiState.isOverlayVisible,
            isRefreshing = uiState.isRefreshing,
            refreshMessage = uiState.refreshMessage,
            onChannelSelected = { channel: Channel ->
                viewModel.selectChannel(channel)
                showInfoBanner = true
            },
            onCategorySelected = { category: String ->
                viewModel.selectCategory(category)
            },
            onRefreshChannels = {
                viewModel.refreshChannelsList()
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
