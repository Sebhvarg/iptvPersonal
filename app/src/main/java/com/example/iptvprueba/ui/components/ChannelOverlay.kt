package com.example.iptvprueba.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.example.iptvprueba.domain.model.Channel
import com.example.iptvprueba.ui.theme.TvAccent
import com.example.iptvprueba.ui.theme.TvBackgroundDark
import com.example.iptvprueba.ui.theme.TvCardFocusedBorder
import com.example.iptvprueba.ui.theme.TvOverlayBackground
import com.example.iptvprueba.ui.theme.TvPrimary
import com.example.iptvprueba.ui.theme.TvSurfaceVariant
import com.example.iptvprueba.ui.theme.TvTextPrimary
import com.example.iptvprueba.ui.theme.TvTextSecondary

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ChannelOverlay(
    channels: List<Channel>,
    currentChannel: Channel?,
    categories: List<String>,
    selectedCategory: String,
    isVisible: Boolean,
    isRefreshing: Boolean,
    refreshMessage: String?,
    onChannelSelected: (Channel) -> Unit,
    onCategorySelected: (String) -> Unit,
    onRefreshChannels: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            TvOverlayBackground.copy(alpha = 0.85f),
                            TvOverlayBackground
                        )
                    )
                )
                .padding(top = 12.dp, bottom = 24.dp, start = 24.dp, end = 24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Status banner if refresh in progress or completed
                if (refreshMessage != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(TvAccent.copy(alpha = 0.25f))
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = refreshMessage,
                            color = TvAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Header: Title, channel count and Reload button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Guia de Canales",
                            color = TvTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                        Text(
                            text = "(${channels.size} disponibles)",
                            color = TvTextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    // Reload Channels Button
                    val refreshInteractionSource = remember { MutableInteractionSource() }
                    val isRefreshFocused by refreshInteractionSource.collectIsFocusedAsState()

                    Surface(
                        onClick = onRefreshChannels,
                        interactionSource = refreshInteractionSource,
                        shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(8.dp)),
                        colors = ClickableSurfaceDefaults.colors(
                            containerColor = TvSurfaceVariant,
                            focusedContainerColor = TvPrimary
                        ),
                        border = ClickableSurfaceDefaults.border(
                            border = Border(border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))),
                            focusedBorder = Border(border = BorderStroke(2.dp, TvCardFocusedBorder))
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            if (isRefreshing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = if (isRefreshFocused) TvBackgroundDark else TvPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    tint = if (isRefreshFocused) TvBackgroundDark else TvTextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isRefreshing) "Actualizando..." else "Recargar canales",
                                color = if (isRefreshFocused) TvBackgroundDark else TvTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Categories Row
                if (categories.size > 1) {
                    CategoryFilterRow(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = onCategorySelected,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Channel Cards Row
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    itemsIndexed(
                        items = channels,
                        key = { _, item -> item.id + item.streamUrl }
                    ) { index, channel ->
                        val isPlaying = currentChannel?.id == channel.id
                        ChannelCard(
                            channel = channel,
                            channelNumber = index + 1,
                            isPlaying = isPlaying,
                            onSelect = { onChannelSelected(channel) }
                        )
                    }
                }
            }
        }
    }
}
