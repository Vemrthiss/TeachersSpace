package com.teachersspace.parent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.teachersspace.R;

public class ParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        // TODO: request permission for microphone
    }
}