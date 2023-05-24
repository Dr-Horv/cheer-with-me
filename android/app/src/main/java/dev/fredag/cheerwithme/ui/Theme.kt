package dev.fredag.cheerwithme.ui

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    background = dark,
    surface = lightDark,
    primary = beerYellow,
    onPrimary = white,
    onBackground = gray,
    onSurface = gray, // TODO? was light dark
    onSecondary = white,
    secondary = gray,
)

private val LightColorPalette = lightColors(
    background = dark,
    surface = dark,
    primary = beerYellow,
    onPrimary = fontColor,
    onBackground = gray,
    onSurface = gray, // TODO? was light dark
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