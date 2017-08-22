package com.tororobot.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.tororobot.R;
import com.tororobot.ui.fragment.BluetoothInitFragment;
import com.tororobot.ui.fragment.InitFragment;
import com.tororobot.util.Constants;

public class ContainerActivity extends AppCompatActivity {

    private String mMacAddress = "";
    private String mDeviceName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
//        mDeviceName = getIntent().getExtras().getString("name");
//        mMacAddress = getIntent().getExtras().getString("mac");
        selectFragment(getIntent().getExtras().getInt(Constants.ID_WHERE_FRAGMENT), savedInstanceState);
    }

    /**
     * Select id of fragment
     *
     * @param id                 id
     * @param savedInstanceState Bundle
     */
    private void selectFragment(int id, Bundle savedInstanceState) {
        switch (id) {
            case 11:
                setTitle("Bluetooth");
                goToFragment(BluetoothInitFragment.newInstance(id), savedInstanceState);
                break;
            case 12:
                setTitle("Streaming");
//                goToFragment(InitFragment.newInstance(id, mMacAddress, mDeviceName), savedInstanceState);
                goToFragment(InitFragment.newInstance(id), savedInstanceState);
                break;
            default:
                finish();
        }
    }

    /**
     * Go
     *
     * @param fragment           Fragment
     * @param savedInstanceState Bundle
     */
    private void goToFragment(Fragment fragment, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

}