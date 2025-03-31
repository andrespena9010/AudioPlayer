package com.exaple.audioplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.exaple.audioplayer.ui.theme.AudioPlayerTheme
import com.exaple.audioplayer.ui.views.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioPlayerTheme {
                AudioPlayer1( "audio.mp3" )
            }
        }
    }
}