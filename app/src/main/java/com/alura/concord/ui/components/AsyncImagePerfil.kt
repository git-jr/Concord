package com.alura.concord.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.alura.concord.R

@Composable
fun AsyncImage(
    modifier: Modifier = Modifier, imageUrl: String? = null, description: String? = null
) {
    AsyncImage(
        modifier = modifier,
        contentScale = ContentScale.Crop,
        model = ImageRequest.Builder(LocalContext.current).data(imageUrl).build(),
        placeholder = painterResource(R.drawable.image_place_holder),
        error = painterResource(R.drawable.image_place_holder),
        contentDescription = description,
    )
}