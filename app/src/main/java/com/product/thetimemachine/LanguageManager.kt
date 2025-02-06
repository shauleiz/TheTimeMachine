package com.product.thetimemachine

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.product.thetimemachine.ui.getPrefLanguage
import java.util.IllformedLocaleException
import java.util.Locale

object LanguageManager {

    fun getLanguage(context: Context): String {
        val language = getPrefLanguage(context)
        return ""//language
    }

    fun setLocale(context: Context, language: String): Context {

        if (language.isBlank())
            return context

        val locale = try {
            Locale.Builder().setLanguage("").build()
        } catch (e: IllformedLocaleException){
            Locale.getDefault()
        }

        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
