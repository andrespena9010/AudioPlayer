package com.exaple.audioplayer.ui.views

import android.graphics.Color
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.*
import androidx.media3.common.util.RepeatModeUtil.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.PlayerView

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
                    60 * 1000, // Buffer mínimo: 60 segundos
                    120 * 1000, // Buffer máximo: 120 segundos
                    5000, // Buffer para inicio de reproducción
                    5000 // Buffer para rebuffering
                )
                .build()
        )
        .build()

    player.setMediaItems(mediaItems)
    player.prepare()
    player.playWhenReady = true

    // Configuración de los controles del reproductor
    val controls = PlayerControlView(context).apply {
        this.player = player
        this.repeatToggleModes = REPEAT_TOGGLE_MODE_ALL // Todos los modos de repetición
        this.showShuffleButton = true // Botón de reproducción aleatoria visible
        this.showSubtitleButton = true // Botón de subtítulos visible
        this.showVrButton = true // Botón de realidad virtual visible
        this.showTimeoutMs = 5000 // Tiempo de ocultamiento automático: 5 segundos
        this.setShowPlayButtonIfPlaybackIsSuppressed(false)
        this.setTimeBarMinUpdateInterval(500) // Actualización de barra de progreso cada 500ms
    }

    Scaffold { innerPaddings ->
        Box(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() }, // Sin efecto visual
                    indication = null, // Sin animación de clic
                    onClick = { // Alternar visibilidad de controles al tocar la pantalla
                        if (controls.isVisible) {
                            controls.hide()
                        } else {
                            controls.show()
                        }
                    }),
            contentAlignment = Alignment.Center
        ) {

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
                factory = {
                    controls
                }
            )

            // Vista principal del reproductor de video
            AndroidView(
                modifier = Modifier
                    .fillMaxSize(),
                factory = {
                    PlayerView(context)
                        .apply {
                            this.player = player
                            useController = false
                            setBackgroundColor(Color.BLACK)
                        }
                }
            )
        }
    }
}