package com.amoeba99.novelreader;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.amoeba99.novelreader.model.Novel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Created by Amoeba on 11/30/2017.
 */

@RunWith(AndroidJUnit4.class)
public class FirebaseTest {
    private DatabaseReference rootRef;

    @Before
    public void setUp(){
        rootRef = FirebaseDatabase.getInstance().getReference();
    }

    @Test
    public void getKeyNovel() throws Exception {
        assertEquals("Kusuriya no Hitoritogo", rootRef.child("novel").child("Kusuriya no Hitoritogo").getKey());
        assertEquals("Lsdjixzhs", rootRef.child("novel").child("Lsdjixzhs").getKey());
    }
}
