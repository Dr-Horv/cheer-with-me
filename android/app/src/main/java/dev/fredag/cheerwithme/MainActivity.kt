package dev.fredag.cheerwithme

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.fredag.cheerwithme.data.UserState
import dev.fredag.cheerwithme.data.backend.BackendModule
import dev.fredag.cheerwithme.data.backend.HappeningId
import dev.fredag.cheerwithme.friends.FindFriendView
import dev.fredag.cheerwithme.friends.FriendsScreen
import dev.fredag.cheerwithme.happening.Happenings
import dev.fredag.cheerwithme.ui.CheerWithMeTheme
import kotlinx.coroutines.FlowPreview

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            CheerWithMeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Router(navController)
                }
            }
        }
        // Comment this out if you don't want to auto-login with stored access key. To test login
        UserState.loggedIn.postValue(BackendModule.hasAccessKey(applicationContext))
    }
}


sealed class AuthenticatedScreen(val route: String, val path: String = route) {
    object Friends : AuthenticatedScreen("authenticated/friends")
    object Happenings : AuthenticatedScreen("authenticated/happenings")
    object NewHappening : AuthenticatedScreen("authenticated/happenings/new")
    class Happening(happeningsId: HappeningId) : AuthenticatedScreen(
        route,
        "authenticated/happenings/$happeningsId"
    ) {
        companion object {
            const val route = "authenticated/happenings/{happeningsId}"
        }
    }

    object Profile : AuthenticatedScreen("authenticated/profile")
    object Checkin : AuthenticatedScreen("authenticated/checkin")
    object Map : AuthenticatedScreen("authenticated/map")
    object Login : AuthenticatedScreen("authenticated/login")
    object FindFriend : AuthenticatedScreen("authenticated/findfriend")
}

@Composable
public fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

@OptIn(FlowPreview::class)
@Composable
fun Router(navController: NavHostController) {

    val loggedIn = UserState.loggedIn.observeAsState()
    Scaffold(bottomBar = {
        Log.d("Bottombar render", "${navController.currentBackStackEntry?.destination?.route}")
        if (currentRoute(navController) != AuthenticatedScreen.Login.route) {
            NavBar(navController)
        }
    }) {
        Box(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 58.dp)) {
            NavHost(
                navController = navController,
                startDestination = if (loggedIn.value == true) AuthenticatedScreen.Happenings.route else AuthenticatedScreen.Login.route
            ) {
                composable(AuthenticatedScreen.Friends.route) {
                    FriendsScreen(hiltViewModel()) {
                        navController.navigate(
                            AuthenticatedScreen.FindFriend.route
                        ) {
                            launchSingleTop = true
                        }
                    }
                }
                composable(AuthenticatedScreen.Map.route) { Map() }
                composable(AuthenticatedScreen.FindFriend.route) {
                    FindFriendView(
                        navController,
                        hiltViewModel()
                    )
                }
                composable(AuthenticatedScreen.Checkin.route) { Happening() }
                composable(AuthenticatedScreen.Happening.route) {

                }
                composable(AuthenticatedScreen.NewHappening.route) {

                }
                composable(AuthenticatedScreen.Happenings.route) {
                    Happenings(
                        hiltViewModel(),
                        openAddHappeningScreen = {
                            navController.navigate(AuthenticatedScreen.NewHappening.path)
                        },
                        openHappningScreen = {
                            navController.navigate(AuthenticatedScreen.Happening(it.happeningId).path)
                        })
                }
                composable(AuthenticatedScreen.Profile.route) { Profile(navController) }
                composable(AuthenticatedScreen.Login.route) {
                    Login(hiltViewModel(), {
                        navController.navigate(AuthenticatedScreen.Checkin.route)
                    }, {
                        Log.d("Login", "login failed")
                    })
                }
            }

        }
    }
}

@Composable
fun Happening() {
    Text("Checkin")
}

@Composable
fun Map() {
    Text("map")
}

@Composable
fun Profile(navController: NavHostController) {
    val context = LocalContext.current
    Column() {
        Text("profile")
        Button(onClick = {
            clearAccessToken(context)
            navController.navigate(AuthenticatedScreen.Login.route)
        }) {
            Text(text = "Logout")

        }
    }

}

fun clearAccessToken(context: Context) {
    BackendModule.clearAccessKey(context)
}

@Composable
fun NavBar(navController: NavHostController) {
    val backStackState = navController.currentBackStackEntryAsState()
    Log.d("NavBar", "Current back stack entry ${backStackState.value}")
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
    Button(
        onClick = { navController.navigate(targetRoute.route) },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
    ) {
        Icon(painter = painter, contentDescription = targetRoute.route, modifier = Modifier)
    }
}