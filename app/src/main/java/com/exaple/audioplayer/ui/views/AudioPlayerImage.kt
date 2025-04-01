package com.exaple.audioplayer.ui.views

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.exaple.audioplayer.R
import java.io.File

/**
 *
 * Composable que reproduce un archivo de audio usando ExoPlayer en Jetpack Compose.
 *
 * @param fileName Nombre del archivo de audio almacenado en el directorio interno del dispositivo.
 *
 * Librerías externas utilizadas:
 * - **ExoPlayer (androidx.media3)**: Proporciona herramientas avanzadas para gestionar medios en Android.
 *
 */

@OptIn(UnstableApi::class) // Indica que estamos utilizando una API inestable de ExoPlayer.
@Composable
fun AudioPlayerImage(fileName: String) {

    // Contexto de la aplicación, necesario para acceder a recursos y archivos locales.
    val context = LocalContext.current

    // Crea un objeto File que representa el archivo de audio dentro del almacenamiento interno de la app.
    val audio = File(context.filesDir, fileName)

    // Inicialización de ExoPlayer
    val player = ExoPlayer.Builder(context).build()

    // Si el archivo de audio existe, se carga en el reproductor.
    if (audio.exists()) {
        player.setMediaItem(MediaItem.fromUri(audio.toUri()))
    }

    // Prepara el reproductor y establece la reproducción automática cuando esté listo.
    player.prepare()
    player.playWhenReady = true

    // Creación de la vista PlayerView que mostrará el reproductor.
    val view = PlayerView(context).apply {
        this.player = player // Asigna el reproductor a la vista.
        this.defaultArtwork = ContextCompat.getDrawable(context, R.drawable.image) // Imagen por defecto si no hay video.
        this.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM // Ajuste de tamaño del contenido.
    }

    // Estructura de la interfaz de usuario usando Scaffold.
    Scaffold { innerPaddings ->

        Box(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize(),
            contentAlignment = Alignment.Center // Centra el contenido en la pantalla.
        ) {

            // Renderiza la vista PlayerView dentro de Jetpack Compose.
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f), // Asegura que el reproductor esté al frente.
                factory = { view }
            )
        }
    }
}