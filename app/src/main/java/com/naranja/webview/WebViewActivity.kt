package com.naranja.webview

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.naranja.webview.databinding.ActivityWebviewBinding


class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebviewBinding
    private val readStoragePermission = 11

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestPhonePermissions()
        setupWebview()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebview() {
        val webUrl = intent.getStringExtra("KEY_URL")?:""
        binding.progressBar.visibility = View.VISIBLE
        binding.browser.loadUrl(webUrl)
        binding.browser.settings.javaScriptEnabled = true
        binding.browser.setDownloadListener { url, _, _, mimeType, _ ->
            Log.e("MIME TYPE", mimeType.toString())
            binding.browser.loadUrl(JavaScriptInterface.getBase64StringFromBlobUrl(url))
        }
        binding.browser.settings.setSupportZoom(true)
        binding.browser.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        //browser.clearCache(true)
        this.deleteDatabase("webview.db");
        this.deleteDatabase("webviewCache.db")
        binding.browser.settings.databaseEnabled = true
        binding.browser.settings.domStorageEnabled = true
        binding.browser.settings.useWideViewPort = true
        binding.browser.settings.loadWithOverviewMode = true
        //browser.settings.setAppCachePath(applicationContext.cacheDir.absolutePath)
        binding.browser.addJavascriptInterface(JavaScriptInterface(applicationContext), "Android")
        // browser.settings.pluginState = WebSettings.PluginState.ON

        binding.browser.webViewClient = object : WebViewClient() {

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

                return if (url.startsWith("tel:") || url.startsWith("mailto:")) {
                    view.context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    )
                    true
                } else {
                    view.loadUrl(url)
                    true
                }
            }

            override fun onLoadResource(view: WebView, url: String) {
            }

            override fun onPageFinished(view: WebView, url: String) {
                Log.e("URL onPageFinished", url)
                binding.progressBar.visibility = View.GONE
            }

            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                handler.proceed() // Ignore SSL certificate errors
            }
        }
    }

    private fun requestPhonePermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                readStoragePermission
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                readStoragePermission
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            )
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                readStoragePermission
            )
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.browser.canGoBack()) {
            binding.browser.goBack()
            return true
        } else {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}
