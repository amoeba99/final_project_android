package com.amoeba99.novelreader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.widget.TextView;
import com.amoeba99.novelreader.R;
import com.amoeba99.novelreader.model.Volume;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Amoeba on 11/23/2017.
 */

public class VolumePageActivity extends AppCompatActivity {

    @BindView(R.id.content)
    public TextView content;
    @BindView(R.id.title)
    public TextView title;

    private Volume vol;
    private MenuItem login;
    private CookieManager cm = CookieManager.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volume_content_page);
        ButterKnife.bind(this, this);
        Intent intent = getIntent();
        vol = intent.getParcelableExtra("vol");
        refresh(this);
    }

    public void refresh(final Context context){
        content.setText(vol.getContent());
        title.setText(vol.getTitle());
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
        login.setTitle("Logout");
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

}
