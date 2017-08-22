package com.tororobot.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.tororobot.R;
import com.tororobot.streaming.CallActivity;
import com.tororobot.ui.fragment.SettingsActivity;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Random;

import static com.tororobot.util.Constants.EXTRA_AECDUMP_ENABLED;
import static com.tororobot.util.Constants.EXTRA_AUDIOCODEC;
import static com.tororobot.util.Constants.EXTRA_AUDIO_BITRATE;
import static com.tororobot.util.Constants.EXTRA_CAMERA2;
import static com.tororobot.util.Constants.EXTRA_CAPTURETOTEXTURE_ENABLED;
import static com.tororobot.util.Constants.EXTRA_CMDLINE;
import static com.tororobot.util.Constants.EXTRA_DISABLE_BUILT_IN_AEC;
import static com.tororobot.util.Constants.EXTRA_DISABLE_BUILT_IN_AGC;
import static com.tororobot.util.Constants.EXTRA_DISABLE_BUILT_IN_NS;
import static com.tororobot.util.Constants.EXTRA_DISPLAY_HUD;
import static com.tororobot.util.Constants.EXTRA_ENABLE_LEVEL_CONTROL;
import static com.tororobot.util.Constants.EXTRA_HWCODEC_ENABLED;
import static com.tororobot.util.Constants.EXTRA_LOOPBACK;
import static com.tororobot.util.Constants.EXTRA_NOAUDIOPROCESSING_ENABLED;
import static com.tororobot.util.Constants.EXTRA_OPENSLES_ENABLED;
import static com.tororobot.util.Constants.EXTRA_ROOMID;
import static com.tororobot.util.Constants.EXTRA_RUNTIME;
import static com.tororobot.util.Constants.EXTRA_TRACING;
import static com.tororobot.util.Constants.EXTRA_VIDEOCODEC;
import static com.tororobot.util.Constants.EXTRA_VIDEO_BITRATE;
import static com.tororobot.util.Constants.EXTRA_VIDEO_CALL;
import static com.tororobot.util.Constants.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED;
import static com.tororobot.util.Constants.EXTRA_VIDEO_FPS;
import static com.tororobot.util.Constants.EXTRA_VIDEO_HEIGHT;
import static com.tororobot.util.Constants.EXTRA_VIDEO_WIDTH;

/**
 * Handles the initial setup where the user selects which room to join.
 */
public class ConnectActivity extends AppCompatActivity {
    private static final String TAG = "ConnectActivity";
    private static final int CONNECTION_REQUEST = 1;
    private static final int REMOVE_FAVORITE_INDEX = 0;
    private static boolean commandLineRun = false;

    private ImageButton connectButton;
    private ImageButton addFavoriteButton;
    private EditText roomEditText;
    private ListView roomListView;
    private SharedPreferences sharedPref;
    private String keyprefVideoCallEnabled;
    private String keyprefCamera2;
    private String keyprefResolution;
    private String keyprefFps;
    private String keyprefCaptureQualitySlider;
    private String keyprefVideoBitrateType;
    private String keyprefVideoBitrateValue;
    private String keyprefVideoCodec;
    private String keyprefAudioBitrateType;
    private String keyprefAudioBitrateValue;
    private String keyprefAudioCodec;
    private String keyprefHwCodecAcceleration;
    private String keyprefCaptureToTexture;
    private String keyprefNoAudioProcessingPipeline;
    private String keyprefAecDump;
    private String keyprefOpenSLES;
    private String keyprefDisableBuiltInAec;
    private String keyprefDisableBuiltInAgc;
    private String keyprefDisableBuiltInNs;
    private String keyprefEnableLevelControl;
    private String keyprefDisplayHud;
    private String keyprefTracing;
    private String keyprefRoomServerUrl;
    private String keyprefRoom;
    private String keyprefRoomList;
    private ArrayList<String> roomList;
    private ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get setting keys.
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        keyprefVideoCallEnabled = getString(R.string.pref_videocall_key);
        keyprefCamera2 = getString(R.string.pref_camera2_key);
        keyprefResolution = getString(R.string.pref_resolution_key);
        keyprefFps = getString(R.string.pref_fps_key);
        keyprefCaptureQualitySlider = getString(R.string.pref_capturequalityslider_key);
        keyprefVideoBitrateType = getString(R.string.pref_startvideobitrate_key);
        keyprefVideoBitrateValue = getString(R.string.pref_startvideobitratevalue_key);
        keyprefVideoCodec = getString(R.string.pref_videocodec_key);
        keyprefHwCodecAcceleration = getString(R.string.pref_hwcodec_key);
        keyprefCaptureToTexture = getString(R.string.pref_capturetotexture_key);
        keyprefAudioBitrateType = getString(R.string.pref_startaudiobitrate_key);
        keyprefAudioBitrateValue = getString(R.string.pref_startaudiobitratevalue_key);
        keyprefAudioCodec = getString(R.string.pref_audiocodec_key);
        keyprefNoAudioProcessingPipeline = getString(R.string.pref_noaudioprocessing_key);
        keyprefAecDump = getString(R.string.pref_aecdump_key);
        keyprefOpenSLES = getString(R.string.pref_opensles_key);
        keyprefDisableBuiltInAec = getString(R.string.pref_disable_built_in_aec_key);
        keyprefDisableBuiltInAgc = getString(R.string.pref_disable_built_in_agc_key);
        keyprefDisableBuiltInNs = getString(R.string.pref_disable_built_in_ns_key);
        keyprefEnableLevelControl = getString(R.string.pref_enable_level_control_key);
        keyprefDisplayHud = getString(R.string.pref_displayhud_key);
        keyprefTracing = getString(R.string.pref_tracing_key);
        keyprefRoomServerUrl = getString(R.string.pref_room_server_url_key);
        keyprefRoom = getString(R.string.pref_room_key);
        keyprefRoomList = getString(R.string.pref_room_list_key);

        setContentView(R.layout.activity_connect);

        roomEditText = (EditText) findViewById(R.id.room_edittext);
        roomEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                addFavoriteButton.performClick();
                return true;
            }
            return false;
        });
        roomEditText.requestFocus();

        roomListView = (ListView) findViewById(R.id.room_listview);
        roomListView.setEmptyView(findViewById(android.R.id.empty));
        roomListView.setOnItemClickListener(roomListClickListener);
        registerForContextMenu(roomListView);
        connectButton = (ImageButton) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectListener);
        addFavoriteButton = (ImageButton) findViewById(R.id.add_favorite_button);
        addFavoriteButton.setOnClickListener(addFavoriteListener);

        // If an implicit VIEW intent is launching the app, go directly to that URL.
        final Intent intent = getIntent();
        if ("android.intent.action.VIEW".equals(intent.getAction()) && !commandLineRun) {
            boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
            int runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);
            String room = sharedPref.getString(keyprefRoom, "");
            connectToRoom(room, true, loopback, runTimeMs);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connect_menu, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.room_listview) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(roomList.get(info.position));
            String[] menuItems = getResources().getStringArray(R.array.roomListContextMenu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        } else {
            super.onCreateContextMenu(menu, v, menuInfo);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == REMOVE_FAVORITE_INDEX) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            roomList.remove(info.position);
            adapter.notifyDataSetChanged();
            return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        if (item.getItemId() == R.id.action_settings) {
            intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
//        } else if (item.getItemId() == R.id.action_chat) {
//            intent = new Intent(this, ChatActivity.class);
//            startActivity(intent);
//            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        String room = roomEditText.getText().toString();
        String roomListJson = new JSONArray(roomList).toString();
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(keyprefRoom, room);
        editor.putString(keyprefRoomList, roomListJson);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        String room = sharedPref.getString(keyprefRoom, "");
        roomEditText.setText(room);
        roomList = new ArrayList<>();
        String roomListJson = sharedPref.getString(keyprefRoomList, null);
        if (roomListJson != null) {
            try {
                JSONArray jsonArray = new JSONArray(roomListJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    roomList.add(jsonArray.get(i).toString());
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed to load room list: " + e.toString());
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roomList);
        roomListView.setAdapter(adapter);
        if (adapter.getCount() > 0) {
            roomListView.requestFocus();
            roomListView.setItemChecked(0, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            Log.d(TAG, "Return: " + resultCode);
            setResult(resultCode);
            commandLineRun = false;
            finish();
        }
    }

    private void connectToRoom(String roomId, boolean commandLineRun, boolean loopback, int runTimeMs) {
        ConnectActivity.commandLineRun = commandLineRun;
        // roomId is random for loopback.
        if (loopback) {
            roomId = Integer.toString((new Random()).nextInt(100000000));
        }

        String roomUrl = sharedPref.getString(keyprefRoomServerUrl, getString(R.string.pref_room_server_url_default));

        // Video call enabled flag.
        boolean videoCallEnabled = sharedPref.getBoolean(keyprefVideoCallEnabled,
                Boolean.valueOf(getString(R.string.pref_videocall_default)));

        // Use Camera2 option.
        boolean useCamera2 = sharedPref.getBoolean(keyprefCamera2, Boolean.valueOf(getString(R.string.pref_camera2_default)));

        // Get default codecs.
        String videoCodec = sharedPref.getString(keyprefVideoCodec, getString(R.string.pref_videocodec_default));
        String audioCodec = sharedPref.getString(keyprefAudioCodec, getString(R.string.pref_audiocodec_default));

        // Check HW codec flag.
        boolean hwCodec = sharedPref.getBoolean(keyprefHwCodecAcceleration, Boolean.valueOf(getString(R.string.pref_hwcodec_default)));

        // Check Capture to texture.
        boolean captureToTexture = sharedPref.getBoolean(keyprefCaptureToTexture, Boolean.valueOf(getString(R.string.pref_capturetotexture_default)));

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = sharedPref.getBoolean(keyprefNoAudioProcessingPipeline, Boolean.valueOf(getString(R.string.pref_noaudioprocessing_default)));

        // Check Disable Audio Processing flag.
        boolean aecDump = sharedPref.getBoolean(keyprefAecDump, Boolean.valueOf(getString(R.string.pref_aecdump_default)));

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = sharedPref.getBoolean(keyprefOpenSLES, Boolean.valueOf(getString(R.string.pref_opensles_default)));

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = sharedPref.getBoolean(keyprefDisableBuiltInAec, Boolean.valueOf(getString(R.string.pref_disable_built_in_aec_default)));

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = sharedPref.getBoolean(keyprefDisableBuiltInAgc, Boolean.valueOf(getString(R.string.pref_disable_built_in_agc_default)));

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = sharedPref.getBoolean(keyprefDisableBuiltInNs, Boolean.valueOf(getString(R.string.pref_disable_built_in_ns_default)));

        // Check Enable level control.
        boolean enableLevelControl = sharedPref.getBoolean(keyprefEnableLevelControl, Boolean.valueOf(getString(R.string.pref_enable_level_control_key)));

        // Get video resolution from settings.
        int videoWidth = 0;
        int videoHeight = 0;
        String resolution = sharedPref.getString(keyprefResolution, getString(R.string.pref_resolution_default));
        String[] dimensions = resolution.split("[ x]+");
        if (dimensions.length == 2) {
            try {
                videoWidth = Integer.parseInt(dimensions[0]);
                videoHeight = Integer.parseInt(dimensions[1]);
            } catch (NumberFormatException e) {
                videoWidth = 0;
                videoHeight = 0;
                Log.e(TAG, "Wrong video resolution setting: " + resolution);
            }
        }

        // Get camera fps from settings.
        int cameraFps = 0;
        String fps = sharedPref.getString(keyprefFps, getString(R.string.pref_fps_default));
        String[] fpsValues = fps.split("[ x]+");
        if (fpsValues.length == 2) {
            try {
                cameraFps = Integer.parseInt(fpsValues[0]);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Wrong camera fps setting: " + fps);
            }
        }

        // Check capture quality slider flag.
        boolean captureQualitySlider = sharedPref.getBoolean(keyprefCaptureQualitySlider, Boolean.valueOf(getString(R.string.pref_capturequalityslider_default)));

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        String bitrateTypeDefault = getString(
                R.string.pref_startvideobitrate_default);
        String bitrateType = sharedPref.getString(keyprefVideoBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(keyprefVideoBitrateValue, getString(R.string.pref_startvideobitratevalue_default));
            videoStartBitrate = Integer.parseInt(bitrateValue);
        }
        int audioStartBitrate = 0;
        bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
        bitrateType = sharedPref.getString(keyprefAudioBitrateType, bitrateTypeDefault);
        if (!bitrateType.equals(bitrateTypeDefault)) {
            String bitrateValue = sharedPref.getString(keyprefAudioBitrateValue, getString(R.string.pref_startaudiobitratevalue_default));
            audioStartBitrate = Integer.parseInt(bitrateValue);
        }

        // Check statistics display option.
        boolean displayHud = sharedPref.getBoolean(keyprefDisplayHud, Boolean.valueOf(getString(R.string.pref_displayhud_default)));

        boolean tracing = sharedPref.getBoolean(keyprefTracing, Boolean.valueOf(getString(R.string.pref_tracing_default)));

        // Start AppRTCDemo activity.
        Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);
        if (validateUrl(roomUrl)) {
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(this, CallActivity.class);
            intent.setData(uri);
            intent.putExtra(EXTRA_ROOMID, roomId);
            intent.putExtra(EXTRA_LOOPBACK, loopback);
            intent.putExtra(EXTRA_VIDEO_CALL, videoCallEnabled);
            intent.putExtra(EXTRA_CAMERA2, useCamera2);
            intent.putExtra(EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(EXTRA_VIDEO_FPS, cameraFps);
            intent.putExtra(EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
            intent.putExtra(EXTRA_VIDEO_BITRATE, videoStartBitrate);
            intent.putExtra(EXTRA_VIDEOCODEC, videoCodec);
            intent.putExtra(EXTRA_HWCODEC_ENABLED, hwCodec);
            intent.putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
            intent.putExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
            intent.putExtra(EXTRA_AECDUMP_ENABLED, aecDump);
            intent.putExtra(EXTRA_OPENSLES_ENABLED, useOpenSLES);
            intent.putExtra(EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
            intent.putExtra(EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
            intent.putExtra(EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
            intent.putExtra(EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl);
            intent.putExtra(EXTRA_AUDIO_BITRATE, audioStartBitrate);
            intent.putExtra(EXTRA_AUDIOCODEC, audioCodec);
            intent.putExtra(EXTRA_DISPLAY_HUD, displayHud);
            intent.putExtra(EXTRA_TRACING, tracing);
            intent.putExtra(EXTRA_CMDLINE, commandLineRun);
            intent.putExtra(EXTRA_RUNTIME, runTimeMs);

            startActivityForResult(intent, CONNECTION_REQUEST);
        }
    }

    private boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok, (dialog, id) -> dialog.cancel()).create().show();
        return false;
    }

    private final AdapterView.OnItemClickListener roomListClickListener = (adapterView, view, i, l) -> {
        String roomId = ((TextView) view).getText().toString();
        connectToRoom(roomId, false, false, 0);
    };

    private final OnClickListener addFavoriteListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String newRoom = roomEditText.getText().toString();
            if (newRoom.length() > 0 && !roomList.contains(newRoom)) {
                adapter.add(newRoom);
                adapter.notifyDataSetChanged();
            }
        }
    };

    private final OnClickListener connectListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            connectToRoom(roomEditText.getText().toString(), false, false, 0);
        }
    };
}
