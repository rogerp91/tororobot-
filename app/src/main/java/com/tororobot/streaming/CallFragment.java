package com.tororobot.streaming;

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

import com.tororobot.R;
import com.tororobot.service.TypeCommand;
import com.tororobot.util.Constants;

import org.webrtc.RendererCommon.ScalingType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

import static android.app.Activity.RESULT_OK;
import static com.tororobot.util.Constants.EXTRA_ROOMID;
import static com.tororobot.util.Constants.EXTRA_VIDEO_CALL;
import static com.tororobot.util.Constants.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED;

/**
 * Fragment for call control.
 */
public class CallFragment extends Fragment {

    public static final int ENABLE_BT__REQUEST = 1;

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

    private OnCallEvents callEvents;
    private ScalingType scalingType;
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

    private TypeCommand command;
    Bundle args = null;

    /**
     * Call control interface for container activity.
     */
    public interface OnCallEvents {

        void onCallHangUp();

        void onCameraSwitch();

        void onVideoScalingSwitch(ScalingType scalingType);

        void onCaptureFormatChange(int width, int height, int framerate);

        boolean onToggleMic();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);
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
            if (scalingType == ScalingType.SCALE_ASPECT_FILL) {
                videoScalingButton.setBackgroundResource(R.drawable.ic_fullscreen_black_48dp);
                scalingType = ScalingType.SCALE_ASPECT_FIT;
            } else {
                videoScalingButton.setBackgroundResource(R.drawable.ic_fullscreen_exit_black_48dp);
                scalingType = ScalingType.SCALE_ASPECT_FILL;
            }
            callEvents.onVideoScalingSwitch(scalingType);
        });
        scalingType = ScalingType.SCALE_ASPECT_FILL;

//        toggleMuteButton.setOnClickListener(view -> {
//            boolean enabled = callEvents.onToggleMic();
//            toggleMuteButton.setAlpha(enabled ? 1.0f : 0.3f);
//        });

        mSmoothBluetooth = new SmoothBluetooth(getActivity());
        mSmoothBluetooth.setListener(mListener);
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
        callEvents = (OnCallEvents) context;
    }

    @OnClick(R.id.btn_connet)
    void clickConnetBluetooth() {

//        if (!mSmoothBluetooth.isBluetoothEnabled()) {
//            new MaterialDialog.Builder(getActivity())
//                    .iconRes(R.mipmap.ic_launcher)
//                    .limitIconToDefaultSize()
//                    .title("Activar Bluetooth")
//                    .content("Para poder mo")
//                    .positiveText("Aceptar")
//                    .negativeText("Rechazar")
//                    .onPositive((dialog, which) -> startActivity(new Intent().setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)))
//                    .onNegative((dialog, which) -> dialog.dismiss())
//                    .show();
//
//        }
//
//        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
//                .title("Conexión Bluetooth")
//                .customView(R.layout.dialog_customview, true)
//                .positiveText("Conectar")
//                .negativeText(android.R.string.cancel)
//                .onPositive((dialog1, which) -> {
//
//                }).build();
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSmoothBluetooth.stop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ENABLE_BT__REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getActivity(), "Conexión establecida...", Toast.LENGTH_SHORT).show();
                mSmoothBluetooth.tryConnection();
            }
        }
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


    @OnClick(R.id.up_move)
    void clickUpMove() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.UP);
        getActivity().sendBroadcast(new Intent(Constants.UP));
    }

    @OnClick(R.id.left_move)
    void clickLeftMove() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.LEFT);
        getActivity().sendBroadcast(new Intent(Constants.LEFT));
    }

    @OnClick(R.id.down_move)
    void clickDownMove() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.DOWN);
        getActivity().sendBroadcast(new Intent(Constants.DOWN));
    }

    @OnClick(R.id.right_move)
    void clickRightMove() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.RIGHT);
        getActivity().sendBroadcast(new Intent(Constants.RIGHT));
    }

    @OnClick(R.id.up_camera)
    void clickUpCamera() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.MOVE_UP);
        getActivity().sendBroadcast(new Intent(Constants.MOVE_UP));
    }

    @OnClick(R.id.left_camera)
    void clickLeftCamera() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.MOVE_LEFT);
        getActivity().sendBroadcast(new Intent(Constants.MOVE_LEFT));
    }

    @OnClick(R.id.down_camera)
    void clickDownCamera() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.MOVE_DOWN);
        getActivity().sendBroadcast(new Intent(Constants.MOVE_DOWN));
    }

    @OnClick(R.id.right_camera)
    void clickRightCamera() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.MOVE_RIGHT);
        getActivity().sendBroadcast(new Intent(Constants.MOVE_RIGHT));
    }

}