package com.tororobot.util;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by roger on 20/10/16.
 */

public interface MS {


    void showSnackbar(@NonNull View view, @NonNull String msg, @ColorInt int color, @IntRange(from = 0, to = 2) int time);

    void showToast(Context context, @NonNull String msg, @IntRange(from = 0, to = 1) int time);

}