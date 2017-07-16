package com.guagua.reportgo;

import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MapActivity extends AppCompatActivity {
    WebView mWebView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mWebView = (WebView)findViewById(R.id.webview);

        mWebView.setWebViewClient(mWebViewClient);


        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//              super.onReceivedSslError(view, handler, error);
                handler.proceed();
//              handleMessage(Message msg);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl("https://www.google.com/maps/d/embed?mid=1Tcx7p18HOe_smYKrF6lxLJclwXk");
        }

    WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    };
}
