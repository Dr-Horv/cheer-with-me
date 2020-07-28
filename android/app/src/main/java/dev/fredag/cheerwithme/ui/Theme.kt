package dev.fredag.cheerwithme.ui

import android.util.Log
import androidx.compose.Composable
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette

private val DarkColorPalette = darkColorPalette(
    background = dark,
    primary = beerYellow,
    onPrimary = fontColor,
    onBackground = gray,
    onSurface = fontColor,
    primaryVariant = beerYellow,
    secondary = gray,
)

private val LightColorPalette = lightColorPalette(
    primary = purple500,
    primaryVariant = purple700,
    secondary = teal200,

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
    darkTheme: Boolean = true,/*isSystemInDarkTheme()*/
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
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