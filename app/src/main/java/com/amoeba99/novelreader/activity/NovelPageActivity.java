package com.amoeba99.novelreader.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.amoeba99.novelreader.R;
import com.amoeba99.novelreader.adapter.NovelPageAdapter;
import com.amoeba99.novelreader.model.Volume;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
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
    @BindView(R.id.spinnerScore)
    public Spinner scoreDropdown;
    @BindView(R.id.score)
    public TextView score;
    @BindView(R.id.loginLayout)
    public ConstraintLayout loginLayout;
    @BindView(R.id.share)
    public ShareButton shareButton;

    private WebView webViewPop;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private List<Volume> vol = new ArrayList<>();
    private NovelPageAdapter adapter;
    private CookieManager cm = CookieManager.getInstance();
    private ArrayAdapter<String> dropdownAdapter;
    private Double totalscore = 0.0;
    private Double count = 0.0;
    private MenuItem login;
    private int your_score = 0;
    private ShareLinkContent content;

    private static String APP_KEY = "1938649856401879";
    private static String BASE_DOMAIN = "http://www.catreturn.net";
    private static String PATH_URL;
    private static String TAG = "Tag";
    private static int NUMBER_OF_COMMENTS = 5;
    private static String[] scoreItem = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.novel_page);
        ButterKnife.bind(this, this);
        Intent intent = getIntent();
        key = intent.getStringExtra("key");
        PATH_URL = "/"+key;
        dropdownAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, scoreItem);
        scoreDropdown.setAdapter(dropdownAdapter);
        scoreDropdown.setPrompt("Score");
        adapter = new NovelPageAdapter(this);
        adapter.setOnClick(this);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
        callbackManager = CallbackManager.Factory.create();
        callBackLogin();
        refresh(this);
    }

    private FacebookCallback<Sharer.Result> callback = new FacebookCallback<Sharer.Result>() {
        @Override
        public void onSuccess(Sharer.Result result) {
            Log.v(TAG, "Successfully posted");
            // Write some code to do some operations when you shared content successfully.
        }

        @Override
        public void onCancel() {
            Log.v(TAG, "Sharing cancelled");
            // Write some code to do some operations when you cancel sharing content.
        }

        @Override
        public void onError(FacebookException error) {
            Log.v(TAG, error.getMessage());
            // Write some code to do some operations when some error occurs while sharing content.
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void callBackLogin(){
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        checkForVote();
                        setMenu();
                        yourScore();
                    }
                    @Override
                    public void onCancel() {
                        //checkLogin();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
    }


    public void voteScore(final int scoreVote){
        if(your_score != scoreVote) {
            if (your_score == 0 && scoreVote != 0) {
                totalscore += scoreVote;
                count += 1;
                rootRef.child("novel").child(key).child("totalscore").setValue(Double.toString(totalscore));
                rootRef.child("novel").child(key).child("count").setValue(Double.toString(count));
            } else if (scoreVote != 0) {
                totalscore = totalscore + (scoreVote-your_score);
                rootRef.child("novel").child(key).child("totalscore").setValue(Double.toString(totalscore));
            } else if (your_score != 0 && scoreVote == 0) {
                totalscore -= your_score;
                count -= 1;
                rootRef.child("novel").child(key).child("totalscore").setValue(Double.toString(totalscore));
                rootRef.child("novel").child(key).child("count").setValue(Double.toString(count));
            }
            rootRef.child("users").child(AccessToken.getCurrentAccessToken().getUserId()).child("vote").child(key).setValue(Integer.toString(scoreVote));
            your_score = scoreVote;
        }
        setScore();
    }

    public void setScore(){
        score.setText(String.format("Score : %.2f", totalscore/count));
    }

    public void scoreChanged() {
        scoreDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                voteScore(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    public void checkForVote(){
        if(AccessToken.getCurrentAccessToken() != null) {
            loginLayout.setVisibility(View.VISIBLE);
        }
        else {
            loginLayout.setVisibility(View.GONE);
        }
    }

    public void setData(final Context context){
        rootRef.child("novel").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imgUrl = dataSnapshot.child("img").getValue().toString();
                String describe = dataSnapshot.child("description").getValue().toString();
                name.setText(dataSnapshot.child("name").getValue().toString());
                description.setText(describe);
                totalscore = Double.parseDouble(dataSnapshot.child("totalscore").getValue().toString());
                count = Double.parseDouble(dataSnapshot.child("count").getValue().toString());
                Glide.with(context).load(imgUrl).into(img);
                content = new ShareLinkContent.Builder().setContentUrl(Uri.parse(imgUrl)).setImageUrl(Uri.parse(imgUrl)).setQuote(describe).build();
                shareButton.setShareContent(content);
                setScore();
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
    }

    public void refresh(final Context context){
        setData(context);
        checkForVote();
        loadComments();
        yourScore();
        scoreChanged();
    }


    public void yourScore(){
        if(isLogin()) {
            rootRef.child("users").child(AccessToken.getCurrentAccessToken().getUserId()).child("vote").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null)
                        your_score = Integer.parseInt(dataSnapshot.getValue().toString());
                    else
                        your_score = 0;
                    Log.e(TAG, Integer.toString(your_score));
                    scoreDropdown.setSelection(your_score);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
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
            if (url.contains("/plugins/close_popup.php?reload")) {
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        container.removeView(webViewPop);
                        loadComments();
                    }
                }, 600);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                       SslError error) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        login = menu.findItem(R.id.log);
        setMenu();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.log:
                if(isLogin())
                    logout();
                else
                    login();
                checkForVote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void logout(){
        LoginManager.getInstance().logOut();
        login.setTitle("Login");
        cm.removeAllCookie();
    }

    public void login(){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
    }

    public boolean isLogin(){
        if(AccessToken.getCurrentAccessToken() == null)
            return false;
        else
            return true;
    }

    public void setMenu(){
        if(isLogin())
            login.setTitle("Logout");
        else
            login.setTitle("Login");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setMenu();
        checkForVote();
        yourScore();
    }

}
