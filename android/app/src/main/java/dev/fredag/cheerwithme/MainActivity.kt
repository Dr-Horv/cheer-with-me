package dev.fredag.cheerwithme

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.children
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.iid.FirebaseInstanceId

import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.android.ext.android.get

class Controller(val notificationService : NotificationService)

var notificationModule = module {
    single { Controller(get()) }
    single { NotificationService() }
}

class MainActivity : AppCompatActivity() {
    val CHANNEL_ID = "cheer_with_me";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            Log.d("MAIN_STUFF", "STUFFFF" + it.result?.token)
        }

        startKoin {
            // Android context
            androidContext(this@MainActivity)
            // modules
            modules(notificationModule)
        }


        val iconFont = FontManager.getTypeface(applicationContext, FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(icons_container, iconFont)

        val queue = Volley.newRequestQueue(this)
        val url = "http://cheer-with-me.fredag.dev/"
        val notificationService: NotificationService = get()
        notificationService.createNotificationChannel(CHANNEL_ID, this)
        notificationService.showNotification(CHANNEL_ID, "App opened", this)

        for (child in icons_container.children) {
            if (child is Button) {
                child.setOnClickListener { view ->

                    println(view.tag)


                    val stringRequest = StringRequest(Request.Method.GET, url,
                        Response.Listener<String> { response ->
                            // Display the first 500 characters of the response string.
                            println("Response: %s".format(response.toString()))
                        },
                        Response.ErrorListener { error ->
                            // TODO: Handle error
                            println(error.message)
                            println("That didn't work!")
                        })
                    queue.add(stringRequest)


                }
            }
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