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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import dev.fredag.cheerwithme.friends.Friends
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

//        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
//        bottomNavigation.setupWithNavController(navController)

//        lifecycleScope.launchWhenStarted {
//            UserState.loggedIn.observe(this@MainActivity) {
//                Log.d("LoggedIn", it.toString())
//                if(!it) {
//                    bottomNavigation.visibility = View.GONE
//                    navController.navigate(R.id.action_global_loginFragment)
//                } else {
//                    bottomNavigation.visibility = View.VISIBLE
//                }
//            }
//        }
    }
}


sealed class AuthenticatedScreen(val route: String) {
    object Friends : AuthenticatedScreen("authenticated/friends")
    object Calendar : AuthenticatedScreen("authenticated/calendar")
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
                startDestination = if(loggedIn.value == true) AuthenticatedScreen.Checkin.route else AuthenticatedScreen.Login.route
            ) {
                composable(AuthenticatedScreen.Friends.route) {
                    Friends(hiltViewModel()) {
                        navController.navigate(
                            AuthenticatedScreen.FindFriend.route
                        ) {
                            launchSingleTop = true
                        }
                    }
                }
                composable(AuthenticatedScreen.Map.route) { Map() }
                composable(AuthenticatedScreen.FindFriend.route) { FindFriend() }
                composable(AuthenticatedScreen.Checkin.route) { Happening() }
                composable(AuthenticatedScreen.Calendar.route) { Calendar() }
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
fun FindFriend() {
    val searchName = remember { mutableStateOf("") }
    TextField(value = searchName.value, onValueChange = { searchName.value = it })
    Button(onClick = {

    }) {
        Text("Send friend request")
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
    BackendModule.clearAccessKey(context )
}

@Composable
fun Calendar() {
    Text("calendar")
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
                AuthenticatedScreen.Calendar,
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