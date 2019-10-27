package dev.fredag.cheerwithme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import org.koin.dsl.module
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class Controller(val notificationService: NotificationService)

var notificationModule = module {
    single { Controller(get()) }
    single { NotificationService() }
}

class MainActivity : AppCompatActivity() {
    val RC_SIGN_IN = 15

    private lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        this.supportActionBar?.let {
            it.hide()
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.oauth_server_client_id))
            .requestEmail()
            .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Log.d("mainactivity", "loaded")
        val signInButton: SignInButton = findViewById(R.id.sign_in_button)
        signInButton.setSize(SignInButton.SIZE_STANDARD)
        signInButton.setOnClickListener {
            login_status.text = "Logging in..."
            Log.d("mainactivity", "login pressed")
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(this)
        Log.d("mainactivity", "found already signed in user" + account.toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun startApp() {
        val intent = Intent(this, App::class.java)
        startActivity(intent)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        val context = this
        try {
            completedTask.getResult(ApiException::class.java)?.let {
                Log.d("Granted scopes", it.grantedScopes.toString())
                Log.d("Requested scopes", it.requestedScopes.toString())
                val result = it
                Log.d(
                    "GoogleLogin",
                    "${result.displayName} ${result.email} '${result.grantedScopes}' ${result.id} ${result.idToken}"
                )
                login_status.text = "Hello, ${result.displayName}. Wait some more."
                val jwt = result.idToken
                val url = "http://192.168.1.193:8080/login/google"




                val requestQueue = Volley.newRequestQueue(this)

                val request = object : StringRequest(Method.POST, url,
                    Response.Listener { response ->
                        Log.i("VOLLEY", response)
                        //cont.resume(response)

                    },
                    Response.ErrorListener { error ->
                        error.printStackTrace()
                        Log.e("VOLLEYerror", error.toString())
                        //cont.resumeWithException(error)
                    }) {
                    override fun getBodyContentType(): String {
                        return "application/json; charset=utf-8"
                    }

                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        return mapOf("Authorization" to "Bearer $jwt")
                    }

                    override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                        var responseString = ""
                        if (response != null) {
                            responseString = response.statusCode.toString()
                            // can get more details such as response.headers
                        }
                        return Response.success(
                            responseString,
                            HttpHeaderParser.parseCacheHeaders(response)
                        )
                    }
                }

                requestQueue.add(request)

//                MainScope().async {
//                    try {
//                        val resp = get(url, jwt)
//                        Log.d("mainactivity", resp)
//                        //login_status.text = "Success $resp"
//                        //startApp()
//
//                    } catch (e: Error) {
//                        login_status.text = e.toString()
//                    }
//                }

                //this.runOnUiThread {
                //    login_status.text = "Done waiting, welcome! $response"
                // }
            }
        } catch (e: ApiException) {

            Log.d("GoogleLogin failed", e.toString())

        }


    }

    suspend fun get(url: String, jwt: String?): String? = suspendCoroutine { cont ->

        val requestQueue = Volley.newRequestQueue(this)

        val request = object : StringRequest(Method.POST, url,
            Response.Listener { response ->
                Log.i("VOLLEY", response)
                //cont.resume(response)

            },
            Response.ErrorListener { error ->
                error.printStackTrace()
                Log.e("VOLLEY", error.toString())
                //cont.resumeWithException(error)
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return mapOf("Authorization" to "Bearer $jwt")
            }

            override fun parseNetworkResponse(response: NetworkResponse): Response<String> {
                var responseString = ""
                if (response != null) {
                    responseString = response.statusCode.toString()
                    // can get more details such as response.headers
                }
                return Response.success(
                    responseString,
                    HttpHeaderParser.parseCacheHeaders(response)
                )
            }
        }

        requestQueue.add(request)
    }
}
