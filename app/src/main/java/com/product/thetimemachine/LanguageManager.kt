package com.product.thetimemachine

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import java.util.Locale

object LanguageManager {
    private const val LANGUAGE_KEY = "language_key"
    private const val DEFAULT_LANGUAGE = "en"

    fun saveLanguage(context: Context, language: String) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(LANGUAGE_KEY, language).apply()
    }

    fun getLanguage(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val language = sharedPreferences.getString(LANGUAGE_KEY, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        Log.i("MULTI_LANG", "getLanguage(): language = $language")

        //return language
        return "fr"
    }

    fun setLocale(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
