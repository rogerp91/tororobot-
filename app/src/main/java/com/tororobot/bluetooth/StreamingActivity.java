package com.tororobot.bluetooth;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tororobot.R;

import butterknife.ButterKnife;

public class StreamingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);
        ButterKnife.bind(this);
    }
}