package com.tororobot.bluetooth.bluetooh2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tororobot.R;
import com.tororobot.bluetooth.DevicesFragment;

public class Bluetooh2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooh2);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, DevicesFragment.newInstance())
                    .commit();
        }
    }
}
