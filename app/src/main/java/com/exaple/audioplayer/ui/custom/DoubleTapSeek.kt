package com.exaple.audioplayer.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exaple.audioplayer.R
import com.exaple.audioplayer.ui.viewmodels.ExoplayerViewModel
import com.exaple.audioplayer.ui.viewmodels.PlayerViewModel

@Composable
fun DoubleTapSeekControl(
    viewModel: PlayerViewModel = ExoplayerViewModel,
    backGround: Color
){

    val showControls by viewModel.showControls.collectAsStateWithLifecycle()
    val fordward by viewModel.fordward.collectAsStateWithLifecycle()
    val backward by viewModel.backward.collectAsStateWithLifecycle()

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
                            viewModel.showControls( !showControls )
                            viewModel.showLanguageItems( false )
                        },
                        onDoubleTap = {
                            viewModel.jumpTo(
                                time = (15*1000),
                                left = true
                            )
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
                            viewModel.showControls( !showControls )
                            viewModel.showLanguageItems( false )
                        },
                        onDoubleTap = {
                            viewModel.jumpTo( time = (15*1000) )
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
}