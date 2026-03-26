package com.breckneck.debtbook.core.ui.components

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme

/**
 * Empty-state view: centered icon with supporting text for when a list has no items.
 */
@Composable
fun EmptyListPlaceholder(
    emptyText: String,
    emptyIcon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = emptyIcon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(72.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = emptyText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Loading skeleton: column of shimmer rows using theme-aware tonal surface colors.
 */
@Composable
fun ShimmerListPlaceholder(
    rowCount: Int = 6,
    rowHeight: Dp = 72.dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(rowCount) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    .clip(MaterialTheme.shapes.medium)
                    .shimmerEffect()
            )
        }
    }
}

@Preview(name = "EmptyList — light")
@Preview(name = "EmptyList — dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun EmptyListPlaceholderPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface {
            EmptyListPlaceholder(
                emptyText = "There are no debts yet.\nClick the button below to add.",
                emptyIcon = Icons.Default.AccountBalance
            )
        }
    }
}

@Preview(name = "ShimmerList — light")
@Preview(name = "ShimmerList — dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ShimmerListPlaceholderPreview() {
    DebtBookTheme(dynamicColor = false) {
        Surface(modifier = Modifier.fillMaxWidth()) {
            ShimmerListPlaceholder(rowCount = 5, rowHeight = 72.dp)
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    val shimmerBase = MaterialTheme.colorScheme.surfaceContainerHighest
    val shimmerHighlight = MaterialTheme.colorScheme.surfaceContainerLow
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(shimmerBase, shimmerHighlight, shimmerBase),
            start = Offset(translateAnimation - 500f, 0f),
            end = Offset(translateAnimation, 0f)
        )
    )
}
