package dev.fredag.cheerwithme

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.friends_view.*
import org.json.JSONObject
import org.koin.android.ext.android.get


class FriendsFragment : Fragment() {

    val queue: RequestQueue = Volley.newRequestQueue(get())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.friends_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        //POST push/register-device   192.168.0.108
        val url = "http://192.168.0.108:8080/push/register-device"
        val jsonBody = JSONObject()

        jsonBody.put(
            "pushToken",
            "dVtOIVm46jk:APA91bGG8BkGigdXxuBmSwN5ci5K1aPzyVQXa_tkUEZ4XLxVCXQZlp2JHnQyh2Ds3ncntd231VYp0WvLiaMzt5VbfAWqvThEki4eUB-CdHSG2-bkHkQFsQb5AuG7Sa_X4-WRQHpXLKyc"
        )
        jsonBody.put("platform", "ANDROID")

        do_request_button.setOnClickListener {

            val jsonOblect = object : JsonObjectRequest(
                Request.Method.POST, url, jsonBody,
                Response.Listener { response ->
                    Log.d("Response", response.toString())
                    Toast.makeText(
                        get(),
                        "Response:  $response",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                Response.ErrorListener {
                    Log.d("ResponseError", it.toString())
                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    return mutableMapOf("Authorization" to "fuckme tejp")
                }
            }

            queue.add(jsonOblect)

        }

    }


}
