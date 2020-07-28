package dev.fredag.cheerwithme

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.Composable
import androidx.compose.Recomposer
import androidx.ui.core.setContent
import androidx.ui.foundation.Text
import androidx.ui.foundation.currentTextStyle
import androidx.ui.material.Surface
import dev.fredag.cheerwithme.ui.CheerWithMeTheme

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
    Surface {
        Text(text = "Login Screen")
    }

}