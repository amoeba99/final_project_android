package com.amoeba99.novelreader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.amoeba99.novelreader.R;
import com.amoeba99.novelreader.adapter.MainPageAdapter;
import com.amoeba99.novelreader.model.Novel;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainPageAdapter.OnItemClicked {
    CallbackManager callbackManager;

    @BindView(R.id.recyclerView)
    public RecyclerView list;

    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference userRef = rootRef.child("users");
    private String name;
    private MainPageAdapter adapter;

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
        CookieSyncManager.createInstance(this);
        CookieManager cm = CookieManager.getInstance();
        cm.removeAllCookie();
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            /*case R.id.android:
                user = "android";
                refresh(user, mode);
                return true;
            case R.id.nature:
                user = "nature";
                refresh(user, mode);
                return true;
            case R.id.cartoon:
                user = "cartoon";
                refresh(user, mode);
                return true;
            case R.id.switch_mode:
                if(mode.equals("grid")){
                    mode = "list";
                    postAdapter.setLayout(R.layout.item_list);
                }else {
                    mode = "grid";
                    postAdapter.setLayout(R.layout.item_grid);
                }
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    public void refresh(){
        if(AccessToken.getCurrentAccessToken() != null){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            name = object.getString("name");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            //  e.printStackTrace();
                        }

                    }

                });

        request.executeAsync();}
        rootRef.child("novel").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
}
