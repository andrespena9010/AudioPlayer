package com.exaple.audioplayer.ui.custom

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exaple.audioplayer.ui.viewmodels.ExoplayerViewModel
import com.exaple.audioplayer.ui.viewmodels.PlayerViewModel

@Composable
fun Controls(
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = ExoplayerViewModel
) {

    val items by viewModel.items.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val showControls by viewModel.showControls.collectAsStateWithLifecycle()
    val showLanguageItems by viewModel.showLanguageItems.collectAsStateWithLifecycle()
    val backGround = Color(0x33888888)

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent(pass = PointerEventPass.Final)
                        viewModel.hide()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(100.dp)
            )
        }

        DoubleTapSeekControl(
            backGround = backGround
        )

        if (showControls) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.BottomEnd
                ) {
                    if (showLanguageItems && items.audioList.size > 1) {
                        Box(
                            modifier = Modifier
                                .padding(30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            LanguageItems()
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .height(30.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SliderCustom( backGround = backGround )
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
                ) {

                    BottomControls( backGround =  backGround )

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