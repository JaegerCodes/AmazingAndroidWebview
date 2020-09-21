package yanbal.com.pe.webviewyanbal

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViews()
    }

    private fun setupViews() {
        btnTabIntent.setOnClickListener {
            val url = etWebUrl.text.toString()
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
        btnWebview.setOnClickListener {
            val url = etWebUrl.text.toString()
            if (validateUrl(url)) {
                val intent = Intent(this, WebViewActivity::class.java).putExtra("KEY_URL", url)
                startActivity(intent)
            }
        }
    }

    private fun validateUrl(url: String): Boolean {
        val validationResult = !url.isEmpty() && url.isValidUrl()
        if (!validationResult)
            Toast.makeText(baseContext, "Please prove a valid URL", Toast.LENGTH_SHORT).show()
        return validationResult
    }

    private fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()
}
