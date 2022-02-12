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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.fredag.cheerwithme.R
import dev.fredag.cheerwithme.data.backend.User
import dev.fredag.cheerwithme.ui.beerYellow

@Composable
public fun UserWithIcon(
    it: User,
) {

    val img = if (it.avatarUrl !== null) {
        TODO("Fetch image from internet for avatar here")
    } else {
        painterResource(id = R.drawable.ic_profile_black_24dp)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = img,
            modifier = Modifier
                .size(32.dp)
                .background(beerYellow, CircleShape)
                .clip(CircleShape),
            colorFilter = ColorFilter.tint(Color.White),
            contentDescription = ""
        )
        Text(
            it.nick,
            modifier = Modifier.padding(8.dp, 0.dp)
        )
    }
}