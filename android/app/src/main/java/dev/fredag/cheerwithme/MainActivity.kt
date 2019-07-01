package dev.fredag.cheerwithme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.view.children
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val iconFont = FontManager.getTypeface(applicationContext, FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(icons_container, iconFont)

        val queue = Volley.newRequestQueue(this)
        val url = "http://cheer-with-me.fredag.dev/"


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
