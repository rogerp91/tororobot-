package com.tororobot.util;

import android.support.annotation.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by roger on 19/10/16.
 */

public class URLs {

    static String BASE_URL = "http://beta.usetime.co/api/";
    static String VERSION = "v1/";
    static String PREFIX = "Bearer ";
    static String USER_AGENT = "Tororobot-Android/";

    public static String getAbsoluteUrl(@NonNull String relativeUrl) {
        checkArgument(!relativeUrl.isEmpty(), "Url relative can not be empty");
        return BASE_URL + VERSION + relativeUrl;
    }

}