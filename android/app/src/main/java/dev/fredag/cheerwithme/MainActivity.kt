package dev.fredag.cheerwithme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.fredag.cheerwithme.data.UserState

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        bottomNavigation.setupWithNavController(navController)
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        val that = this
        lifecycleScope.launchWhenStarted {
            UserState.loggedIn.observe(that) {
                Log.d("LoggedIn", it.toString())
                if(!it) {
                    navController.navigate(R.id.action_global_loginFragment)
                } else {
                    bottomNavigation.visibility = View.VISIBLE
                }
            }
        }
    }
}