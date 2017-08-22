package com.tororobot.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.tororobot.R;

/**
 * Created by roger on 18/10/16.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
