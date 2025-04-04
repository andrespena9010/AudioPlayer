package com.exaple.audioplayer.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exaple.audioplayer.ui.viewmodels.ExoplayerViewModel
import com.exaple.audioplayer.ui.viewmodels.PlayerViewModel

@Composable
fun RowScope.SliderCustom(
    viewModel: PlayerViewModel = ExoplayerViewModel,
    backGround: Color
){

    val currentPosition by viewModel.currentPosition.collectAsStateWithLifecycle()
    val duration by viewModel.duration.collectAsStateWithLifecycle()

    Spacer(Modifier.size(10.dp))

    TimeInfo(
        modifier = Modifier
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(backGround)
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
        onTapOrDragEnd = { newPos ->
            viewModel.seekTo(newPos)
        },
        timeInfo = true
    )

    TimeInfo(
        modifier = Modifier
            .clip(
                RoundedCornerShape(20.dp)
            )
            .background(backGround)
            .padding(
                start = 5.dp,
                end = 5.dp
            ),
        time = duration
    )

    Spacer(Modifier.size(10.dp))
}