package com.main.urlshort

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView

class PreviewLibActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_UrlShort_NoActionBar)
        super.onCreate(savedInstanceState)
        val webView = WebView(this)
        setContentView(webView)
        webView.loadUrl("http://192.168.1.8:8080/landingpage3")
        webView.settings.javaScriptEnabled = true
    }
}