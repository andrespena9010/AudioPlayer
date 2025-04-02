package com.exaple.audioplayer.ui.custom

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun TimeInfo(
    modifier: Modifier = Modifier,
    time: Long
){
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Text(
            text = formatTime(time),
            color = Color.White
        )
    }
}

@SuppressLint("DefaultLocale")
fun formatTime( millis: Long ): String {
    val totalSeconds = millis / 1000
    val hours = totalSeconds / 3600
    val remainingSeconds = totalSeconds % 3600
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60

    return String.format("%01d:%02d:%02d", hours, minutes, seconds)
}

@Preview
@Composable
private fun PreviewInfo(){
    TimeInfo(
        time = 92894230L
    )
}