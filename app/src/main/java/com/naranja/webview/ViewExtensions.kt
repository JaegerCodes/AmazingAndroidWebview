package com.naranja.webview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Patterns
import androidx.core.content.ContextCompat

fun Uri?.openInBrowser(context: Context) {
    this ?: return
    val browserIntent = Intent(Intent.ACTION_VIEW, this)
    ContextCompat.startActivity(context, browserIntent, null)
}

fun String?.asUri(): Uri? {
    return try {
        return Uri.parse(this)
    } catch (e: Exception) {
        null
    }
}

fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()