package com.exaple.audioplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import com.exaple.audioplayer.ui.theme.AudioPlayerTheme
import com.exaple.audioplayer.ui.views.*
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioPlayerTheme {

                val subBunny = MediaItem.SubtitleConfiguration.Builder( File( filesDir, "big_buck_bunny.srt" ).toUri() )
                    .setMimeType(MimeTypes.APPLICATION_SUBRIP)
                    .setLanguage("en")
                    .build()

                VideoPlayer4K(
                    listOf(
                        MediaItem.Builder()
                            .setUri( File( filesDir, "big_buck_bunny.mp4" ).toUri() )
                            .setSubtitleConfigurations( listOf( subBunny ) )
                            .build(),
                        MediaItem.Builder()
                            .setUri( File( filesDir, "big_buck_bunny_less.mp4" ).toUri() )
                            .setSubtitleConfigurations( listOf( subBunny ) )
                            .build(),
                        MediaItem.fromUri( File( filesDir, "reel.mp4" ).toUri() )
                    )
                )
            }
        }
    }
}