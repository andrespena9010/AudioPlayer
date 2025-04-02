package com.exaple.audioplayer.ui.custom

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun SliderPlayer(
    position: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    var width by remember { mutableIntStateOf( 0 ) }
    var dragPos by remember { mutableFloatStateOf( 0f ) }
    var dragged by remember { mutableStateOf( false ) }

    val sliderFraction: Float = if (dragged && width != 0) {
        //Log.i("INFOTEST","Recompone ancho: DRAGGED" )
        ( dragPos / width ).coerceIn(0f, 1f)
    } else {
        //Log.i("INFOTEST","Recompone ancho" )
        ( position / duration.toFloat() ).coerceIn(0f, 1f)
    }

    Box(
        modifier = modifier
            .onSizeChanged { newSize ->
                width = newSize.width
            }
            .pointerInput(Unit){
                detectTapGestures(
                    onTap = { offset ->
                        onSeek( ( duration * ( offset.x / width ).coerceIn(0f, 1f) ).toLong() )
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragged = true
                        dragPos = 0f
                        dragPos += offset.x
                    },
                    onDrag = { pointer, offset ->
                        pointer.consume()
                        dragPos += offset.x
                    },
                    onDragEnd = {
                        val fraction = ( dragPos / width ).coerceIn(0f, 1f)
                        dragged = false
                        onSeek( ( duration * fraction ).toLong() )
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Color.Gray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.CenterStart
        ){

            // --- Barra de progreso (negro) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth( sliderFraction ) // with(density) { (sliderPosition.toFloat() * width).toDp() }
                    .height(3.dp)
                    .background(Color.Black)
            )

        }

        if ( dragged ){

            TimeInfo(
                modifier = Modifier
                    .offset { IntOffset( ( dragPos -50 ).toInt() , -100 ) }
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
                    .background( Color.Gray.copy(alpha = 0.5f) )
                    .padding(5.dp),
                time = ( ( dragPos / width ).coerceIn(0f, 1f) * duration ).toLong()
            )
        }

    }
}

@Preview(
    widthDp = 100,
    heightDp = 30
)
@Composable
private fun Preview(){
    SliderPlayer(
        position = 450L,
        duration = 900L,
        onSeek = {}
    )
}