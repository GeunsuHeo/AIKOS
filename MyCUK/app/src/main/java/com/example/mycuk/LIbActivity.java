package com.example.mycuk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LIbActivity extends AppCompatActivity {
    WebView webView;
    WebSettings webViewSetting;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);
        intent = getIntent();
        String url = intent.getExtras().getString("url");
        webView = (WebView) findViewById(R.id.LibWebview); // 웹뷰 id
        webViewSetting = webView.getSettings();             // testWebview를 webViewSetting 에 선언 해준다.

        webViewSetting.setJavaScriptEnabled(true);    // 웹의 자바스크립트 허용
        webViewSetting.setLoadWithOverviewMode(true); // 웹 화면을 디바이스 화면에 맞게 셋팅
        webView.setWebViewClient(new WebViewClient(){}); // 내부 webview로 열기

        webView.loadUrl(url);  // Load할 url 주소
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }
}
