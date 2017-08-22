package com.tororobot.presenter;

import android.support.annotation.NonNull;

/**
 * Created by roger on 19/10/16.
 */

public interface BasePresenter<T> {


    void setView(@NonNull T view);

}
