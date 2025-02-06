package com.product.thetimemachine

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import com.product.thetimemachine.ui.PreferencesKeys
import com.product.thetimemachine.ui.getPrefLanguage
import com.product.thetimemachine.ui.updatePref
import java.util.IllformedLocaleException
import java.util.Locale

object LanguageManager {

    // Get the language from Properties: en OR iw
    fun getLanguage(context: Context): String {
        val language = getPrefLanguage(context)
        return language
    }

    // Set the language in Properties
    private fun setLanguage(context: Context, language: String){
        updatePref(context, PreferencesKeys.KEY_LANGUAGE, language)
    }

    // Update context to fit the Locale required by language
    fun setLocale(context: Context, language: String): Context {

        // If language is blank - set Properties to system language and make no changes to context
        if (language.isBlank()) {
            setLanguage(context,  Locale.getDefault().language)
            return context
        }

        // Set locale from the language - if error: use the system language
        val locale = try {
            Locale.Builder().setLanguage(language).build()
        } catch (e: IllformedLocaleException){
            Locale.getDefault()
        }

        // Set the default local and modify context
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }
}
