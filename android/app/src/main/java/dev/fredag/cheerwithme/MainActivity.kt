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


class MainActivity : AppCompatActivity() {
    val CHANNEL_ID = "cheer_with_me";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            Log.d("MAIN_STUFF", "STUFFFF" + it.result?.token)
        }


        val iconFont = FontManager.getTypeface(applicationContext, FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(icons_container, iconFont)

        val queue = Volley.newRequestQueue(this)
        val url = "http://cheer-with-me.fredag.dev/"

        createNotificationChannel()
        showNotification("App opened")

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

    private fun showNotification(title: String) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.notification_icon_background)
            .setContentTitle(title)
            //.setContentText("Woop content")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(1, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.default_notification_channel)
            val descriptionText = getString(R.string.default_notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
