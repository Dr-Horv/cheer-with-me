package dev.fredag.cheerwithme

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import dev.fredag.cheerwithme.data.UserState
import dev.fredag.cheerwithme.friends.UserIcon

@Composable
fun ProfileView(navController: NavHostController) {
    val user by UserState.user.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = user.nick,
            color = MaterialTheme.colors.onSecondary
        )
        UserIcon(user, 120.dp, Modifier.padding(0.dp, 20.dp))
        Button(onClick = {
            clearAccessToken(context)
            navController.navigate(AuthenticatedScreen.Login.route)
        }) {
            Text(text = "Logout")

        }
    }

}