package com.exaple.audioplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import com.exaple.audioplayer.ui.theme.AudioPlayerTheme
import com.exaple.audioplayer.ui.views.*
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioPlayerTheme {
                VideoPlayer4K(
                    listOf(
                        MediaItem.fromUri( File( filesDir, "v_4k_3840_2160.mp4").toUri() ),
                        MediaItem.fromUri( File( filesDir, "v_4096_2160.mp4").toUri() ),
                        MediaItem.fromUri( File( filesDir, "v_3840_2160.mp4").toUri() ),
                        MediaItem.fromUri( File( filesDir, "universo.mp4").toUri() )
                    )
                )
            }
        }
    }
}