package dev.fredag.cheerwithme

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.ui.core.*
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Surface
import androidx.ui.res.vectorResource
import androidx.ui.text.font.font
import androidx.ui.text.font.fontFamily
import androidx.ui.text.style.TextAlign
import androidx.ui.unit.TextUnit
import androidx.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import dev.fredag.cheerwithme.data.UserRepository
import dev.fredag.cheerwithme.data.UserState
import dev.fredag.cheerwithme.ui.CheerWithMeTheme
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment: Fragment() {

    @Inject lateinit var userRepository: UserRepository

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

fun handleLoginSuccess(
    fragment: LoginFragment,
    account: GoogleSignInAccount,
    loading: MutableState<Boolean>
) {
    fragment.lifecycleScope.launch {
        fragment.userRepository.loginWithGoogle(account.serverAuthCode!!, account.idToken!!)
        Log.d("Login", "Login with google complete")

    }.invokeOnCompletion {
        loading.value = false
        if(it !== null) {
            TODO("Error handle")
        }
        UserState.loggedIn.postValue(true)
        val navController = Navigation.findNavController(fragment.requireView())
        Log.d("Login", "navigating to startDestination")
        navController.navigate(navController.graph.startDestination)
    }


}

fun handleLoginButtonPress(
    fragment: LoginFragment,
    context: Context,
    loading: MutableState<Boolean>
) {
    loading.value = true
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
                handleLoginSuccess(fragment, account, loading)
            } else {
                loading.value = false
            }
        }
    Log.d("Login", "Starting signInIntent")
    startForResult.launch(signInIntent)
}

@Composable
fun Login(loginFragment: LoginFragment) = CheerWithMeTheme {
    val loading = state { false }
    Surface {
        Column(modifier = Modifier.padding(20.dp, 30.dp)) {
            Text(
                text = "Welcome",
                fontSize = TextUnit.Em(8),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalGravity = Alignment.CenterHorizontally
            ) {
                val context = ContextAmbient.current
                Image(
                    asset = vectorResource(id = R.drawable.ic_cheer_with_me),
                    modifier = Modifier.size(200.dp).clip(CircleShape)
                )
                Text(
                    "Cheer With Me is a social app that requires you to have an account in order to connect with friends and share happenings.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp, 40.dp)
                )
                Button(
                    onClick = {
                        Log.d("Login", "ButtonClicked!")
                        handleLoginButtonPress(loginFragment, context, loading)
                    },
                    backgroundColor = Color.White,
                    disabledBackgroundColor = Color.Gray,
                    padding = InnerPadding(8.dp, 0.dp, 8.dp, 0.dp),
                    enabled = !loading.value,
                ) {
                    Image(
                        asset = vectorResource(id = R.drawable.ic_btn_google_light_normal),
                        modifier = Modifier.size(50.dp)
                    )
                    Text(
                        "Sign in with Google",
                        color = Color.DarkGray,
                        fontFamily = fontFamily(listOf(font(R.font.roboto_medium))),
                        fontSize = TextUnit.Companion.Sp(14),
                        modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 0.dp)
                    )
                }
                if(loading.value) {
                    CircularProgressIndicator(
                        Modifier.padding(0.dp, 24.dp)
                    )
                }
            }
        }
    }
}