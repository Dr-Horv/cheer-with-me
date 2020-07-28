package dev.fredag.cheerwithme

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.*
import androidx.compose.Recomposer
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.VerticalAlignmentLine
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.foundation.currentTextStyle
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Button
import androidx.ui.material.IconButton
import androidx.ui.material.Surface
import androidx.ui.unit.dp
import dev.fredag.cheerwithme.ui.CheerWithMeTheme
import dev.fredag.cheerwithme.ui.gray

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val fragmentView = inflater.inflate(R.layout.fragment_login, container, false) as ViewGroup

        fragmentView.setContent(Recomposer.current()) {
            Login()
        }
        return fragmentView
    }
}

@Composable
fun Login() = CheerWithMeTheme {
    Log.d("Login", currentTextStyle().color.toString())
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Login Screen")
            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                verticalArrangement = Arrangement.Center, horizontalGravity = Alignment.CenterHorizontally) {
                Button(onClick = {
                    Log.d("Login", "ButtonClicked!")
                },
                    backgroundColor = Color.White,
                ) {
                    Text("Sign in with Google", color = Color.Black)
                }
            }
        }
}