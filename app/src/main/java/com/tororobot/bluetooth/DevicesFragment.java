package com.tororobot.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tororobot.R;
import com.tororobot.service.SocketService;
import com.tororobot.ui.activity.ContainerActivity;
import com.tororobot.ui.adapte.DevicesAdapter;
import com.tororobot.util.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

import static android.app.Activity.RESULT_OK;
import static com.tororobot.R.id.device;

public class DevicesFragment extends Fragment {

    public static DevicesFragment newInstance() {
        return new DevicesFragment();
    }

    public static final int ENABLE_BT__REQUEST = 1;

    private SmoothBluetooth mSmoothBluetooth;

    @BindView(R.id.scan)
    Button mScanButton;

    @BindView(R.id.state)
    TextView mStateTv;

    @BindView(device)
    TextView mDeviceTv;

    @BindView(R.id.paired)
    Button mPairedButton;

    @BindView(R.id.disconnect)
    Button mDisconnectButton;

    @BindView(R.id.connection)
    LinearLayout mConnectionLayout;

    @BindView(R.id.message)
    EditText mMessageInput;

    @BindView(R.id.send)
    Button mSendButton;

    private List<Integer> mBuffer = new ArrayList<>();
    private List<String> mResponseBuffer = new ArrayList<>();

    private ArrayAdapter<String> mResponsesAdapter;
    private DevicesAdapter devicesAdapter;

    @BindView(R.id.responses)
    ListView responseListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_customview, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSmoothBluetooth = new SmoothBluetooth(getActivity());

        SocketService.actionUpdate(getActivity(), "");

        mSmoothBluetooth.setListener(mListener);

        mResponsesAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mResponseBuffer);
        responseListView.setAdapter(mResponsesAdapter);

        mSendButton.setOnClickListener(v -> {
            mSmoothBluetooth.send(mMessageInput.getText().toString(), false);
            mMessageInput.setText("");
        });


        mDisconnectButton.setOnClickListener(v -> {
            mSmoothBluetooth.disconnect();
            mResponseBuffer.clear();
            mResponsesAdapter.notifyDataSetChanged();
        });

        mScanButton.setOnClickListener(v -> mSmoothBluetooth.doDiscovery());

        mPairedButton.setOnClickListener(v -> mSmoothBluetooth.tryConnection());

        mStateTv.setText("Disconnected");
    }

    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            Toast.makeText(getActivity(), "Bluetooth not found", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onBluetoothNotEnabled() {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, ENABLE_BT__REQUEST);
        }

        @Override
        public void onConnecting(Device device) {
            mStateTv.setText("Connecting to");
            mDeviceTv.setText(device.getName());
            //Connecting - Run Services
            Toast.makeText(getActivity(), "Connecting - Run Services", Toast.LENGTH_LONG).show();
            SocketService.actionUpdate(getActivity(), device.getAddress());
            Intent intent = new Intent(getActivity(), ContainerActivity.class);
            intent.putExtra(Constants.ID_WHERE_FRAGMENT, Constants.ID_STREAMING);
            startActivity(intent);
        }

        @Override
        public void onConnected(Device device) {
            mStateTv.setText("Connected to");
            mDeviceTv.setText(device.getName());
            mConnectionLayout.setVisibility(View.GONE);
            mDisconnectButton.setVisibility(View.VISIBLE);
        }

        @Override
        public void onDisconnected() {
            mStateTv.setText("Disconnected");
            mDeviceTv.setText("");
            mDisconnectButton.setVisibility(View.GONE);
            mConnectionLayout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onConnectionFailed(Device device) {
            mStateTv.setText("Disconnected");
            mDeviceTv.setText("");
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
        public void onDevicesFound(final List<Device> deviceList, final SmoothBluetooth.ConnectionCallback connectionCallback) {
            getActivity().runOnUiThread(() -> {
                devicesAdapter = new DevicesAdapter(getActivity().getApplicationContext(), deviceList);
                responseListView.setAdapter(devicesAdapter);
                responseListView.setOnItemClickListener((parent, view, position, id) -> connectionCallback.connectTo(deviceList.get(position)));
                mResponsesAdapter.notifyDataSetChanged();
            });
        }

        @Override
        public void onDataReceived(int data) {
            mBuffer.add(data);
            if (data == 62 && !mBuffer.isEmpty()) {
                //if (data == 0x0D && !mBuffer.isEmpty() && mBuffer.get(mBuffer.size()-2) == 0xA0) {
                StringBuilder sb = new StringBuilder();
                for (int integer : mBuffer) {
                    sb.append((char) integer);
                }
                mBuffer.clear();
                mResponseBuffer.add(0, sb.toString());
                mResponsesAdapter.notifyDataSetChanged();
            }
        }

    };

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
                mSmoothBluetooth.tryConnection();
            }
        }
    }
}
