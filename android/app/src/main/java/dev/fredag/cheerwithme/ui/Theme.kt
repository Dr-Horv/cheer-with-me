package dev.fredag.cheerwithme.ui

import android.util.Log
import androidx.compose.Composable
import androidx.ui.foundation.isSystemInDarkTheme
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette

private val DarkColorPalette = darkColorPalette(
    background = dark,
    surface = dark,
    primary = beerYellow,
    onPrimary = fontColor,
    onBackground = gray,
    onSurface = lightDark,
    onSecondary = fontColor,
    secondary = gray,
)

private val LightColorPalette = lightColorPalette(
    background = dark,
    surface = dark,
    primary = beerYellow,
    onPrimary = fontColor,
    onBackground = gray,
    onSurface = lightDark,
    onSecondary = fontColor,
    secondary = gray,

    /* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
)

@Composable
fun CheerWithMeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (false) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    Log.d("CheerWithMeTheme", darkTheme.toString())
    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}