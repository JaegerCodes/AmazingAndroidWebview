package com.naranja.webview

import android.content.ComponentName
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.Browser
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.browser.customtabs.CustomTabsSessionToken
import androidx.core.content.ContextCompat
import com.naranja.webview.databinding.ActivityMainBinding
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var customTabsSession: CustomTabsSession? = null
    private var url = "http://192.168.1.47:3000/get-cookies"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        val connection = object : CustomTabsServiceConnection() {
            override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
                val intentBuilder = CustomTabsIntent.Builder(customTabsSession)
                intentBuilder.setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
                    .setToolbarColor(ContextCompat.getColor(this@MainActivity, R.color.colorPrimary))
                    .build())
                intentBuilder.setShowTitle(true)
                intentBuilder.setShareState(CustomTabsIntent.SHARE_STATE_ON)


                val customTabsIntent = intentBuilder.build()

                val headers = Bundle().apply {
                    putString("Authorization", "Bearer asdasd")
                    putString("redirect-url", "Some redirect url")
                }
                customTabsIntent.intent.putExtra("android.intent.extra.REFERRER", Uri.parse("your_referrer_here"))
                customTabsIntent.intent.putExtra("android.intent.extra.HEADERS", headers)

                customTabsIntent.launchUrl(this@MainActivity, Uri.parse(url))
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                customTabsSession = null
            }
        }


        binding.btnTabIntent.setOnClickListener {
            CustomTabsClient.bindCustomTabsService(
                this,
                CustomTabsClient.getPackageName(this, null),
                connection
            )
        }

        binding.btnWebview.setOnClickListener {
            val url = binding.etWebUrl.text.toString()
            if (validateUrl(url)) {
                val intent = Intent(this, WebViewActivity::class.java).putExtra("KEY_URL", url)
                startActivity(intent)
            }
        }

        binding.btnOpenChrome.setOnClickListener {
            val url = binding.etWebUrl.text.toString()
            url.asUri()?.openInBrowser(this)
        }
    }

    private fun validateUrl(url: String): Boolean {
        val validationResult = url.isNotEmpty() && url.isValidUrl()
        if (!validationResult)
            Toast.makeText(baseContext, "Please prove a valid URL", Toast.LENGTH_SHORT).show()
        return validationResult
    }
}
