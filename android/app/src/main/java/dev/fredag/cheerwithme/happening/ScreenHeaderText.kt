package dev.fredag.cheerwithme.happening

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScreenHeaderText(text: String) {
    Text(
        text = text,
        fontSize = 32.sp,
        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp)
    )
}