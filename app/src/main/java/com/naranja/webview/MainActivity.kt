package com.naranja.webview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.naranja.webview.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.btnTabIntent.setOnClickListener {
            val url = binding.etWebUrl.text.toString()
            if (validateUrl(url)) {
                val builder = CustomTabsIntent.Builder()
                builder.setToolbarColor(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.colorPrimaryDark
                    )
                )

                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this, Uri.parse(url))
            }
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