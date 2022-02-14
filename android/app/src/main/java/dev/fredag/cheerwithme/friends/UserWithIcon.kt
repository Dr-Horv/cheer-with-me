package dev.fredag.cheerwithme.friends

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import dev.fredag.cheerwithme.R
import dev.fredag.cheerwithme.data.backend.User
import dev.fredag.cheerwithme.ui.beerYellow

@Composable
public fun UserWithIcon(
    it: User,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        UserIcon(it, modifier = Modifier)
        Text(
            it.nick,
            modifier = Modifier.padding(8.dp, 0.dp)
        )
    }
}

@Composable
fun UserIcon(user: User, size: Dp = 32.dp, modifier: Modifier) {
    val img = if (user.avatarUrl !== null) {
        rememberImagePainter(
            user.avatarUrl,
            builder = {
                transformations(CircleCropTransformation())
            })
    } else {
        painterResource(id = R.drawable.ic_profile_black_24dp)
    }

    val colorFilter = if (user.avatarUrl !== null) {
        null
    } else {
        ColorFilter.tint(Color.White)
    }

    Image(
        painter = img,
        modifier = Modifier
            .composed { modifier }
            .size(size)
            .background(beerYellow, CircleShape)
            .clip(CircleShape),
        colorFilter = colorFilter,
        contentDescription = ""
    )
}