package dev.fredag.cheerwithme

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dev.fredag.cheerwithme.data.UserState
import dev.fredag.cheerwithme.data.backend.BackendModule
import dev.fredag.cheerwithme.friends.Friends
import dev.fredag.cheerwithme.friends.FriendsList
import dev.fredag.cheerwithme.friends.FriendsViewModel
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
                    Column() {
                        NavBar(navController)
                        Router(navController)
                    }
                }
            }
        }
        // TODO
//        setContentView(R.layout.activity_main)
//        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
//        bottomNavigation.setupWithNavController(navController)
//        UserState.loggedIn.postValue(BackendModule.hasAccessKey(applicationContext))

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


enum class Route {
    Login,
    Checkin,
    Map,
    Calendar,
    Friends,
    Profile,
}

@OptIn(FlowPreview::class)
@Composable
fun Router(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Route.Login.name) {
        composable(Route.Login.name) {
            Login(hiltViewModel())
        }
        composable(Route.Friends.name) {
            Friends(hiltViewModel())
        }
        //composable(Route.Map.name) { MapView(/*...*/) }
        //composable(Route.Calendar.name) { CalendarView(/*...*/) }
        //composable(Route.Checkin.name) { CheckinView(/*...*/) }
    }

}

@Composable
fun FakeLogin() {
    Text(text = "FakeLogin")
}

@Composable
fun NavBar(navController: NavHostController) {

    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            NavButton(painterResource(R.drawable.ic_beer_black_24dp), Route.Checkin, navController)
            NavButton(painterResource(R.drawable.ic_map_black_24dp), Route.Map, navController)
            NavButton(
                painterResource(R.drawable.ic_calendar_black_24dp),
                Route.Calendar,
                navController
            )
            NavButton(
                painterResource(R.drawable.ic_people_black_24dp),
                Route.Friends,
                navController
            )
            NavButton(
                painterResource(R.drawable.ic_profile_black_24dp),
                Route.Profile,
                navController
            )
        }

        Divider(color = Color.Gray, thickness = 1.dp)
    }


}

@Composable
fun NavButton(
    painter: Painter,
    targetRoute: Route,
    navController: NavHostController
) {
    Button(
        onClick = { navController.navigate(targetRoute.name) },
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
    ) {
        Icon(painter = painter, contentDescription = targetRoute.name, modifier = Modifier)
    }
}