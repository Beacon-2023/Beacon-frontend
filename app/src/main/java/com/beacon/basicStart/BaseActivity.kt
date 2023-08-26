package com.beacon.basicStart

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.*

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLocale()
    }

    fun setAppLocale() {
        val sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "")
        if (!language.isNullOrBlank()) {
            val locale = Locale(language)
            Locale.setDefault(locale)

            val config = Configuration()
            config.setLocale(locale)

            resources.updateConfiguration(config, resources.displayMetrics)
        }
    }
}
