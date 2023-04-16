package com.example.englishassistantapp.ui.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.PressGestureScope
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.englishassistantapp.R

@Composable
fun ConversationButton(
    isMicTapDisable: Boolean = false,
    isUserSpeaking: Boolean = false,
    onMicPressing: suspend PressGestureScope.(Offset) -> Unit,
    recognizedText: String = ""
) {
    val buttonBGColor by animateColorAsState(targetValue = if(isUserSpeaking) Color.LightGray else Color.Gray)
    BottomBtnArea(
        isSpeaking = isUserSpeaking,
        buttonBGColor = buttonBGColor,
        onPress = onMicPressing,
        isMicTapDisable = isMicTapDisable,
        recognizedText = recognizedText
    )
}

@Composable
fun BottomBtnArea(
    onPress: suspend PressGestureScope.(Offset) -> Unit,
    isSpeaking: Boolean,
    buttonBGColor : Color,
    isMicTapDisable : Boolean,
    elevation : Dp = 8.dp,
    recognizedText: String = ""
) {
    val buttonSize = 80.dp
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = recognizedText)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(if (isMicTapDisable) Color.Gray else Color.Transparent)
        ) {
            if(!isMicTapDisable) {
                Canvas(
                    modifier = Modifier
                        .size(buttonSize)
                        .align(Alignment.Center)
                        .shadow(
                            elevation = elevation,
                            shape = CircleShape
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(onPress = onPress)
                        },
                ) {
                    drawCircle(
                        color = buttonBGColor,
                        radius = this.size.width/2,
                        center = center
                    )
                }
            }
            Image(
                modifier = Modifier
                    .size(buttonSize)
                    .align(Alignment.Center),
                painter = painterResource(id = if(isSpeaking) R.drawable.baseline_mic_24 else R.drawable.baseline_mic_none_24),
                contentDescription = "mic"
            )
        }
    }
}

/**
 * TODO
 */
@Composable
fun SoundWaveView(amplitude: Float, modifier: Modifier = Modifier) {
    val waveAnimation = rememberInfiniteTransition()
    val scale by waveAnimation.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .padding(32.dp)
            .border(
                shape = CircleShape,
                width = 4.dp,
                color = Color.White,
            )
    ) {
        val waveHeight = (amplitude * 100).coerceIn(0f, 100f) // 振幅は0~1の範囲なので、0~100に変換する
        val waveColor = if (amplitude > 0.5f) Color.Red else Color.Yellow // 振幅が大きいほど色を変える
        val waveWidth = 48.dp

        Wave(
            height = waveHeight,
            width = waveWidth,
            color = waveColor,
            scale = scale
        )
    }
}

@Composable
fun Wave(
    height: Float,
    width: Dp,
    color: Color,
    scale: Float,
    modifier: Modifier = Modifier,
) {
//    val path = remember { Path() }
    Canvas(
        modifier = modifier
            .size(width, height.dp)
            .padding(end = 8.dp)
    ) {
        drawPath(
            path = Path().also {path ->
                path.reset()

                path.moveTo(0f, 0f)
                path.lineTo(0f, height)
                path.quadraticBezierTo(
                    width.toPx() * 0.25f,
                    height * (1 - scale),
                    width.toPx() * 0.5f,
                    height
                )
                path.quadraticBezierTo(
                    width.toPx() * 0.75f,
                    height * (1 + scale),
                    width.toPx(),
                    height
                )
                path.lineTo(width.toPx(), 0f)
                path.close()
            },
            color = color,
            style = Stroke(width = 0f)
        )
    }
}