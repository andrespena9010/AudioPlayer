package com.exaple.audioplayer.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.exaple.audioplayer.ui.viewmodels.ExoplayerViewModel
import com.exaple.audioplayer.ui.viewmodels.PlayerViewModel

@Composable
fun LanguageItems(
    viewModel: PlayerViewModel = ExoplayerViewModel
){

    val items by viewModel.items.collectAsStateWithLifecycle()

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
                            onClick = {
                                viewModel.setAudio( item.mediaTrackGroupId )
                            }
                        ),
                    text = "   ${item.languageIso6392Name}   ",
                    color = Color.White
                )

            }
        }

    }
}