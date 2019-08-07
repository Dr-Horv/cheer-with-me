package dev.fredag.cheerwithme

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.cheers_view.*
import org.koin.android.ext.android.get
import androidx.core.graphics.drawable.DrawableCompat
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat


class CheerViewFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cheers_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {


        val iconFont = FontManager.getTypeface(get(), FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(icons_container, iconFont)

        val queue = Volley.newRequestQueue(get())
        val url = "http://cheer-with-me.fredag.dev/"

        for (child in icons_container.children) {
            if (child is Button) {
                Log.d("Child", child.toString())

                child.setOnClickListener { button ->
                    val tagToColor = mutableMapOf(
                        "beer" to R.color.beer,
                        "wine" to R.color.wine,
                        "cocktail" to R.color.cocktail,
                        "wineBottle" to R.color.wineBottle,
                        "whiskey" to R.color.whiskey,
                        "coffee" to R.color.coffee
                    )

                    val tagToIcon = mutableMapOf(
                        "beer" to R.string.fa_icon_beer,
                        "wine" to R.string.fa_icon_wine,
                        "cocktail" to R.string.fa_icon_cocktail,
                        "wineBottle" to R.string.fa_icon_wineBottle,
                        "whiskey" to R.string.fa_icon_whiskey,
                        "coffee" to R.string.fa_icon_coffee
                    )

                    val unwrappedDrawable = AppCompatResources.getDrawable(get(), R.drawable.big_button_bg_round)
                    val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
                    DrawableCompat.setTintMode(unwrappedDrawable, PorterDuff.Mode.SRC)
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(get(), tagToColor[button.tag] ?: Color.RED) )
                    active_button.background = wrappedDrawable

                    //active_button.text = resources.getString(tagToIcon[button.tag] ?: R.string.fa_icon_beer)


                    //val packageName = (get() as Context).packageName
                    //val resId = resources.getIdentifier(aString, "string", packageName)
                    active_button.text = getString(R.string.fa_icon_coffee)

                    //it.mutate().colorFilter = PorterDuffColorFilter(view.background.color)
                    //it.mutate().colorFilter = view.background.colorFilter
                    //active_button.setBackgroundColor(resources.getColor(R.color.beer))

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