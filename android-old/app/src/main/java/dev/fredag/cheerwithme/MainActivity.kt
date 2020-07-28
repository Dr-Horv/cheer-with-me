package dev.fredag.cheerwithme

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dev.fredag.cheerwithme.service.BackendService
import dev.fredag.cheerwithme.service.NotificationService
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import org.koin.dsl.module
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.coroutines.suspendCoroutine


class Controller(val notificationService: NotificationService)

var notificationModule = module {
    single { Controller(get()) }
    single { NotificationService() }
}

data class GoogleLoginResponse(val accessToken: String)
data class GoogleLoginRequest(val code: String)

class MainActivity : AppCompatActivity() {
    val RC_SIGN_IN = 15

    private lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BackendService.getInstance(this.applicationContext)

        this.supportActionBar?.let {
            it.hide()
        }


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.oauth_server_client_id))
            .requestProfile()
            .requestServerAuthCode(getString(R.string.oauth_server_client_id))
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
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if(account == null) {
                Log.d("SignIn", "Failed to signin")
                return
            }
            Log.d("Granted scopes", account.grantedScopes.toString())
            Log.d("Requested scopes", account.requestedScopes.toString())
            Log.d(
                "GoogleLogin",
                "${account.displayName} ${account.email} '${account.grantedScopes}' ${account.id} ${account.idToken} ${account.serverAuthCode}"
            )
            login_status.text = "Hello, ${account.displayName}. Wait some more."

            val jwt = account.idToken
            if(jwt !== null) {
                BackendService.token = jwt
            }

            BackendService.getInstance(this.applicationContext).post(
                "login/google",
                GoogleLoginRequest(code = account.serverAuthCode!!),
                GoogleLoginResponse::class.java
            ) {
                if(it.isSuccess) {
                    BackendService.token = it.getOrThrow().accessToken
                    startApp()
                } else {
                    Log.d("GoogleLogin", "Failed")
                }
            }
        } catch (e: ApiException) {

            Log.d("GoogleLogin failed", e.toString())
            Log.d("GoogleLogin failed", e.statusCode.toString())

        }
    }
}
