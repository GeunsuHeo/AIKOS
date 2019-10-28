package com.example.mycuk;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public TabHost tabHost;

    private String preUrl,url,postUrl,midUrl,baseUrl,sectionNum,pageNum,searchValue;
    private RecyclerView recyclerView;
    private MyAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    List<String> myDataset, myUrl;
    Button button1,button2,button3,button4, buttonPrevious, buttonNext, buttonLib1, buttonLib2, buttonLib3, buttonLib4, buttonSp1, buttonSp2, buttonSp3, buttonSp4;
    ImageButton buttonLibSearch;
    ImageView buttonSearch,buttonBell;
    TextView text_page;
    EditText editTextSearch, editTextLibSearch;
    int colorNotAcitve, colorAcitve;
    InputMethodManager imm;

    private WebView webView_schedule;

    public static HttpClient httpClient = new DefaultHttpClient();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.INTERNET},0);
            }
        }

        new Thread(){
            public void run(){
                trustAllHosts();
                login();
            }
        }.start();

        tabHostSetup();
        setupNoticeTab();
        setupSchedule();
        setupMain();
        setupLibrary();
        setupSpace();
    }



    private void setupMain() {

    }

    private void setupSchedule() {
        webView_schedule = (WebView) findViewById(R.id.webView_schedule);
        webView_schedule.setWebViewClient(new WebViewClient());
        webView_schedule.getSettings().setJavaScriptEnabled(true);
        webView_schedule.getSettings().setLoadWithOverviewMode(true);
        webView_schedule.getSettings().setUseWideViewPort(true);
        webView_schedule.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView_schedule.setScrollbarFadingEnabled(true);

        webView_schedule.loadUrl("https://haksa.catholic.ac.kr/haksa/schedule01_8.html");
    }

    private void setupNoticeTab() {
        colorAcitve = 0xffffffff;
        colorNotAcitve = ContextCompat.getColor(this, R.color.colorPrimary);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        sectionNum = "1";
        midUrl = "1";
        pageNum = "1";
        baseUrl = "https://www.catholic.ac.kr";
        preUrl = baseUrl+"/front/boardlist.do?cmsDirPkid=2053&cmsLocalPkid=";
        midUrl = "&searchField=ALL&currentPage=";
        postUrl = "&searchLowItem=ALL&searchValue=";
        searchValue="";
        url = preUrl+sectionNum+midUrl+pageNum+postUrl+searchValue;

        myDataset = new ArrayList<>();
        myUrl = new ArrayList<>();
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset, myUrl);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new MyltemDecoration());

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button1.setBackgroundColor(colorNotAcitve);
        button2.setBackgroundColor(colorNotAcitve);
        button3.setBackgroundColor(colorNotAcitve);
        button4.setBackgroundColor(colorNotAcitve);
        buttonBell = (ImageView) findViewById(R.id.button_bell);
        buttonBell.setOnClickListener(this);
        buttonPrevious = (Button) findViewById(R.id.button_previous);
        buttonNext = (Button) findViewById(R.id.button_next);
        buttonPrevious.setOnClickListener(this);
        buttonNext.setOnClickListener(this);

        text_page = (TextView) findViewById(R.id.text_page);

        buttonSearch = (ImageView) findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(this);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        editTextSearch = (EditText) findViewById(R.id.editText_search);

    }

    private void setupLibrary() {
        buttonLibSearch = (ImageButton) findViewById(R.id.lib_search_btn);
        buttonLib1 = (Button) findViewById(R.id.lib_btn1);
        buttonLib2 = (Button) findViewById(R.id.lib_btn2);
        buttonLib3 = (Button) findViewById(R.id.lib_btn3);
        buttonLib4 = (Button) findViewById(R.id.lib_btn4);

        buttonLibSearch.setOnClickListener(this);
        buttonLib1.setOnClickListener(this);
        buttonLib2.setOnClickListener(this);
        buttonLib3.setOnClickListener(this);
        buttonLib4.setOnClickListener(this);

        editTextLibSearch = (EditText) findViewById(R.id.editText_libSearch);
    }
    private void setupSpace() {
        buttonSp1 = (Button) findViewById(R.id.sp_btn1);
        buttonSp2 = (Button) findViewById(R.id.sp_btn2);
        buttonSp3 = (Button) findViewById(R.id.sp_btn3);
        buttonSp4 = (Button) findViewById(R.id.sp_btn4);


        buttonSp1.setOnClickListener(this);
        buttonSp2.setOnClickListener(this);
        buttonSp3.setOnClickListener(this);
        buttonSp4.setOnClickListener(this);

    }

    private void tabHostSetup() {
        tabHost = (TabHost) findViewById(R.id.tabhost);
        tabHost.setup();

        TabHost.TabSpec ts1 = tabHost.newTabSpec("tab_notice");
        ts1.setIndicator(createNewTabView("학교공지"));
        ts1.setContent(R.id.tab_notice);
        tabHost.addTab(ts1);

        TabHost.TabSpec ts2 = tabHost.newTabSpec("tab_schedule");
        ts2.setIndicator(createNewTabView("학교일정"));
        ts2.setContent(R.id.tab_schedule);
        tabHost.addTab(ts2);

        TabHost.TabSpec ts3 = tabHost.newTabSpec("tab_home");
        ts3.setIndicator(createNewTabView(ContextCompat.getDrawable(this,R.drawable.cat_home)));
        ts3.setContent(R.id.tab_home);
        tabHost.addTab(ts3);

        TabHost.TabSpec ts4 = tabHost.newTabSpec("tab_space");
        ts4.setIndicator(createNewTabView("공간대여"));
        ts4.setContent(R.id.tab_space);
        tabHost.addTab(ts4);

        TabHost.TabSpec ts5 = tabHost.newTabSpec("tab_lib");
        ts5.setIndicator(createNewTabView("가대중도"));
        ts5.setContent(R.id.tab_lib);
        tabHost.addTab(ts5);

        tabHost.setCurrentTab(2);
    }

    public TextView createNewTabView(String text){
        TextView tx = new TextView(this);
        tx.setText(text);
        tx.setTextSize(15);
        tx.setTextColor(0xffffffff);
        tx.setTypeface(ResourcesCompat.getFont(this, R.font.main_font));
        tx.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        tx.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
        return tx;
    }
    public LinearLayout createNewTabView(Drawable drawable){
        LinearLayout linearLayout = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView imageView = new ImageView(this);
        linearLayout.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
        linearLayout.addView(imageView);
        imageView.setImageDrawable(drawable);
        layoutParams.setMargins(0,10,0,10);
        imageView.setLayoutParams(layoutParams);
        return linearLayout;
    }

    @Override
    public void onClick(View v) {
        if(v == button1){
            sectionNum = "1";
            pageNum = "1";
            searchValue = "";
            button1.setBackgroundColor(colorAcitve);
            button2.setBackgroundColor(colorNotAcitve);
            button3.setBackgroundColor(colorNotAcitve);
            button4.setBackgroundColor(colorNotAcitve);
            button1.setTextColor(0xff000000);
            button2.setTextColor(0xffffffff);
            button3.setTextColor(0xffffffff);
            button4.setTextColor(0xffffffff);
        } else if(v == button2){
            sectionNum = "2";
            pageNum = "1";
            button1.setBackgroundColor(colorNotAcitve);
            button2.setBackgroundColor(colorAcitve);
            button3.setBackgroundColor(colorNotAcitve);
            button4.setBackgroundColor(colorNotAcitve);
            button1.setTextColor(0xffffffff);
            button2.setTextColor(0xff000000);
            button3.setTextColor(0xffffffff);
            button4.setTextColor(0xffffffff);
        } else if(v == button3){
            sectionNum = "3";
            pageNum = "1";
            button1.setBackgroundColor(colorNotAcitve);
            button2.setBackgroundColor(colorNotAcitve);
            button3.setBackgroundColor(colorAcitve);
            button4.setBackgroundColor(colorNotAcitve);
            button1.setTextColor(0xffffffff);
            button2.setTextColor(0xffffffff);
            button3.setTextColor(0xff000000);
            button4.setTextColor(0xffffffff);
        } else if(v == button4){
            sectionNum = "4";
            pageNum = "1";
            button1.setBackgroundColor(colorNotAcitve);
            button2.setBackgroundColor(colorNotAcitve);
            button3.setBackgroundColor(colorNotAcitve);
            button4.setBackgroundColor(colorAcitve);
            button1.setTextColor(0xffffffff);
            button2.setTextColor(0xffffffff);
            button3.setTextColor(0xffffffff);
            button4.setTextColor(0xff000000);
        } else if(v == buttonPrevious){
            if(Integer.parseInt(pageNum)<=1)
                Toast.makeText(getApplicationContext(),"이전 장이 없습니다.",Toast.LENGTH_LONG).show();
            else{
                pageNum = Integer.toString(Integer.parseInt(pageNum)-1);
                //text_page.setText(pageNum);
            }
        } else if(v == buttonNext){
            if(Integer.parseInt(pageNum)>=10)
                Toast.makeText(getApplicationContext(),"10장 이후는 지원하지 않습니다 인터넷을 사용해주세요.",Toast.LENGTH_LONG).show();
            else{
                pageNum = Integer.toString(Integer.parseInt(pageNum)+1);
                //text_page.setText(pageNum);
            }
        } else if(v == buttonSearch){
            searchValue = editTextSearch.getText().toString();
        } else if(v == buttonBell) {
            // 알림기능 추가
        } else if(v == buttonLibSearch){
            Intent intent = new Intent(getApplicationContext(), LIbActivity.class);
            String text = editTextLibSearch.getText().toString() != "" ? editTextLibSearch.getText().toString() : "";
            String url = "http://cuk-songsim-primo.hosted.exlibrisgroup.com/primo_library/libweb/action/search.do;jsessionid=88FF20581947A0375D8A6DB3C0420925?fn=search&ct=search&initialSearch=true&mode=Basic&tab=default_tab&indx=1&dum=true&srt=rank&vid=82KST-CUK_SONGSIM&frbg=&vl%28freeText0%29="
                    + text + "&scp.scps=scope%3A%2882KST-CUK_SONGSIM_SFX%29%2Cscope%3A%2882KST-CUK_SONGSIM_LOCAL%29%2Cprimo_central_multiple_fe";
            intent.putExtra("url", url); /*송신*/
            startActivity(intent);
        } else if(v == buttonLib1){
            Intent intent = new Intent(getApplicationContext(), LIbActivity.class);
            String url = "http://library.catholic.ac.kr/myloan/list";
            intent.putExtra("url", url); /*송신*/
            startActivity(intent);
        } else if(v == buttonLib2){
            Intent intent = new Intent(getApplicationContext(), LIbActivity.class);
            String url = "http://203.229.203.240/8080/Domian5.asp";
            intent.putExtra("url", url); /*송신*/
            startActivity(intent);
        } else if(v == buttonLib3){
            Intent intent = new Intent(getApplicationContext(), LIbActivity.class);
            String url = "http://library.catholic.ac.kr/purchaserequest/write";
            intent.putExtra("url", url); /*송신*/
            startActivity(intent);
        } else if(v == buttonLib4){
            Intent intent = new Intent(getApplicationContext(), LIbActivity.class);
            String url = "http://library.catholic.ac.kr/myreserve/list";
            intent.putExtra("url", url); /*송신*/
            startActivity(intent);
        } else if(v == buttonSp1){
            Intent intent = new Intent(getApplicationContext(), SpaceActivity.class);
            String url = "http://www.catholic.ac.kr/front/classroomreglist.do";
            intent.putExtra("url", url); /*송신*/
            startActivity(intent);
        } else if(v == buttonSp2) {
            Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:0221644164"));
            try {
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        text_page.setText(pageNum);

        imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);
        //editTextSearch.setText("");
        url = preUrl+sectionNum+midUrl+pageNum+postUrl+searchValue;

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(url).get();
                Elements titles= doc.select("div.rbbs_list_normal_sec div.text");
                Elements urls= doc.select("div.rbbs_list_normal_sec > ul > li > a");
                myDataset = new ArrayList<>();
                myUrl = new ArrayList<>();
                for(int i=0;i<titles.size();i++){
                    myDataset.add(titles.get(i).text().trim());
                    myUrl.add(urls.eachAttr("href").get(i).trim());
                }
                //System.out.println("end:"+myDataset);
                //System.out.println("url:"+myUrl);
            } catch (Exception e) {
                Log.e("sys","coudnt get the html");
                Log.e("sys", e.getMessage());
                //Toast.makeText(getApplicationContext(),"정보를 가져오는데 오류가 발생하였습니다.",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mAdapter.updateList(myDataset, myUrl);
        }


    }

    private void login() { // 사캠, 트리니티, 도서관, 학교 홈페이지 쿠키 받기
        final String TAG = "login";
        String domain[] = {"http://library.catholic.ac.kr","http://e-cyber.catholic.ac.kr","https://uportal.catholic.ac.kr","https://www.catholic.ac.kr"};
        String login[] = {"/login", "/ilos/lo/login.acl", "/sso/jsp/sso/ip/login_form.jsp", "/loginprocess.do"};
        String idName[] = {"id", "usr_id", "userId", "userId"};
        String pwdName[] = {"password", "usr_pwd","password", "userPw"};

        Log.v(TAG, "login");
        for(int i =0;i<4;i++) {
            Log.v(TAG, "token transfer start -------------------" + i);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(i==0 ? 3 : 2);
                nameValuePairs.add(new BasicNameValuePair(idName[i], URLDecoder.decode("whdauddbs", "UTF-8")));
                nameValuePairs.add(new BasicNameValuePair(pwdName[i], URLDecoder.decode("wjd2011", "UTF-8")));
                if(i==0) nameValuePairs.add(new BasicNameValuePair("loginType", URLDecoder.decode("1", "UTF-8")));

                HttpParams params = new BasicHttpParams();

                HttpPost post = new HttpPost(domain[i] + login[i]);
                post.setParams(params);
                HttpResponse response = null;
                BasicResponseHandler myHandler = new BasicResponseHandler();
                String endResult = null;

                try {
                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                try {
                    response = httpClient.execute(post);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v(TAG, response.getHeaders("set-cookie").toString());

                try {
                    endResult = myHandler.handleResponse(response);
                } catch (HttpResponseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v(TAG, endResult);
                Log.v(TAG, "Post logon cookies");

                List<Cookie> cookies = ((DefaultHttpClient) httpClient).getCookieStore().getCookies();

                Log.e("cookie", "setSyncCookie start");
                if (!cookies.isEmpty()) {
                    CookieManager cookieManager = CookieManager.getInstance();
                    CookieSyncManager.createInstance(getApplicationContext());
                    cookieManager.setAcceptCookie(true);
                    for (int j = 0; j < cookies.size(); j++) {
                        // cookie = cookies.get(i);
                        String cookieString = cookies.get(j).getName() + "=" + cookies.get(j).getValue();

                        cookieManager.setCookie(domain[i], cookieString);
                        cookieManager.setAcceptCookie(true);
                        Log.e("cookie test : ", cookieString);
                    }
                }
                Thread.sleep(500);
            } catch (Exception ex) {
                ex.printStackTrace();
                Log.v(TAG, "error!");
            }
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        CookieSyncManager.getInstance().stopSync();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        CookieSyncManager.getInstance().startSync();
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
