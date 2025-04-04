package com.exaple.audioplayer.ui.viewmodels

import android.content.Context
import android.view.TextureView
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.exaple.audioplayer.data.Language
import com.exaple.audioplayer.data.MediaItems
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class PlayerViewModel: ViewModel() {

    var job: Job?  = null

    private lateinit var player: ExoPlayer

    private val _items = MutableStateFlow( MediaItems() )
    val items: StateFlow<MediaItems> = _items.asStateFlow()

    private val _currentPosition = MutableStateFlow( 0L )
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _isPlaying = MutableStateFlow( false )
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isMute = MutableStateFlow( false )
    val isMute: StateFlow<Boolean> = _isMute.asStateFlow()

    private val _fordward = MutableStateFlow( false )
    val fordward: StateFlow<Boolean> = _fordward.asStateFlow()

    private val _backward = MutableStateFlow( false )
    val backward: StateFlow<Boolean> = _backward.asStateFlow()

    private val _showControls = MutableStateFlow( false )
    val showControls: StateFlow<Boolean> = _showControls.asStateFlow()

    private val _showLanguageItems = MutableStateFlow( false )
    val showLanguageItems: StateFlow<Boolean> = _showLanguageItems.asStateFlow()

    private val _loading = MutableStateFlow( false )
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _duration = MutableStateFlow( 1L )
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _currentVolume = MutableStateFlow( 0.7f )
    val currentVolume: StateFlow<Float> = _currentVolume.asStateFlow()

    @OptIn(UnstableApi::class)
    fun init(
        mediaItems: List<MediaItem>,
        context: Context
    ): ExoPlayer? {

        // Selector de pistas configurado para video 4K
        val trackSelector = DefaultTrackSelector(context).apply {
            parameters = buildUponParameters()
                .setMaxVideoSize(3840, 2160)
                .setMaxVideoFrameRate(60)
                .setForceHighestSupportedBitrate(true)
                .build()
        }

        // Configuración del reproductor ExoPlayer
        player = ExoPlayer
            .Builder(context)
            .setTrackSelector(trackSelector)
            .setLoadControl(
                DefaultLoadControl
                    .Builder()
                    .setBufferDurationsMs(
                        60 * 1000,
                        120 * 1000,
                        5000,
                        5000
                    )
                    .build()
            )
            .build()

        player.setVideoTextureView(TextureView(context))
        player.setMediaItems(mediaItems)
        player.prepare()
        player.playWhenReady = true

        player.addListener(
            object: Player.Listener{
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when ( playbackState ){

                        Player.STATE_READY -> {
                            setInfoItems()
                            _loading.update { false }
                            _duration.update { player.duration }
                            hide()
                        }

                        Player.STATE_BUFFERING -> {
                            _loading.update { true }
                        }

                        Player.STATE_ENDED -> {

                        }

                        Player.STATE_IDLE -> {

                        }
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    if (reason == Player.MEDIA_ITEM_TRANSITION_REASON_AUTO) {
                        _duration.update { player.duration }
                    }
                }
            }
        )

        _showControls.update { true }

        hide()

        updateUI()

        return player

    }

    fun updateUI(){
        viewModelScope.launch {
            while ( true ){
                _currentPosition.update { player.currentPosition }
                _isPlaying.update { player.isPlaying }
                _isMute.update { player.volume == 0f }
                delay(100)
            }
        }
    }

    fun jumpTo(time: Long, left: Boolean = false ){
        viewModelScope.launch {
            if ( player.playbackState == Player.STATE_READY ){
                var advance = 0L
                if ( left ) {
                    _backward.update { true }
                    advance = currentPosition.value - (time)
                } else {
                    _fordward.update { true }
                    advance = currentPosition.value + (time)
                }
                player.seekTo( advance )
                _currentPosition.update { player.currentPosition }
                delay(100)
                if ( left ) _backward.update { false } else _fordward.update { false }
            }
        }
    }

    fun hide(){
        if ( job?.isActive == true ){
            job!!.cancel()
        }
        job = viewModelScope.launch {
            delay(5000)
            if ( player.playbackState != Player.STATE_BUFFERING ){
                _showControls.update { false }
                _showLanguageItems.update { false }
            }
        }
    }

    fun showControls( value: Boolean ){
        _showControls.update { value }
    }

    fun showLanguageItems( value: Boolean ){
        _showLanguageItems.update { value }
    }

    fun seekTo( millis: Long ){
        if ( player.playbackState == Player.STATE_READY ){
            player.seekTo( millis )
            _currentPosition.update { player.currentPosition }
        }
    }

    fun pause(){
        player.pause()
    }

    fun play(){
        player.play()
    }

    fun goToNextMedia(){
        if ( player.hasNextMediaItem() ) {
            _currentPosition.update { 0L }
            player.seekToNext()
            player.play()
        }
    }

    fun goToPreviousMedia(){
        if ( player.hasPreviousMediaItem() ) {
            _currentPosition.update { 0L }
            player.seekToPrevious()
            player.play()
        }
    }

    @OptIn(UnstableApi::class)
    fun setInfoItems(){
        var audioLs = mutableListOf<Language>()
        player.currentTracks.groups.forEach { group ->
            if (group.type == C.TRACK_TYPE_AUDIO) {
                if ( group.mediaTrackGroup.length > 0 ){
                    val format = group.mediaTrackGroup.getFormat(0)
                    if ( format.language.toString() != "und" ){
                        audioLs.add(
                            Language(
                                mediaTrackGroupId = group.mediaTrackGroup.id,
                                languageIso6392Name = format.language.toString()
                            )
                        )
                    }
                }
            }
        }
        _items.update { MediaItems( audioList = audioLs ) }
    }

    @OptIn(UnstableApi::class)
    fun setAudio( mediaTrackGroupId: String ){
        if (player.playbackState != Player.STATE_READY) {
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

    fun setVolume( position: Long ){
        _currentVolume.update { position.toFloat() / 100 }
        player.volume = currentVolume.value
    }

    fun mute( value: Boolean ){
        if ( value ){
            player.volume = 0f
        } else {
            player.volume = currentVolume.value
        }
    }

}

object ExoplayerViewModel: PlayerViewModel()