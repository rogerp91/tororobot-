package com.tororobot.util;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.tororobot.R;

/**
 * Created by roger on 27/10/16.
 */

public class GoToFragment {

    public static void callFragment(@NonNull FragmentManager manager, @NonNull Fragment fragment) {
        manager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}