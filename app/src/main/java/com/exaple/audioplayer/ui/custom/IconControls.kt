package com.exaple.audioplayer.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun IconControls(
    source: Int,
    contentDescription: String,
    backGround: Color,
    onTap: () -> Unit = {}
){
    Icon(
        painter = painterResource(source),
        contentDescription = contentDescription,
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onTap()
                    }
                )
            }
            .clip(RoundedCornerShape(30.dp))
            .background(backGround)
            .padding(10.dp),
        tint = Color.White
    )
}