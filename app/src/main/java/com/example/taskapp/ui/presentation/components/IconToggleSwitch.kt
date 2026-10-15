package com.example.taskapp.ui.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

private val TrackWidth = 76.dp
private val TrackHeight = 40.dp
private val ThumbSize = 32.dp
private val ThumbPadding = 4.dp


// checked = false -> ползунок слева (startIcon)
// checked = true  -> ползунок справа (endIcon)

@Composable
fun IconToggleSwitch(
    checked: Boolean,
    onCheckedChange: (checked: Boolean, origin: Offset) -> Unit,
    startIcon: Painter,
    endIcon: Painter,
    modifier: Modifier = Modifier
) {
    val isDarkSurface = MaterialTheme.colorScheme.background.luminanceIsDark()

    var thumbCenter by remember { mutableStateOf(Offset.Zero) }

    val thumbOffset by animateDpAsState(
        targetValue = if (checked) TrackWidth - ThumbSize - ThumbPadding else ThumbPadding,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "thumbOffset"
    )
    val trackColor by animateColorAsState(
        targetValue = if (isDarkSurface) Color(0xFF2A2F3F) else Color(0xFFD9DCE6),
        label = "trackColor"
    )
    val thumbColor by animateColorAsState(
        targetValue = if (isDarkSurface) Color(0xFF8A90A6) else Color(0xFFF6F7FB),
        label = "thumbColor"
    )
    val trackBorder = Brush.verticalGradient(
        colors = if (isDarkSurface) {
            listOf(Color(0xFF181B26), Color(0xFF3A4054))
        } else {
            listOf(Color(0xFFB4B8C8), Color(0xFFFFFFFF))
        }
    )

    val activeTint = MaterialTheme.colorScheme.onSurface
    val inactiveTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
    ) {
        Icon(
            painter = startIcon,
            contentDescription = null,
            tint = if (!checked) activeTint else inactiveTint,
            modifier = Modifier.size(32.dp)
        )

        Box(
            modifier = Modifier
                .size(TrackWidth, TrackHeight)
                .clip(CircleShape)
                .background(trackColor)
                .border(width = 3.dp, brush = trackBorder, shape = CircleShape)
                .toggleable(
                    value = checked,
                    role = Role.Switch,
                    onValueChange = { onCheckedChange(it, thumbCenter) }
                )
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = thumbOffset)
                    .size(ThumbSize)
                    .onGloballyPositioned { coords ->
                        thumbCenter = coords.positionInRoot() +
                                Offset(coords.size.width / 2f, coords.size.height / 2f)
                    }
                    .shadow(elevation = 6.dp, shape = CircleShape)
                    .background(thumbColor, CircleShape)
            )
        }

        Icon(
            painter = endIcon,
            contentDescription = null,
            tint = if (checked) activeTint else inactiveTint,
            modifier = Modifier.size(32.dp)
        )
    }
}

private fun Color.luminanceIsDark(): Boolean =
    (0.299f * red + 0.587f * green + 0.114f * blue) < 0.5f