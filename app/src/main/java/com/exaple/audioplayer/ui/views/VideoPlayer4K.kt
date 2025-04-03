package com.exaple.audioplayer.ui.views

import android.view.TextureView
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerView
import com.exaple.audioplayer.ui.custom.Controls

/**
 * Reproductor de video 4K que utiliza ExoPlayer en Jetpack Compose.
 *
 * @param mediaItems Lista de elementos multimedia a reproducir.
 *
 * Características principales:
 * - Soporte para video 4K (3840x2160)
 * - Buffer optimizado para streaming
 *
 * Dependencias usadas:
 * - androidx.media3: Biblioteca ExoPlayer para reproducción multimedia avanzada
 */

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer4K(mediaItems: List<MediaItem>) {

    val context = LocalContext.current

    // Selector de pistas configurado para video 4K
    val trackSelector = DefaultTrackSelector(context).apply {
        parameters = buildUponParameters()
            .setMaxVideoSize(3840, 2160)
            .setMaxVideoFrameRate(60)
            .setForceHighestSupportedBitrate(true)
            .build()
    }

    // Configuración del reproductor ExoPlayer
    val player = ExoPlayer
        .Builder(context)
        .setTrackSelector(trackSelector)
        .setLoadControl(
            DefaultLoadControl
                .Builder()
                .setBufferDurationsMs(
                    60 * 1000,
                    120 * 1000,
                    5000,
                    5000
                )
                .build()
        )
        .build()

    player.setVideoTextureView(TextureView(context))
    player.setMediaItems(mediaItems)
    player.prepare()
    player.playWhenReady = true

    Scaffold { innerPaddings ->
        Box(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize()
                .background( Color.Black ),
            contentAlignment = Alignment.Center
        ) {

            Controls(
                modifier = Modifier
                    .zIndex(1f),
                player = player
            )

            AndroidView(
                factory = {
                    PlayerView( context )
                        .apply {
                            this.player = player
                            useController = false
                        }
                }
            )
        }
    }
}