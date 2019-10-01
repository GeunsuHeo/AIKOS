package com.example.mycuk;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.CookieSpecBase;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class LIbActivity extends AppCompatActivity {
    WebView webView;
    WebSettings webViewSetting;
    Intent intent;
    public CookieManager cookieManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);
        trustAllHosts();

        new Thread(){
            public void run(){
                setupLogin();
            }
        }.start();
        try{
            Thread.sleep(1500);
        } catch (Exception ex){}

        Log.v("login", "2");
        intent = getIntent();
        String url = intent.getExtras().getString("url");
        webView = (WebView) findViewById(R.id.LibWebview); // 웹뷰 id
        webViewSetting = webView.getSettings();             // testWebview를 webViewSetting 에 선언 해준다.

        webViewSetting.setJavaScriptEnabled(true);    // 웹의 자바스크립트 허용
        webViewSetting.setLoadWithOverviewMode(true); // 웹 화면을 디바이스 화면에 맞게 셋팅
        webView.setWebViewClient(new WebViewClient(){}); // 내부 webview로 열기
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
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

    private void setupLogin() {
        final String TAG = "login";
        String domain = "http://library.catholic.ac.kr";
        CookieSyncManager.createInstance(this);
        cookieManager = CookieManager.getInstance();
        DefaultHttpClient httpClient = new DefaultHttpClient();
        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        String[] keyValueSets = CookieManager.getInstance().getCookie(domain).split(";");
        for(String cookie : keyValueSets){
            Log.v(TAG, "token transfer start------------------------");
            String[] keyValue = cookie.split("=");
            String key = keyValue[0];
            String value = "";
            if(keyValue .length > 1) value = keyValue [1];
            httpClient.getCookieStore().addCookie(new BasicClientCookie(key, value));
            Log.v(TAG, "key:"+key+";value:"+value);
        }
        Log.v(TAG,CookieManager.getInstance().getCookie(domain));
        try{
            HttpParams params = new BasicHttpParams();

            HttpPost post = new HttpPost(domain);
            CookieSpecBase cookieSpecBase = new BrowserCompatSpec();
            List<Cookie> cookies = httpClient.getCookieStore().getCookies();

            List<?> cookieHeader = cookieSpecBase.formatCookies(cookies);
            post.setHeader((Header) cookieHeader.get(0));

            post.setParams(params);
            HttpResponse response = null;
            BasicResponseHandler myHandler = new BasicResponseHandler();
            String endResult = null;

            try{
                response = httpClient.execute(post,localContext);
            } catch (ClientProtocolException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                endResult = myHandler.handleResponse(response);
            }catch (HttpResponseException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }

            Log.v(TAG,endResult);
            Log.v(TAG, "Post logon cookies:");
            if(cookies.isEmpty()){
                CookieSyncManager.createInstance(this);
                CookieManager cookieManager = CookieManager.getInstance();

                Cookie sessionInfo = null;
                for(Cookie cookie : cookies){
                    sessionInfo = cookie;
                    String cookieString = sessionInfo.getName() + "=" + sessionInfo.getValue()+"; path="+sessionInfo.getPath();
                    cookieManager.setCookie(sessionInfo.getDomain(), cookieString);
                    CookieSyncManager.getInstance().sync();

                }
            }
            Log.v(TAG, "1");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.v(TAG,"error!");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().startSync();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().stopSync();
        }
    }

    private void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
