package com.telemed.demo.ui.responsive

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Responsive padding and max-width helper for all screens.
 * On phones (< 600dp): uses standard 16dp horizontal padding
 * On tablets (600-840dp): uses 32dp horizontal padding with 600dp max content width
 * On large tablets (> 840dp): uses 48dp horizontal padding with 720dp max content width
 */
@Composable
fun responsiveHorizontalPadding(): Dp {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    return when {
        screenWidth > 840.dp -> 48.dp
        screenWidth > 600.dp -> 32.dp
        else -> 16.dp
    }
}

@Composable
fun responsiveMaxContentWidth(): Dp {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    return when {
        screenWidth > 840.dp -> 720.dp
        screenWidth > 600.dp -> 600.dp
        else -> Dp.Unspecified
    }
}

/**
 * Modifier that applies responsive centering and max-width constraints.
 * Useful for making content look good on both phones and tablets.
 */
@Composable
fun Modifier.responsiveContent(): Modifier {
    val maxWidth = responsiveMaxContentWidth()
    return if (maxWidth != Dp.Unspecified) {
        this.widthIn(max = maxWidth)
    } else {
        this.fillMaxWidth()
    }
}

@Composable
fun ResponsiveScreen(
    title: String,
    onBack: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Scaffold(
        topBar = {
            AppTopBar(title = title, onBack = onBack)
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val horizontalPadding = when {
                maxWidth > 840.dp -> 48.dp
                maxWidth > 600.dp -> 32.dp
                else -> 16.dp
            }
            val contentMaxWidth = when {
                maxWidth > 840.dp -> 720.dp
                maxWidth > 600.dp -> 600.dp
                else -> maxWidth
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontalPadding, 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(modifier = Modifier.widthIn(max = contentMaxWidth)) {
                    content()
                }
            }
        }
    }
}
