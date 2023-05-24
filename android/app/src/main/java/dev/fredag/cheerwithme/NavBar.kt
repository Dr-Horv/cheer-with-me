package dev.fredag.cheerwithme

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavBar(navController: NavHostController) {
    Column(verticalArrangement = Arrangement.Bottom) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            NavButton(
                painterResource(R.drawable.ic_beer_black_24dp),
                AuthenticatedScreen.Checkin,
                navController
            )
            NavButton(
                painterResource(R.drawable.ic_map_black_24dp),
                AuthenticatedScreen.Map,
                navController
            )
            NavButton(
                painterResource(R.drawable.ic_calendar_black_24dp),
                AuthenticatedScreen.Happenings,
                navController
            )
            NavButton(
                painterResource(R.drawable.ic_people_black_24dp),
                AuthenticatedScreen.Friends,
                navController
            )
            NavButton(
                painterResource(R.drawable.ic_profile_black_24dp),
                AuthenticatedScreen.Profile,
                navController
            )
        }

        Divider(color = Color.Gray, thickness = 1.dp)
    }


}

@Composable
fun NavButton(
    painter: Painter,
    targetRoute: AuthenticatedScreen,
    navController: NavHostController
) {
    val isActive = targetRoute.route == navController.currentBackStackEntry?.destination?.route
    Button(
        onClick = { navController.navigate(targetRoute.route) },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.background,
            contentColor = if (isActive) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onBackground
        )
    ) {
        Icon(painter = painter, contentDescription = targetRoute.route, modifier = Modifier)
    }
}