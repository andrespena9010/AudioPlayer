package com.exaple.audioplayer.ui.custom

import android.util.Log
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.exaple.audioplayer.R
import com.exaple.audioplayer.ui.theme.AudioPlayerTheme
import com.exaple.audioplayer.utils.ExoPlayerExample
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Controls(
    modifier: Modifier = Modifier,
    player: ExoPlayer
) {

    var items by remember { mutableStateOf( MediaItems() ) }
    var showItems by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var job: Job? by rememberSaveable { mutableStateOf(null) }
    val configuration = LocalConfiguration.current
    var currentPosition by rememberSaveable { mutableLongStateOf( 0L ) }
    var currentMediaItemIndex by rememberSaveable { mutableIntStateOf( 0 ) }
    var duration by rememberSaveable { mutableLongStateOf( 1L ) }
    var orientation by rememberSaveable { mutableIntStateOf( -1 ) }
    var show by rememberSaveable { mutableStateOf(true) }
    var loading by rememberSaveable { mutableStateOf(true) }
    var currentVolume by rememberSaveable { mutableFloatStateOf( 0.7f ) }
    val backGround = Color(0x33888888)
    var fordward by remember { mutableStateOf(false) }
    var backward by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    suspend fun jumpTo(time: Long, left: Boolean = false ){
        if ( player.playbackState == Player.STATE_READY ){
            var advance = 0L
            if ( left ) {
                backward = true
                advance = currentPosition - (time)
            } else {
                fordward = true
                advance = currentPosition + (time)
            }
            player.seekTo( advance )
            currentPosition = player.currentPosition
            delay(100)
            if ( left ) backward = false else fordward = false
        }
    }

    fun hide(){
        if ( job?.isActive == true ){
            job!!.cancel()
        }
        job = scope.launch {
            delay(5000)
            if ( player.playbackState != Player.STATE_BUFFERING ){
                show = false
                showItems = false
            }
        }
    }

    fun setInfoItems(){
        var audioLs = mutableListOf<Language>()
        player.currentTracks.groups.forEach { group ->
            if (group.type == C.TRACK_TYPE_AUDIO) {
                if ( group.mediaTrackGroup.length > 0 ){
                    val format = group.mediaTrackGroup.getFormat(0)
                    audioLs.add(
                        Language(
                            mediaTrackGroupId = group.mediaTrackGroup.id,
                            languageIso6392Name = format.language.toString()
                        )
                    )
                }
            }
        }
        items = MediaItems( audioList = audioLs )
    }

    fun setAudio( mediaTrackGroupId: String ){
        if (player.playbackState != Player.STATE_READY) {
            Log.e("AUDIO_TRACK", "El reproductor no está listo. Estado actual: ${player.playbackState}")
            return
        }

        var mediaTrackGroup: TrackGroup? = null

        player.currentTracks.groups.forEach { group ->
            if ( group.type == C.TRACK_TYPE_AUDIO ){
                if ( group.mediaTrackGroup.id == mediaTrackGroupId ){
                    mediaTrackGroup = group.mediaTrackGroup
                }
            }
        }

        mediaTrackGroup?.let {

            val newParameters = player.trackSelectionParameters.buildUpon()
                .setOverrideForType(
                    TrackSelectionOverride( mediaTrackGroup, listOf(0))
                )
                .build()

            player.trackSelectionParameters = newParameters

        }

    }

    player.addListener(
        object: Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                when ( playbackState ){

                    Player.STATE_READY -> {
                        setInfoItems()
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

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                    duration = player.duration
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
            delay(100)
        }
    }

    Box (
        modifier = modifier
            .fillMaxSize()
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

        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ){

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .pointerInput(Unit){
                        detectTapGestures(
                            onTap = {
                                show = !show
                                showItems = false
                            },
                            onDoubleTap = {
                                scope.launch {
                                    jumpTo(
                                        time = (15*1000),
                                        left = true
                                    )
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ){

                if ( backward ){
                    Icon(
                        painter = painterResource(R.drawable.backward),
                        contentDescription = "backward",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background( backGround )
                            .padding(10.dp),
                        tint = Color.White
                    )
                }

            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .pointerInput(Unit){
                        detectTapGestures(
                            onTap = {
                                show = !show
                                showItems = false
                            },
                            onDoubleTap = {
                                scope.launch {
                                    jumpTo( time = (15*1000) )
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ){

                if ( fordward ){
                    Icon(
                        painter = painterResource(R.drawable.fordward),
                        contentDescription = "fordward",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background( backGround )
                            .padding(10.dp),
                        tint = Color.White
                    )
                }

            }

        }

        if ( show ){

            Column (
                modifier = Modifier
                    .fillMaxSize()
            ){

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.BottomEnd
                ){


                    if ( showItems && items.audioList.isNotEmpty() ){

                        Box(
                            modifier = Modifier
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ){

                            Box(
                                Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Gray.copy(alpha = 0.3f))
                                    .heightIn( max = 100.dp )
                                    .width(80.dp)
                            ){

                                LazyColumn (
                                    modifier = Modifier
                                        .padding(5.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.End
                                ){
                                    items ( items.audioList ){ item ->

                                        Text(
                                            modifier = Modifier
                                                .padding(
                                                    top = 2.dp,
                                                    bottom = 2.dp
                                                )
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(Color.Black.copy(alpha = 0.6f))
                                                .fillMaxWidth()
                                                .clickable(
                                                    interactionSource = interactionSource,
                                                    indication = LocalIndication.current,
                                                    onClick = {
                                                        setAudio( item.mediaTrackGroupId )
                                                    }
                                                ),
                                            text = "   ${item.languageIso6392Name}   ",
                                            color = Color.White
                                        )

                                    }
                                }

                            }

                        }

                    }

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
                            .clip(
                                RoundedCornerShape(20.dp)
                            )
                            .background( backGround )
                            .padding(
                                start = 5.dp,
                                end = 5.dp
                            ),
                        time = currentPosition
                    )

                    SliderPlayer(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp, end = 10.dp),
                        position = currentPosition,
                        duration = duration,
                        onSeek = { newPos ->
                            if ( player.playbackState == Player.STATE_READY ){
                                player.seekTo( newPos )
                                currentPosition = player.currentPosition
                            }
                        },
                        timeInfo = true
                    )

                    TimeInfo(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(20.dp)
                            )
                            .background( backGround )
                            .padding(
                                start = 5.dp,
                                end = 5.dp
                            ),
                        time = duration
                    )

                    Spacer(Modifier.size(10.dp))
                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ){

                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ){

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
                                .background( backGround )
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
                                    .background( backGround )
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
                                    .background( backGround )
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
                                .background( backGround )
                                .padding(10.dp),
                            tint = Color.White
                        )

                    }

                    Row (
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ){

                        if ( player.volume == 0f ){
                            Icon(
                                painter = painterResource(R.drawable.mute),
                                contentDescription = "Mute",
                                modifier = Modifier
                                    .clickable(
                                        onClick = {
                                            player.volume = currentVolume
                                        }
                                    )
                                    .clip(RoundedCornerShape(30.dp))
                                    .background( backGround )
                                    .padding(10.dp),
                                tint = Color.White
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.volume_on),
                                contentDescription = "Play",
                                modifier = Modifier
                                    .clickable(
                                        onClick = {
                                            player.volume = 0f
                                        }
                                    )
                                    .clip(RoundedCornerShape(30.dp))
                                    .background( backGround )
                                    .padding(10.dp),
                                tint = Color.White
                            )
                        }

                        SliderPlayer(
                            modifier = Modifier
                                .width(100.dp)
                                .padding(start = 10.dp, end = 10.dp),
                            position = ( currentVolume * 100 ).toLong(),
                            duration = 100,
                            onDrag = { newPos ->
                                currentVolume = newPos.toFloat() / 100
                                player.volume = currentVolume
                            }
                        )

                        Spacer(Modifier.size(10.dp))

                        Icon(
                            painter = painterResource(R.drawable.settings),
                            contentDescription = "Settings",
                            modifier = Modifier
                                .pointerInput(Unit){
                                    detectTapGestures(
                                        onTap = {
                                            showItems = !showItems
                                        }
                                    )
                                }
                                .clip(RoundedCornerShape(30.dp))
                                .background( backGround )
                                .padding(10.dp),
                            tint = Color.White
                        )

                        Spacer(Modifier.size(20.dp))

                    }

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

data class MediaItems(
    val audioList: List<Language> = listOf()
)

data class Language(
    val mediaTrackGroupId: String,
    val languageIso6392Name: String
)

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