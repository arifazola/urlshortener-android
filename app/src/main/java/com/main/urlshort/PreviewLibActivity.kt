package com.main.urlshort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class PreviewLibActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_UrlShort_NoActionBar)
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        val intent = intent
        val property = intent.getStringExtra("PROPERTY")
        setContentView(webView)
        webView.loadUrl("https://shrlnk.my.id/${property}")
        webView.settings.javaScriptEnabled = true
    }
}