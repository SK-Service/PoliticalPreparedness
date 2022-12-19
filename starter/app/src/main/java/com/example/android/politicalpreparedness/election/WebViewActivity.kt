package com.example.android.politicalpreparedness.election;

import android.app.Activity;
import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.android.politicalpreparedness.R

class WebViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val intent = getIntent()
        val textUrl = intent.getStringExtra("URL")

        Log.i("WebViewActivity", "URL to open: ${textUrl}")
        // Find the WebView by its unique ID
        val webView: WebView = findViewById(R.id.webview_voterinfo)
        webView.webViewClient = WebViewClient()
        webView.settings.javaScriptEnabled = true
        webView.settings.setSupportZoom(true)

            webView.loadUrl(textUrl!!)
    }
}
