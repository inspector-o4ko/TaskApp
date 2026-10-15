package com.example.taskapp.ui.presentation.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.max

@Stable
class ThemeRevealController(
    private val scope: CoroutineScope,
    val layer: GraphicsLayer
) {
    var snapshot by mutableStateOf<ImageBitmap?>(null)
        private set
    var center by mutableStateOf(Offset.Zero)
        private set
    val progress = Animatable(0f)

    val isRunning: Boolean get() = snapshot != null

    fun start(origin: Offset, applyTheme: () -> Unit) {
        if (isRunning) return
        scope.launch {
            center = origin
            snapshot = layer.toImageBitmap()
            progress.snapTo(0f)
            applyTheme()
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(650, easing = FastOutSlowInEasing)
            )
            snapshot = null
        }
    }
}

val LocalThemeReveal = compositionLocalOf<ThemeRevealController?> { null }

@Composable
fun ThemeRevealHost(content: @Composable () -> Unit) {
    val layer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    val controller = remember { ThemeRevealController(scope, layer) }

    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .drawWithContent {
                    layer.record { this@drawWithContent.drawContent() }
                    drawLayer(layer)
                }
        ) {
            androidx.compose.runtime.CompositionLocalProvider(
                LocalThemeReveal provides controller
            ) {
                content()
            }
        }

        controller.snapshot?.let { bitmap ->
            Canvas(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            ) {
                drawImage(bitmap)
                drawCircle(
                    color = Color.Black,
                    radius = maxRadius(controller.center) * controller.progress.value,
                    center = controller.center,
                    blendMode = BlendMode.Clear
                )
            }
        }
    }
}

private fun DrawScope.maxRadius(center: Offset): Float =
    hypot(
        max(center.x, size.width - center.x),
        max(center.y, size.height - center.y)
    )