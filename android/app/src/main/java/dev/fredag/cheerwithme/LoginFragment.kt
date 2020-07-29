package dev.fredag.cheerwithme

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.Composable
import androidx.compose.Recomposer
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.foundation.currentTextStyle
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.Surface
import androidx.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.fredag.cheerwithme.data.UserState
import dev.fredag.cheerwithme.ui.CheerWithMeTheme


class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_login, container, false) as ViewGroup

        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.bottom_navigation)
        navBar.visibility = View.GONE

        fragmentView.setContent(Recomposer.current()) {
            Login(this)
        }

        return fragmentView
    }
}

fun handleLoginSuccess(fragment: LoginFragment, account: GoogleSignInAccount) {
    UserState.loggedIn.postValue(true)
    val navController = Navigation.findNavController(fragment.requireView())
    Log.d("Login", "navigating to startDestination")
    navController.navigate(navController.graph.startDestination)

}

fun handleLoginButtonPress(fragment: LoginFragment, context: Context) {
    val oauthServerClientId = context.getString(R.string.oauth_server_client_id)
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(oauthServerClientId)
        .requestProfile()
        .requestServerAuthCode(oauthServerClientId)
        .requestEmail()
        .build()
    val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
    val signInIntent = mGoogleSignInClient.signInIntent
    val startForResult =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.d("Login", "Got login result: ${it.resultCode}")
            if (it.resultCode == Activity.RESULT_OK) {
                val account = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                    .getResult(ApiException::class.java)
                if (account == null) {
                    Log.d("Login", "Failed to sign in")
                    return@registerForActivityResult
                }
                Log.d("Login", "Granted scopes ${account.grantedScopes}")
                Log.d("Login", "Requested scopes ${account.requestedScopes}")
                Log.d(
                    "Login",
                    "${account.displayName} ${account.email} '${account.grantedScopes}' ${account.id} ${account.idToken} ${account.serverAuthCode}"
                )
                handleLoginSuccess(fragment, account)
            }
        }
    Log.d("Login", "Starting signInIntent")
    startForResult.launch(signInIntent)
}

@Composable
fun Login(loginFragment: LoginFragment) = CheerWithMeTheme {
    Log.d("Login", currentTextStyle().color.toString())
    Surface {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Login Screen")
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalGravity = Alignment.CenterHorizontally
            ) {
                val context = ContextAmbient.current
                Button(
                    onClick = {
                        Log.d("Login", "ButtonClicked!")
                        handleLoginButtonPress(loginFragment, context)
                    },
                    backgroundColor = Color.White,
                ) {
                    Text("Sign in with Google", color = Color.Black)
                }
            }
        }
    }
}