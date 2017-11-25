package com.amoeba99.novelreader.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.amoeba99.novelreader.R;
import com.amoeba99.novelreader.adapter.NovelPageAdapter;
import com.amoeba99.novelreader.model.Volume;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NovelPageActivity extends AppCompatActivity implements NovelPageAdapter.OnItemClicked{

    private String key;
    @BindView(R.id.name)
    public TextView name;
    @BindView(R.id.description)
    public TextView description;
    @BindView(R.id.image)
    public ImageView img;
    @BindView(R.id.recyclerView)
    public RecyclerView list;
    @BindView(R.id.webView)
    public WebView webView;
    @BindView(R.id.container)
    public FrameLayout container;


    private WebView webViewPop;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private List<Volume> vol = new ArrayList<>();
    private NovelPageAdapter adapter;
    private CookieManager cm;

    private static String APP_KEY = "You're app key";
    private static String BASE_DOMAIN = "http://www.catreturn.net";
    private static String PATH_URL = "/novel/4799";
    private static int NUMBER_OF_COMMENTS = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novel_page);
        ButterKnife.bind(this, this);
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        adapter = new NovelPageAdapter(this);
        adapter.setOnClick(this);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        ///CookieSyncManager.createInstance(this);
        cm = CookieManager.getInstance();
        //cm.removeAllCookie();
        refresh(this);
    }

    public void refresh(final Context context){
        rootRef.child("novel").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imgUrl = dataSnapshot.child("img").getValue().toString();
                name.setText(dataSnapshot.child("name").getValue().toString());
                description.setText(dataSnapshot.child("description").getValue().toString());
                Glide.with(context).load(imgUrl).into(img);
                for(DataSnapshot data : dataSnapshot.child("volume").getChildren()){
                    vol.add(new Volume(data.child("title").getValue().toString(), data.child("content").getValue().toString()));
                }
                adapter.setData(vol);
                adapter.setImgUrl(imgUrl);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        loadComments();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, VolumePageActivity.class);
        intent.putExtra("vol", vol.get(position));
        startActivity(intent);
    }

    public void loadComments(){
        webView.setWebViewClient(new UriWebViewClient());
        webView.setWebChromeClient(new UriChromeClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);
        cm.setAcceptCookie(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        cm.setAcceptThirdPartyCookies(webView, true);
        webView.loadDataWithBaseURL(BASE_DOMAIN,
                "<html><head></head><body><div id=\"fb-root\"></div><div id=\"fb-root\"></div><script>(function(d, s, id) {var js, fjs = d.getElementsByTagName(s)[0];if (d.getElementById(id)) return;js = d.createElement(s); js.id = id;js.src = \"http://connect.facebook.net/en_US/all.js#xfbml=1&appId="+APP_KEY+ "\";fjs.parentNode.insertBefore(js, fjs);}(document, 'script', 'facebook-jssdk'));</script><div class=\"fb-comments\" data-href=\""
                        +BASE_DOMAIN+PATH_URL+"\" data-numposts=\""+NUMBER_OF_COMMENTS+"\" data-width=\"470\"></div> </body></html>", "text/html", null, null);
    }

    private class UriWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            String host = Uri.parse(url).getHost();

            return !host.equals("m.facebook.com");

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String host = Uri.parse(url).getHost();
            //setLoading(false);
            if (url.contains("/plugins/close_popup.php?reload")) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100ms
                        container.removeView(webViewPop);
                        loadComments();
                    }
                }, 600);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
            //setLoading(false);
        }
    }

    class UriChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            webViewPop = new WebView(getApplicationContext());
            webViewPop.setVerticalScrollBarEnabled(false);
            webViewPop.setHorizontalScrollBarEnabled(false);
            webViewPop.setWebViewClient(new UriWebViewClient());
            webViewPop.setWebChromeClient(this);
            webViewPop.getSettings().setJavaScriptEnabled(true);
            webViewPop.getSettings().setDomStorageEnabled(true);
            webViewPop.getSettings().setSupportZoom(false);
            webViewPop.getSettings().setBuiltInZoomControls(false);
            webViewPop.getSettings().setSupportMultipleWindows(true);
            webViewPop.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            container.addView(webViewPop);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(webViewPop);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
        }
    }

}
