package com.amoeba99.novelreader.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;

import com.amoeba99.novelreader.R;
import com.amoeba99.novelreader.adapter.MainPageAdapter;
import com.amoeba99.novelreader.model.Novel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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

public class MainActivity extends AppCompatActivity implements MainPageAdapter.OnItemClicked{

    @BindView(R.id.recyclerView)
    public RecyclerView list;

    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private MainPageAdapter adapter;
    private MenuItem login;
    private CallbackManager callbackManager;
    private CookieManager cm = CookieManager.getInstance();

    List<Novel> novel = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this, this);
        adapter = new MainPageAdapter(this);
        adapter.setOnClick(this);
        list.setLayoutManager(new GridLayoutManager(this, 2));
        list.setAdapter(adapter);
        callbackManager = CallbackManager.Factory.create();
        callBackLogin();
        refresh();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                refresh();
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

    public void callBackLogin(){
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            setMenu();
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

    public void refresh(){
        setData();
    }

    public void setData(){
        rootRef.child("novel").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                novel = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    novel.add(new Novel(data.getKey(), data.child("name").getValue().toString(), data.child("img").getValue().toString()));
                }
                adapter.setData(novel);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, NovelPageActivity.class);
        intent.putExtra("key", novel.get(position).getKey());
        startActivity(intent);
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
    }
}
