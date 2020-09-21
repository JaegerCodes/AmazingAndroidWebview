package yanbal.com.pe.webviewyanbal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.DownloadListener
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_webview.*

class WebViewActivity : AppCompatActivity() {
    private val readStoragePermission = 11
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)
        requestPhonePermissions()
        setupWebview()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebview() {
        val webUrl = intent.getStringExtra("KEY_URL")
        progressBar.visibility = View.VISIBLE
        browser.loadUrl(webUrl)
        browser.settings.javaScriptEnabled = true
        browser.setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
            browser.loadUrl(JavaScriptInterface.getBase64StringFromBlobUrl(url))
        }
        browser.settings.setSupportZoom(true)
        browser.settings.setAppCachePath(applicationContext.cacheDir.absolutePath)
        browser.settings.cacheMode = WebSettings.LOAD_DEFAULT
        browser.settings.databaseEnabled = true
        browser.settings.domStorageEnabled = true
        browser.settings.useWideViewPort = true
        browser.settings.loadWithOverviewMode = true
        browser.addJavascriptInterface(JavaScriptInterface(applicationContext), "Android")
        browser.settings.pluginState = WebSettings.PluginState.ON

        browser.webViewClient = object : WebViewClient() {

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
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun requestPhonePermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                readStoragePermission)
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                readStoragePermission)
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                readStoragePermission)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && browser.canGoBack()) {
            browser.goBack()
            return true
        } else {
            finish()
        }
        return super.onKeyDown(keyCode, event)
    }
}
