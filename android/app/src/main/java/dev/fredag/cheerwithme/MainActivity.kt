package dev.fredag.cheerwithme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.core.view.children

import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val iconFont = FontManager.getTypeface(applicationContext, FontManager.FONTAWESOME)
        FontManager.markAsIconContainer(icons_container, iconFont)

        for (child in icons_container.children) {
            if (child is Button) {
                child.setOnClickListener { println(child.tag) }
            }
        }
    }
}
