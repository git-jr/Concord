package com.alura.concord.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.alura.concord.R
import com.alura.concord.data.Message
import kotlinx.coroutines.delay

@Composable
fun MessageItemUser(message: Message) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Row(Modifier.padding(start = 50.dp)) {
            val hasImage = message.mediaLink.isNotEmpty()
            val intrinsicSizeLayout = if (hasImage) {
                IntrinsicSize.Min
            } else {
                IntrinsicSize.Max
            }

            Column(
                Modifier
                    .shadow(1.dp, shape = RoundedCornerShape(10.dp, 0.dp, 10.dp, 10.dp))
                    .background(
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        shape = RoundedCornerShape(10.dp, 0.dp, 10.dp, 10.dp),
                    )
                    .padding(4.dp)
                    .width(intrinsicSizeLayout)
            ) {
                if (hasImage) {
                    AsyncImage(
                        modifier = Modifier
                            .widthIn(
                                min = 200.dp,
                                max = 300.dp
                            )
                            .padding(2.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        imageUrl = message.mediaLink,
                        contentScale = ContentScale.FillWidth
                    )
                }
                Text(
                    text = message.content,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Row(
                    modifier =
                    Modifier.fillMaxWidth()
                        .padding(start = 20.dp, end = 4.dp)
                        .offset(y = (-4).dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.date,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        painterResource(id = R.drawable.ic_action_all_done),
                        stringResource(R.string.message_status),
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageItemAi(message: Message) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(Modifier.padding(end = 50.dp)) {
            val hasImage = message.mediaLink.isNotEmpty()
            val intrinsicSizeLayout = if (hasImage) {
                IntrinsicSize.Min
            } else {
                IntrinsicSize.Max
            }
            Column(
                Modifier
                    .background(
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = RoundedCornerShape(0.dp, 25.dp, 25.dp, 25.dp)
                    )
                    .padding(16.dp)
                    .width(intrinsicSizeLayout),
            ) {
                if (hasImage) {
                    AsyncImage(
                        modifier = Modifier.widthIn(
                            min = 200.dp,
                            max = 300.dp
                        ).padding(2.dp)
                            .clip(RoundedCornerShape(10)),
                        imageUrl = message.mediaLink,
                        contentScale = ContentScale.FillWidth
                    )
                }
                Text(
                    message.content,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

@Composable
fun MessageItemAiOld(value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row {
            Box(modifier = Modifier.weight(5f)) {
                Text(
                    value,
                    Modifier
                        .background(
                            color = Color("#FFE9EFFD".toColorInt()),
                            shape = RoundedCornerShape(0.dp, 25.dp, 25.dp, 25.dp)
                        )
                        .padding(16.dp),
                    color = Color.Black,
                )
            }
            Spacer(Modifier.size(50.dp))
        }
    }
}

@Composable
fun MessageItemLoad() {
    val defaultOpacity = 0.1f

    val balls = listOf(
        remember { Animatable(defaultOpacity) },
        remember { Animatable(defaultOpacity) },
        remember { Animatable(defaultOpacity) },
    )

    balls.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {

            val interval = 210L
            val minDelay = interval * 2
            val maxDelay = interval * 4
            var delay = minDelay
            when (index) {
                0 -> delay = minDelay
                1 -> delay = interval * 3
                2 -> delay = maxDelay
            }

            delay(delay)
            animatable.animateTo(
                targetValue = 1.0f, animationSpec = infiniteRepeatable(
                    animation = tween(maxDelay.toInt(), easing = FastOutLinearInEasing),
                    repeatMode = RepeatMode.Reverse,
                )
            )
        }
    }

    val ballsMap = balls.map { it.value }

    Row(
        Modifier
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ballsMap.forEachIndexed { index, animatable ->
            Box(
                Modifier
                    .size(24.dp)
                    .padding(2.dp)
                    .scale(animatable)
                    .alpha(animatable)
                    .background(color = Color("#bbcaed".toColorInt()), shape = CircleShape)
            )
        }
    }
}

@Preview
@Composable
fun MessageItemUserPreview() {
    MessageItemUser(Message())
}


@Composable
fun MessageItemAiTest(value: String) {
    BoxWithConstraints(
        Modifier.fillMaxSize()
    ) {
        val maxWidth = this.maxWidth
        val maxHeight = this.maxHeight

        Surface(
            modifier = Modifier
                .padding(16.dp)
                .size(
                    width = maxWidth * 0.8f,
                    height = maxHeight * 0.5f
                ),
            color = Color(0xFF64B5F6),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Hello",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    )
                    Text(
                        "2:30 PM"
                    )
                }
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

