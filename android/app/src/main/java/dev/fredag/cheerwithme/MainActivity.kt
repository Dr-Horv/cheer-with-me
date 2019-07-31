package dev.fredag.cheerwithme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId

import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Controller(val notificationService : NotificationService)

var notificationModule = module {
    single { Controller(get()) }
    single { NotificationService() }
}

class MainActivity : AppCompatActivity() {

    val fragmentManager = supportFragmentManager

    private var currentNavigationId = -1


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        if (currentNavigationId == item.itemId) {
            return@OnNavigationItemSelectedListener false
        }
        currentNavigationId = item.itemId

        lateinit var switchToFragment: Fragment
        var retValue = false
        when (item.itemId) {
            R.id.navigation_cheer -> {
                switchToFragment = CheerViewFragment()
                retValue = true
            }
            R.id.navigation_map-> {
                switchToFragment = MapViewFragment()
                retValue = true
            }
            R.id.navigation_calendar -> {
                switchToFragment = CheerViewFragment()
                retValue = true
            }
            R.id.navigation_friends -> {
                switchToFragment = CheerViewFragment()
                retValue = true
            }
            R.id.navigation_profile-> {
                switchToFragment = CheerViewFragment()
                retValue = true
            }
        }
        val fragmentTransaction = fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        fragmentTransaction.replace(R.id.fragment_container, switchToFragment)
        fragmentTransaction.commit()

        retValue
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, CheerViewFragment())
        fragmentTransaction.commit()

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            Log.d("MAIN_STUFF", "STUFFFF" + it.result?.token)
        }

        startKoin {
            // Android context
            androidContext(this@MainActivity)
            // modules
            modules(notificationModule)
        }



    }


}

class NotificationService {
    fun showNotification(channelId: String, title: String, context: Context) {
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle(title)
            //.setContentText("Woop content")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, notificationBuilder.build())
        }
    }

    fun createNotificationChannel(channel_id: String, context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.default_notification_channel)
            val descriptionText = context.getString(R.string.default_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channel_id, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}