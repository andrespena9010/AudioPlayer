package com.exaple.audioplayer.ui.views

import android.os.Build.VERSION.SDK_INT
import androidx.annotation.OptIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.gif.AnimatedImageDecoder
import coil3.gif.GifDecoder
import coil3.request.ImageRequest
import com.exaple.audioplayer.R
import com.exaple.audioplayer.ui.theme.AudioPlayerTheme

@OptIn(UnstableApi::class)
@Composable
fun AudioPlayer(){

    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mediaId = R.raw.audio

    val player = ExoPlayer.Builder( context ).build()
    player.setMediaItem(MediaItem.fromUri( "android.resource://${context.packageName}/$mediaId" ) )
    player.prepare()
    player.playWhenReady = true

    lifecycle.addObserver(object : DefaultLifecycleObserver {
        override fun onStart( owner: LifecycleOwner ) {
            if ( !player.isPlaying ) player.play()
        }
        override fun onStop( owner: LifecycleOwner ) {
            player.pause()
        }
    })

    val controls = PlayerControlView( context )
    controls.player = player

    var isAudio by rememberSaveable { mutableStateOf( false ) }

    player.addListener(object : Player.Listener {
        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            var video = false
            for (i in 0 until tracks.groups.size) {
                val trackGroup = tracks.groups[i]
                for (j in 0 until trackGroup.length) {
                    val format = trackGroup.getTrackFormat(j)
                    if (format.sampleMimeType?.startsWith("video") == true) {
                        video = true
                        break
                    }
                }
                if (video) {
                    break
                }
            }
            isAudio = !video
        }
    })

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

            Content {

                if ( isAudio ){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White ),
                        contentAlignment = Alignment.Center
                    ){
                        Text("No video Available", fontSize = 20.sp, color = Color.Black)
                    }
                } else {
                    PlayerSurface(
                        player = player,
                        surfaceType = SURFACE_TYPE_TEXTURE_VIEW
                    )
                }

            }

        }

    }

}

@OptIn(UnstableApi::class)
@Composable
fun Content(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    Column (
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Row (
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){

                content()

            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background( Color.Gray ),
                contentAlignment = Alignment.Center
            ){

                Image(
                    painter = painterResource( R.drawable.icono ),
                    modifier = Modifier.fillMaxHeight(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )

            }

        }

        Row (
            modifier = modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){

                Image(
                    painter = painterResource( R.drawable.image ),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )

            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){



                AsyncImage(
                    model = ImageRequest.Builder( LocalContext.current)
                        .data(R.raw.gif)
                        .build(),
                    imageLoader = ImageLoader.Builder( LocalContext.current )
                        .components {
                            if (SDK_INT >= 28) {
                                add(AnimatedImageDecoder.Factory())
                            } else {
                                add(GifDecoder.Factory())
                            }
                        }
                        .build(),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )

            }

        }

    }
}

@Preview(
    device = "spec:width=840dp,height=474dp,dpi=640"
)
@Composable
fun PlayerPreview(){
    AudioPlayerTheme {
        Content{
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                Text("Video")
            }
        }
    }
}