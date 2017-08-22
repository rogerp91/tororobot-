package com.tororobot.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tororobot.R;

import org.webrtc.RendererCommon;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

import static com.tororobot.streaming.CallFragment.ENABLE_BT__REQUEST;
import static com.tororobot.util.Constants.EXTRA_ROOMID;
import static com.tororobot.util.Constants.EXTRA_VIDEO_CALL;
import static com.tororobot.util.Constants.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED;

/**
 * Created by roger on 02/11/16.
 */

public class BluetoothCallFragment extends Fragment {

    @BindView(R.id.contact_name_call)
    TextView contactView;

//    @BindView(R.id.button_call_disconnect)
//    ImageButton disconnectButton;

    @BindView(R.id.button_call_switch_camera)
    ImageView cameraSwitchButton;

    @BindView(R.id.button_call_scaling_mode)
    ImageView videoScalingButton;

//    @BindView(R.id.button_call_toggle_mic)
//    ImageButton toggleMuteButton;

//    @BindView(R.id.capture_format_text_call)
//    TextView captureFormatText;
//
//    @BindView(R.id.capture_format_slider_call)
//    SeekBar captureFormatSlider;

    private BluetoothCallFragment.OnCallEvents callEvents;
    private RendererCommon.ScalingType scalingType;
    private boolean videoCallEnabled = true;
    private SmoothBluetooth mSmoothBluetooth;
    private Button mScanButton;

    private TextView mStateTv;

    private TextView mDeviceTv;

    private Button mPairedButton;

    private Button mDisconnectButton;

    private LinearLayout mConnectionLayout;

    private EditText mMessageInput;

    private Button mSendButton;

    private CheckBox mCRLFBox;

    private List<Integer> mBuffer = new ArrayList<>();
    private List<String> mResponseBuffer = new ArrayList<>();

    private ArrayAdapter<String> mResponsesAdapter;
    private ListView responseListView;

    /**
     * Call control interface for container activity.
     */
    public interface OnCallEvents {

        void onCallHangUp();

        void onCameraSwitch();

        void onVideoScalingSwitch(RendererCommon.ScalingType scalingType);

        void onCaptureFormatChange(int width, int height, int framerate);

        boolean onToggleMic();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call_bluetooth, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view_, Bundle savedInstanceState) {
        super.onViewCreated(view_, savedInstanceState);
        // Add buttons click events.
//        disconnectButton.setOnClickListener(view -> callEvents.onCallHangUp());

        cameraSwitchButton.setOnClickListener(view -> callEvents.onCameraSwitch());

        videoScalingButton.setOnClickListener(view -> {
            if (scalingType == RendererCommon.ScalingType.SCALE_ASPECT_FILL) {
                videoScalingButton.setBackgroundResource(R.drawable.ic_fullscreen_black_48dp);
                scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
            } else {
                videoScalingButton.setBackgroundResource(R.drawable.ic_fullscreen_exit_black_48dp);
                scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
            }
            callEvents.onVideoScalingSwitch(scalingType);
        });
        scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;

//        toggleMuteButton.setOnClickListener(view -> {
//            boolean enabled = callEvents.onToggleMic();
//            toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
//        });
        mSmoothBluetooth = new SmoothBluetooth(getActivity());

    }

    @Override
    public void onStart() {
        super.onStart();

        boolean captureSliderEnabled = false;
        Bundle args = getArguments();
        if (args != null) {
            String contactName = args.getString(EXTRA_ROOMID);
            contactView.setText(contactName);
            videoCallEnabled = args.getBoolean(EXTRA_VIDEO_CALL, true);
            captureSliderEnabled = videoCallEnabled && args.getBoolean(EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
        }
        if (!videoCallEnabled) {
            cameraSwitchButton.setVisibility(View.INVISIBLE);
        }
//        if (captureSliderEnabled) {
//            captureFormatSlider.setOnSeekBarChangeListener(new CaptureQualityController(captureFormatText, callEvents));
//        } else {
//            captureFormatText.setVisibility(View.GONE);
//            captureFormatSlider.setVisibility(View.GONE);
//        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        callEvents = (BluetoothCallFragment.OnCallEvents) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSmoothBluetooth.stop();
    }

    @OnClick(R.id.btn_connet)
    void clickConnetBluetooth() {

//        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
//                .title("Conexión Bluetooth")
//                .customView(R.layout.dialog_customview, true)
//                .positiveText("Conectar")
//                .negativeText(android.R.string.cancel)
//                .onPositive((dialog1, which) -> {
//
//                }).build();
//        mSmoothBluetooth.setListener(mListener);
//        responseListView = (ListView) dialog.getCustomView().findViewById(R.id.responses);
//        mResponsesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mResponseBuffer);
//        responseListView.setAdapter(mResponsesAdapter);
//        mCRLFBox = (CheckBox) dialog.getCustomView().findViewById(R.id.carrage);
//        mMessageInput = (EditText) dialog.getCustomView().findViewById(R.id.message);
//        mSendButton = (Button) dialog.getCustomView().findViewById(R.id.send);
//        mDisconnectButton = (Button) dialog.getCustomView().findViewById(R.id.disconnect);
//        mConnectionLayout = (LinearLayout) dialog.getCustomView().findViewById(R.id.connection);
//        mScanButton = (Button) dialog.getCustomView().findViewById(R.id.scan);
//        mPairedButton = (Button) dialog.getCustomView().findViewById(R.id.paired);
//        mStateTv = (TextView) dialog.getCustomView().findViewById(R.id.state);
//        mStateTv.setText("Disconnected");
//        mDeviceTv = (TextView) dialog.getCustomView().findViewById(R.id.device);
//
//        mSendButton.setOnClickListener(v -> {
//            mSmoothBluetooth.send(mMessageInput.getText().toString(), mCRLFBox.isChecked());
//            mMessageInput.setText("");
//        });
//
//        mDisconnectButton.setOnClickListener(v -> {
//            mSmoothBluetooth.disconnect();
//            mResponseBuffer.clear();
//            mResponsesAdapter.notifyDataSetChanged();
//        });
//        mScanButton.setOnClickListener(v -> mSmoothBluetooth.doDiscovery());
//
//        mPairedButton.setOnClickListener(v -> mSmoothBluetooth.tryConnection());
//
//        dialog.show();

        startActivity(new Intent(getActivity(), DevicesActivity.class));
    }

    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            Toast.makeText(getActivity(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        @Override
        public void onBluetoothNotEnabled() {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, ENABLE_BT__REQUEST);
        }

        @Override
        public void onConnecting(Device device) {
            Toast.makeText(getActivity(), "Connecting to" + device.getName(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnected(Device device) {
            Toast.makeText(getActivity(), "Connecting to" + device.getName(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnected() {
            Toast.makeText(getActivity(), "onDisconnected", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectionFailed(Device device) {
            Toast.makeText(getActivity(), "Failed to connect to " + device.getName(), Toast.LENGTH_SHORT).show();
            if (device.isPaired()) {
                mSmoothBluetooth.doDiscovery();
            }
        }

        @Override
        public void onDiscoveryStarted() {
            Toast.makeText(getActivity(), "Searching", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDiscoveryFinished() {

        }

        @Override
        public void onNoDevicesFound() {
            Toast.makeText(getActivity(), "No device found", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {

        }

        @Override
        public void onDataReceived(int data) {

        }
    };
}