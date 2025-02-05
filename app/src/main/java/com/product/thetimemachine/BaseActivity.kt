package com.product.thetimemachine

import android.content.Context
import androidx.activity.ComponentActivity

abstract class BaseActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        val language = LanguageManager.getLanguage(newBase)
        val context = LanguageManager.setLocale(newBase, language)
        super.attachBaseContext(context)
    }
}