package com.exaple.audioplayer.ui.custom

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.exaple.audioplayer.R
import com.exaple.audioplayer.ui.theme.AudioPlayerTheme
import com.exaple.audioplayer.utils.ExoPlayerExample
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Controls(
    modifier: Modifier = Modifier,
    player: ExoPlayer
) {

    val scope = rememberCoroutineScope()
    var job: Job? by rememberSaveable { mutableStateOf(null) }
    val configuration = LocalConfiguration.current
    var currentPosition by rememberSaveable { mutableLongStateOf( 0L ) }
    var currentMediaItemIndex by rememberSaveable { mutableIntStateOf( 0 ) }
    var duration by rememberSaveable { mutableLongStateOf( 1L ) }
    var orientation by rememberSaveable { mutableIntStateOf( -1 ) }
    var show by rememberSaveable { mutableStateOf(true) }
    var loading by rememberSaveable { mutableStateOf(true) }

    fun hide(){
        if ( job?.isActive == true ){
            job!!.cancel()
        }
        job = scope.launch {
            delay(5000)
            if ( player.playbackState != Player.STATE_BUFFERING ){
                show = false
            }
        }
    }

    player.addListener(
        object: Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when ( playbackState ){

                    Player.STATE_READY -> {
                        loading = false
                        currentMediaItemIndex = player.currentMediaItemIndex
                        orientation = configuration.orientation
                        duration = player.duration
                        hide()
                    }

                    Player.STATE_BUFFERING -> {
                        loading = true
                    }

                    Player.STATE_ENDED -> {

                    }

                    Player.STATE_IDLE -> {

                    }
                }
            }

        }
    )

    if ( orientation != -1 && orientation != configuration.orientation ) {
        player.seekTo( currentMediaItemIndex, currentPosition)
    }

    LaunchedEffect ( Unit ){
        hide()
        while ( true ){
            currentPosition = player.currentPosition
            //Log.i("INFOTEST","posicion actualizada: $currentPosition" )
            delay(100)
        }
    }

    Box (
        modifier = modifier
            .fillMaxSize()
            .clickable(
                onClick = {
                    show = !show
                }
            )
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true){
                        awaitPointerEvent(pass = PointerEventPass.Final)
                        hide()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ){

        if ( loading ){
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
            )
        }

        if ( show ){

            Column (
                modifier = Modifier
                    .fillMaxSize()
            ){

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){



                }

                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ){

                    Spacer(Modifier.size(10.dp))

                    TimeInfo(
                        modifier = Modifier
                            .background( Color(0x22FFFFFF) )
                            .padding(
                                start = 5.dp,
                                end = 5.dp
                            ),
                        time = currentPosition
                    )

                    SliderPlayer(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(start = 10.dp, end = 10.dp),
                        position = currentPosition,
                        duration = duration,
                        onSeek = { newPos ->
                            if ( player.playbackState == Player.STATE_READY ){
                                Log.i("INFOTEST","SeekTo: $newPos" )
                                player.seekTo( newPos )
                                currentPosition = player.currentPosition
                            }
                        }
                    )

                    TimeInfo(
                        modifier = Modifier
                            .background( Color(0x22FFFFFF) )
                            .padding(
                                start = 5.dp,
                                end = 5.dp
                            ),
                        time = duration
                    )

                    Spacer(Modifier.size(10.dp))
                }

                Row(
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Spacer(Modifier.size(10.dp))

                    Icon(
                        painter = painterResource(R.drawable.back),
                        contentDescription = "Previous",
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    if ( player.hasPreviousMediaItem() ) {
                                        currentPosition = 0L
                                        player.seekToPrevious()
                                        player.play()
                                    }
                                }
                            )
                            .clip(RoundedCornerShape(30.dp))
                            .background( Color(0x22FFFFFF) )
                            .padding(10.dp),
                        tint = Color.White
                    )

                    Spacer(Modifier.size(10.dp))

                    if ( player.isPlaying ){
                        Icon(
                            painter = painterResource(R.drawable.pausa),
                            contentDescription = "Pause",
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        player.pause()
                                    }
                                )
                                .clip(RoundedCornerShape(30.dp))
                                .background( Color(0x22FFFFFF) )
                                .padding(10.dp),
                            tint = Color.White
                        )
                    } else {
                        Icon(
                            painter = painterResource(R.drawable.play),
                            contentDescription = "Play",
                            modifier = Modifier
                                .clickable(
                                    onClick = {
                                        player.play()
                                    }
                                )
                                .clip(RoundedCornerShape(30.dp))
                                .background( Color(0x22FFFFFF) )
                                .padding(10.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.size(10.dp))

                    Icon(
                        painter = painterResource(R.drawable.next),
                        contentDescription = "Next",
                        modifier = Modifier
                            .clickable(
                                onClick = {
                                    if ( player.hasNextMediaItem() ) {
                                        player.seekToNext()
                                        player.play()
                                    }
                                }
                            )
                            .clip(RoundedCornerShape(30.dp))
                            .background( Color(0x22FFFFFF) )
                            .padding(10.dp),
                        tint = Color.White
                    )

                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                )

            }

        }

    }

}

@androidx.annotation.OptIn(UnstableApi::class)
@Preview(
    device = "spec:width=960dp,height=540dp,dpi=640"
)
@Composable
fun Prev(){
    AudioPlayerTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)){
            Controls(
                player = ExoPlayerExample()
            )
        }
    }
}