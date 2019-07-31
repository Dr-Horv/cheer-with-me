package dev.fredag.cheerwithme

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.cheers_view.*
import org.koin.android.ext.android.get

class CheerViewFragment: Fragment() {
    val CHANNEL_ID = "cheer_with_me";

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cheers_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
    //override fun onViewCreate(savedInstanceState: Bundle?) {

        val iconFont = FontManager.getTypeface(get(), FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(icons_container, iconFont)

        val queue = Volley.newRequestQueue(get())
        val url = "http://cheer-with-me.fredag.dev/"
        val notificationService: NotificationService = get()
        notificationService.createNotificationChannel(CHANNEL_ID, get())
        notificationService.showNotification(CHANNEL_ID, "App opened", get())

        for (child in icons_container.children) {
            if (child is Button) {
                child.setOnClickListener { view ->

                    println(view.tag)


                    val stringRequest = StringRequest(
                        Request.Method.GET, url,
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