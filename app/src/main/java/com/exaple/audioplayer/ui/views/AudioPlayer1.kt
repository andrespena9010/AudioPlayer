package com.exaple.audioplayer.ui.views

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView
import coil3.compose.AsyncImage
import com.exaple.audioplayer.R
import java.io.File

/**
 *
 * Composable que reproduce un archivo de audio con controles y muestra una imagen o GIF en Jetpack Compose.
 *
 * @param fileName Nombre del archivo de audio almacenado en el directorio interno del dispositivo.
 *
 * Librerías externas utilizadas:
 * - **ExoPlayer (androidx.media3)**: Proporciona herramientas avanzadas para gestionar medios en Android.
 * - **Coil (coil3.compose)**: Facilita la carga de imágenes y soporta animaciones como GIFs.
 *
 */

@OptIn(UnstableApi::class) // Indica que estamos utilizando una API inestable de Media3.
@Composable
fun AudioPlayer1(fileName: String) {
    val context = LocalContext.current
    val audio = File(context.filesDir, fileName) // Archivo de audio almacenado en el directorio interno del dispositivo.
    val gif = File(context.filesDir, "gif") // Archivo GIF almacenado en el directorio interno.

    // Inicialización del reproductor ExoPlayer.
    val player = ExoPlayer.Builder(context).build()
    if (audio.exists()) {
        player.setMediaItem(MediaItem.fromUri(audio.toUri())) // Si el archivo de audio existe, se carga en el reproductor.
    }
    player.prepare()
    player.playWhenReady = true // Comienza la reproducción automáticamente.

    // Inicialización de los controles del reproductor.
    val controls = PlayerControlView(context)
    controls.player = player // Asigna el reproductor a los controles.

    Scaffold { innerPaddings ->
        Box(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize()
                .clickable(onClick = {
                    // Alternar la visibilidad de los controles de reproducción al hacer clic en la pantalla.
                    if (controls.isVisible) {
                        controls.hide()
                    } else {
                        controls.show()
                    }
                }),
            contentAlignment = Alignment.Center // Centra el contenido en la pantalla.
        ) {
            // Renderiza los controles del reproductor dentro de Jetpack Compose.
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f), // Asegura que los controles se superpongan correctamente.
                factory = { controls }
            )

            // Si el archivo GIF existe, lo muestra como animación. Si no, se muestra una imagen estática.
            if (gif.exists()) {
                AsyncImage(
                    model = gif.toUri(), // Carga la imagen animada desde almacenamiento interno.
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Gif animado",
                    contentScale = ContentScale.Crop // Recorta la imagen para que llene el espacio disponible.
                )
            } else {
                Image(
                    painter = painterResource(R.drawable.image), // Carga una imagen estática de los recursos.
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "Imagen estática",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}