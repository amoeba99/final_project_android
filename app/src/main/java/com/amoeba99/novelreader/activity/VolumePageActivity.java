package com.amoeba99.novelreader.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.amoeba99.novelreader.R;
import com.amoeba99.novelreader.model.Volume;

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
}
