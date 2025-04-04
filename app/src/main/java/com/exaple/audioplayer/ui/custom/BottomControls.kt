package com.exaple.audioplayer.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exaple.audioplayer.R
import com.exaple.audioplayer.ui.viewmodels.ExoplayerViewModel
import com.exaple.audioplayer.ui.viewmodels.PlayerViewModel

@Composable
fun BottomControls(
    viewModel: PlayerViewModel = ExoplayerViewModel,
    backGround: Color
){

    val currentVolume by viewModel.currentVolume.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val isMute by viewModel.isMute.collectAsStateWithLifecycle()
    val showLanguageItems by viewModel.showLanguageItems.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconControls(
            source = R.drawable.back,
            contentDescription = "Previous",
            onTap = { viewModel.goToPreviousMedia() },
            backGround = backGround
        )

        Spacer(Modifier.size(10.dp))

        if (isPlaying) {
            IconControls(
                source = R.drawable.pausa,
                contentDescription = "Pause",
                onTap = { viewModel.pause() },
                backGround = backGround
            )
        } else {
            IconControls(
                source = R.drawable.play,
                contentDescription = "Play",
                onTap = { viewModel.play() },
                backGround = backGround
            )
        }

        Spacer(Modifier.size(10.dp))

        IconControls(
            source = R.drawable.next,
            contentDescription = "Next",
            onTap = { viewModel.goToNextMedia() },
            backGround = backGround
        )

    }

    Row(
        modifier = Modifier
            .fillMaxSize(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (isMute) {
            IconControls(
                source = R.drawable.mute,
                contentDescription = "Mute",
                onTap = { viewModel.mute(false) },
                backGround = backGround
            )
        } else {
            IconControls(
                source = R.drawable.volume_on,
                contentDescription = "Volume on",
                onTap = { viewModel.mute(true) },
                backGround = backGround
            )
        }

        SliderPlayer(
            modifier = Modifier
                .width(100.dp)
                .padding(start = 10.dp, end = 10.dp),
            position = (currentVolume * 100).toLong(),
            duration = 100,
            onDrag = { newPos ->
                viewModel.setVolume(newPos)
            }
        )

        Spacer(Modifier.size(10.dp))

        IconControls(
            source = R.drawable.settings,
            contentDescription = "Settings",
            onTap = { viewModel.showLanguageItems(!showLanguageItems) },
            backGround = backGround
        )

        Spacer(Modifier.size(20.dp))

    }

}