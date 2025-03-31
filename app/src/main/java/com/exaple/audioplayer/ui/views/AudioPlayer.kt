package com.exaple.audioplayer.ui.views

import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView
import com.exaple.audioplayer.R

@OptIn(UnstableApi::class)
@Composable
fun AudioPlayer(){

    val context = LocalContext.current
    val mediaId = R.raw.audio

    val player = ExoPlayer.Builder( context ).build()
    player.setMediaItem(MediaItem.fromUri( "android.resource://${context.packageName}/$mediaId" ) )
    player.prepare()
    player.playWhenReady = true

    val controls = PlayerControlView( context )
    controls.player = player

    Scaffold { innerPaddings ->

        Box(
            modifier = Modifier
                .padding(innerPaddings)
                .fillMaxSize()
                .clickable(onClick = {
                    if (controls.isVisible) {
                        controls.hide()
                    } else {
                        controls.show()
                    }
                }),
            contentAlignment = Alignment.Center
        ){

            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f),
                factory = {
                    controls
                }
            )

            Image(
                painter = painterResource( R.drawable.image ),
                modifier = Modifier.fillMaxSize(),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )

        }

    }

}