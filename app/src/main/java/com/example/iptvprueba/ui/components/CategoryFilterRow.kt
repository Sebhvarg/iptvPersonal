package com.example.iptvprueba.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.example.iptvprueba.ui.theme.TvBackgroundDark
import com.example.iptvprueba.ui.theme.TvCardFocusedBorder
import com.example.iptvprueba.ui.theme.TvPrimary
import com.example.iptvprueba.ui.theme.TvSurfaceVariant
import com.example.iptvprueba.ui.theme.TvTextPrimary
import com.example.iptvprueba.ui.theme.TvTextSecondary

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CategoryFilterRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.equals(selectedCategory, ignoreCase = true)
            val interactionSource = remember { MutableInteractionSource() }
            val isFocused by interactionSource.collectIsFocusedAsState()

            val backgroundColor by animateColorAsState(
                targetValue = when {
                    isFocused -> TvPrimary
                    isSelected -> TvSurfaceVariant
                    else -> Color.Black.copy(alpha = 0.5f)
                },
                label = "cat_bg"
            )

            val textColor by animateColorAsState(
                targetValue = when {
                    isFocused -> TvBackgroundDark
                    isSelected -> TvPrimary
                    else -> TvTextSecondary
                },
                label = "cat_text"
            )

            Surface(
                onClick = { onCategorySelected(category) },
                interactionSource = interactionSource,
                shape = ClickableSurfaceDefaults.shape(shape = RoundedCornerShape(20.dp)),
                colors = ClickableSurfaceDefaults.colors(
                    containerColor = backgroundColor,
                    focusedContainerColor = TvPrimary
                ),
                border = ClickableSurfaceDefaults.border(
                    border = Border(
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) TvPrimary.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    focusedBorder = Border(border = BorderStroke(2.dp, TvCardFocusedBorder))
                ),
                modifier = Modifier.padding(2.dp)
            ) {
                Text(
                    text = category,
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}
