package com.tororobot.util;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.tororobot.TororobotAplications;

/**
 * Created by roger on 20/10/16.
 */


public class MSImp implements MS {

    @Override
    public void showSnackbar(@NonNull View view, @NonNull String msg, @ColorInt int color, @IntRange(from = 0, to = 2) int time) {
        Snackbar snackbar = Snackbar.make(view, msg, (time == 0 ? Snackbar.LENGTH_SHORT : Snackbar.LENGTH_LONG));
        if (color != 0) {
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(color);
        }
        snackbar.show();
    }

    @Override
    public void showToast(Context context, @NonNull String msg, @IntRange(from = 0, to = 1) int time) {
        Toast.makeText((context == null ? TororobotAplications.getContext() : context), msg, (time == 0 ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG)).show();
    }
}