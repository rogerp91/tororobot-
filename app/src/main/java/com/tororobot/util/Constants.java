package com.tororobot.util;

public class Constants {

    public static final String SESSION = "session";

    public static final String PASSWORD = "password";
    public static final String EMAIL = "email";


    /**
     * {@link com.tororobot.streaming.CallActivity}
     */
    public static final String EXTRA_ROOMID = "com.tororobot.ROOMID";
    public static final String EXTRA_LOOPBACK = "com.tororobot.LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "com.tororobot.VIDEO_CALL";
    public static final String EXTRA_CAMERA2 = "com.tororobot.CAMERA2";
    public static final String EXTRA_VIDEO_WIDTH = "com.tororobot.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "com.tororobot.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "com.tororobot.VIDEO_FPS";
    public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED = "com.tororobot.VIDEO_CAPTUREQUALITYSLIDER";
    public static final String EXTRA_VIDEO_BITRATE = "com.tororobot.VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = "com.tororobot.VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "com.tororobot.HWCODEC";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "com.tororobot.CAPTURETOTEXTURE";
    public static final String EXTRA_AUDIO_BITRATE = "com.tororobot.AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "com.tororobot.AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED = "com.tororobot.NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = "com.tororobot.AECDUMP";
    public static final String EXTRA_OPENSLES_ENABLED = "com.tororobot.OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "com.tororobot.DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "com.tororobot.DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "com.tororobot.DISABLE_BUILT_IN_NS";
    public static final String EXTRA_ENABLE_LEVEL_CONTROL = "com.tororobot.ENABLE_LEVEL_CONTROL";
    public static final String EXTRA_DISPLAY_HUD = "com.tororobot.DISPLAY_HUD";
    public static final String EXTRA_TRACING = "com.tororobot.TRACING";
    public static final String EXTRA_CMDLINE = "com.tororobot.CMDLINE";
    public static final String EXTRA_RUNTIME = "com.tororobot.RUNTIME";

    // List of mandatory application permissions.
    public static final String[] MANDATORY_PERMISSIONS = {
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET"
    };

    // Peer connection statistics callback period in ms.
    public static final int STAT_CALLBACK_PERIOD = 1000;
    // Local preview screen position before call is connected.
    public static final int LOCAL_X_CONNECTING = 0;
    public static final int LOCAL_Y_CONNECTING = 0;
    public static final int LOCAL_WIDTH_CONNECTING = 100;
    public static final int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    public static final int LOCAL_X_CONNECTED = 0;
    public static final int LOCAL_Y_CONNECTED = 0;
    public static final int LOCAL_WIDTH_CONNECTED = 30;
    public static final int LOCAL_HEIGHT_CONNECTED = 20;
    // Remote video screen position
    public static final int REMOTE_X = 0;
    public static final int REMOTE_Y = 0;
    public static final int REMOTE_WIDTH = 100;
    public static final int REMOTE_HEIGHT = 100;

    //Indices de fragment
    public static final String ID_WHERE_FRAGMENT = "ID_WHERE_FRAGMENT";
    public static final int ID_BLUETOOTH = 11;
    public static final int ID_STREAMING = 12;

    public static final String ACTION_COMMAND = "com.tororobot.action.COMMAND";

    public static final String UP = "com.tororobot.action.UP";
    public static final String LEFT = "com.tororobot.action.LEFT";
    public static final String DOWN = "com.tororobot.action.DOWN";
    public static final String RIGHT = "com.tororobot.action.RIGHT";
    public static final String MOVE_UP = "com.tororobot.action.MOVE_UP";
    public static final String MOVE_LEFT = "com.tororobot.action.MOVE_LEFT";
    public static final String MOVE_DOWN = "com.tororobot.action.MOVE_DOWN";
    public static final String MOVE_RIGHT = "com.tororobot.action.MOVE_RIGHT";


    public static final String COMMAND_UP = "com.tororobot.action.command.UP";
    public static final String COMMAND_LEFT = "com.tororobot.action.command.LEFT";
    public static final String COMMAND_DOWN = "com.tororobot.action.command.DOWN";
    public static final String COMMAND_RIGHT = "com.tororobot.action.command.RIGHT";
    public static final String COMMAND_MOVE_UP = "com.tororobot.action.command.MOVE_UP";
    public static final String COMMAND_MOVE_LEFT = "com.tororobot.action.command.MOVE_LEFT";
    public static final String COMMAND_MOVE_DOWN = "com.tororobot.action.command.MOVE_DOWN";
    public static final String COMMAND_MOVE_RIGHT = "com.tororobot.action.command.MOVE_RIGHT";


    //Services
    public static final String STARTED = "started";
    public static final String RETRY_INTERVAL = "retry_interval";
    public static final long KEEP_ALIVE_INTERVAL = 1000 * 60 * 5;
    public static final long INITIAL_RETRY_INTERVAL = 1000 * 10;
    public static final long MAXIMUM_RETRY_INTERVAL = 1000 * 60 * 30;


}