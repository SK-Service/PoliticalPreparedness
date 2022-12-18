package com.example.android.politicalpreparedness.election;

import android.app.Activity;
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.politicalpreparedness.R

class WebViewActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        val textUrl = savedInstanceState?.getSerializable("URL")
        Log.i("WebViewActivity", "URL to open: ${textUrl}")
        // Find the WebView by its unique ID
        val webView: WebView = findViewById(R.id.webview)

// loading https://voterstatus.sos.ca.gov in the WebView.
        webView.loadUrl(textUrl.toString());
    }
}
