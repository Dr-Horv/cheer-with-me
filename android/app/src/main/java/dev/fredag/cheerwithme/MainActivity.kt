package dev.fredag.cheerwithme

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dev.fredag.cheerwithme.data.UserState
import dev.fredag.cheerwithme.data.backend.BackendModule

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottomNavigation.setupWithNavController(navController)
        UserState.loggedIn.postValue(BackendModule.hasAccessKey(applicationContext))
        lifecycleScope.launchWhenStarted {
            UserState.loggedIn.observe(this@MainActivity) {
                Log.d("LoggedIn", it.toString())
                if(!it) {
                    bottomNavigation.visibility = View.GONE
                    navController.navigate(R.id.action_global_loginFragment)
                } else {
                    bottomNavigation.visibility = View.VISIBLE
                }
            }
        }
    }
}