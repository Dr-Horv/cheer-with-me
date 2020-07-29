package dev.fredag.cheerwithme

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.TextView
import android.view.ViewGroup

object FontManager {

    val ROOT = "font/"
    val FONTAWESOME_REGULAR = ROOT + "Font Awesome 5 Pro-Regular-400.otf"
    val FONTAWESOME_SOLID = ROOT + "Font Awesome 5 Pro-Solid-900.otf"


    fun getTypeface(context: Context, font: String): Typeface {
        return Typeface.createFromAsset(context.assets, font)
    }

    fun markAsIconContainer(v: View, typeface: Typeface) {
        if (v is ViewGroup) {
            for (i in 0 until v.childCount) {
                val child = v.getChildAt(i)
                markAsIconContainer(child, typeface)
            }
        } else if (v is TextView) {
            v.typeface = typeface
        }
    }

}