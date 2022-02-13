package dev.fredag.cheerwithme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ButtonIcon(
    @DrawableRes
    resource: Int,
    onClick : () -> Unit,
    size: Dp = 32.dp,
) {
    IconButton(onClick = {onClick()}) {
        Image(
            painter = painterResource(resource),
            colorFilter = ColorFilter.tint(MaterialTheme.colors.secondary),
            modifier = Modifier
                .size(size),
            contentDescription = ""
        )

    }
}