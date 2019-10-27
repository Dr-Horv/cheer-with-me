package dev.fredag.cheerwithme

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: FragmentActivity() {
    private var currentNavigationId = -1
    val fragmentManager = supportFragmentManager

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        if (currentNavigationId == item.itemId) {
            return@OnNavigationItemSelectedListener false
        }
        currentNavigationId = item.itemId

        val faIcon = TextDrawable(this)
        faIcon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20.toFloat())
        faIcon.textAlign = Layout.Alignment.ALIGN_CENTER
        faIcon.typeface = FontManager.getTypeface(this, FontManager.FONTAWESOME_REGULAR)
        faIcon.text = resources.getText(R.string.fa_icon_beer)
        val menu: View = findViewById(R.id.menu)
        val menuItem = (menu as Menu).findItem(R.id.navigation_cheer)
        menuItem.icon = faIcon


        lateinit var switchToFragment: Fragment
        var retValue = false
        when (item.itemId) {
            R.id.navigation_cheer -> {
                switchToFragment = CheerViewFragment()
                retValue = true
            }
            R.id.navigation_map -> {
                switchToFragment = MapViewFragment()
                retValue = true
            }
            R.id.navigation_calendar -> {
                switchToFragment = CheerViewFragment()
                retValue = true
            }
            R.id.navigation_friends -> {
                switchToFragment = FriendsFragment()
                retValue = true
            }
            R.id.navigation_profile -> {
                switchToFragment = CheerViewFragment()
                retValue = true
            }
        }
        invalidateOptionsMenu();

        val fragmentTransaction = fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        fragmentTransaction.replace(R.id.fragment_container, switchToFragment)
        fragmentTransaction.commit()

        retValue
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        //val iconFont = FontManager.getTypeface(get(), FontManager.FONTAWESOME_SOLID)

        //val item: View = findViewById(R.id.nav_view)
        //val item = menu.findItem(R.id.navigation_cheer)
        //item.setIcon(R.id.navigation_cheer)


        //FontManager.markAsIconContainer(item, iconFont)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_app)

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
            androidContext(this@App)
            // modules
            modules(notificationModule)
        }


    }
}


class NotificationService {
    fun showNotificationString(channelId: String, title: String, context: Context) {
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

    fun showNotification(channelId: String, notification: RemoteMessage.Notification, context: Context) {
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            //.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logo_gris))
            //.setSmallIcon(R.drawable.big_button_bg_round)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle(notification.title)
            .setContentText(notification.body)
            //.setAutoCancel(true)
            //.setSound(defaultSoundUri)
            //.setContentIntent(pendingIntent);
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        with(NotificationManagerCompat.from(context)) {
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