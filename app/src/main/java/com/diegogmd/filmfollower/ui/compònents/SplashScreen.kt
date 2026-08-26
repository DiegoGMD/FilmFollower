package com.diegogmd.filmfollower.ui.compònents

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.diegogmd.filmfollower.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    LaunchedEffect(Unit) {
        delay(500)
        navController.navigate("Main") {
            popUpTo("SplashScreen") { inclusive = true }
        }
    }

    val painter = painterResource(id = R.drawable.large_film_vertical)
    val intrinsicSize = painter.intrinsicSize
    val aspectRatio = if (intrinsicSize.width > 0 && intrinsicSize.height > 0) {
        intrinsicSize.width / intrinsicSize.height
    } else {
        1f
    }

    var containerHeightPx by remember { mutableIntStateOf(0) }
    var imageHeightPx by remember { mutableIntStateOf(0) }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(containerHeightPx, imageHeightPx) {
        val maxScroll = (imageHeightPx - containerHeightPx).coerceAtLeast(0).toFloat()
        if (maxScroll > 0) {
            offsetY.animateTo(
                targetValue = maxScroll,
                animationSpec = tween(durationMillis = 1800, easing = LinearEasing)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clipToBounds()
            .onSizeChanged { containerHeightPx = it.height },
        contentAlignment = Alignment.TopStart
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio, matchHeightConstraintsFirst = false)
                .onSizeChanged { imageHeightPx = it.height }
                .graphicsLayer { translationY = -offsetY.value }
        )
    }
}