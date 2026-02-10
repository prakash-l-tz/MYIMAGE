package com.example.myimage

import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class GoogleActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google)

        webView = findViewById(R.id.webView)

        // WebView settings
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Open links inside WebView
        webView.webViewClient = WebViewClient()

        // Load Google (or any URL you want)
        webView.loadUrl("https://www.google.com")
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
