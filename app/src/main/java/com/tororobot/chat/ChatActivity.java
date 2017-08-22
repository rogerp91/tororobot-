package com.tororobot.chat;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tororobot.R;
import com.tororobot.util.Constants;
import com.tororobot.util.StateBluetooth;

import butterknife.ButterKnife;
import butterknife.OnClick;
import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;

public class ChatActivity extends AppCompatActivity {

    private BluetoothController mBluetoothController;

    private Button btnDisconnect, btnSend;
    private EditText etSend;
    private TextView tvConnectState, tvContent, tvDeviceName, tvDeviceMac;

    private int mConnectState;
    private String mMacAddress = "", mDeviceName = "";

    private static final String TAG = "LMBluetoothSdk";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mMacAddress = getIntent().getStringExtra("mac");
        mDeviceName = getIntent().getStringExtra("name");

        mBluetoothController = BluetoothController.getInstance();
        mBluetoothController.setBluetoothListener(new BluetoothListener() {
            @Override
            public void onActionStateChanged(int preState, int state) {
                Toast.makeText(ChatActivity.this, "BT state: " + state, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onActionDiscoveryStateChanged(String discoveryState) {
            }

            @Override
            public void onActionScanModeChanged(int preScanMode, int scanMode) {
            }

            @Override
            public void onBluetoothServiceStateChanged(final int state) {
                // If you want to update UI, please run this on UI thread
                runOnUiThread(() -> {
                    mConnectState = state;
                    tvConnectState.setText("Connection state: " + StateBluetooth.transConnStateAsString(state));
                });
            }

            @Override
            public void onActionDeviceFound(BluetoothDevice device, short rssi) {
            }

            @Override
            public void onReadData(final BluetoothDevice device, final byte[] data) {
                // If you want to update UI, please run this on UI thread
                runOnUiThread(() -> {
                    String deviceName = device == null ? "" : device.getName();
                    tvContent.append(deviceName + ": " + new String(data) + "\n");
                });
            }
        });

        btnSend = (Button) findViewById(R.id.btn_send);
        tvConnectState = (TextView) findViewById(R.id.tv_connect_state);
//        etSend = (EditText) findViewById(R.id.et_send_content);
        tvContent = (TextView) findViewById(R.id.tv_chat_content);
        tvDeviceName = (TextView) findViewById(R.id.tv_device_name);
        tvDeviceMac = (TextView) findViewById(R.id.tv_device_mac);

        tvDeviceName.setText("Device: " + mDeviceName);
        tvDeviceMac.setText("MAC address: " + mMacAddress);
        tvConnectState.setText("Connection state: " + StateBluetooth.transConnStateAsString(mBluetoothController.getConnectionState()));

//        btnSend.setOnClickListener(v -> {
//            String msg = etSend.getText().toString();
//            if (TextUtils.isEmpty(msg)) {
//                return;
//            }
//            mBluetoothController.write(msg.getBytes());
//            tvContent.append("Me: " + msg + "\n");
//            etSend.setText("");
//        });
        if (!TextUtils.isEmpty(mMacAddress)) {
            mBluetoothController.connect(mMacAddress);
        } else {
            if (mBluetoothController.getConnectedDevice() == null) {
                return;
            }
            mDeviceName = mBluetoothController.getConnectedDevice().getName();
            mMacAddress = mBluetoothController.getConnectedDevice().getAddress();
            tvDeviceName.setText("Device: " + mDeviceName);
            tvDeviceMac.setText("MAC address: " + mMacAddress);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mConnectState == State.STATE_CONNECTED) {
            mBluetoothController.disconnect();
        }
        finish();
    }

    @OnClick(R.id.up_move)
    void clickUpMove() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.UP);
        sendBroadcast(new Intent(Constants.UP));
    }

    @OnClick(R.id.left_move)
    void clickLeftMove() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.LEFT);
        sendBroadcast(new Intent(Constants.LEFT));
    }

    @OnClick(R.id.down_move)
    void clickDownMove() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.DOWN);
        sendBroadcast(new Intent(Constants.DOWN));
    }

    @OnClick(R.id.right_move)
    void clickRightMove() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.RIGHT);
        sendBroadcast(new Intent(Constants.RIGHT));
    }

    @OnClick(R.id.up_camera)
    void clickUpCamera() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.MOVE_UP);
        sendBroadcast(new Intent(Constants.MOVE_UP));
    }

    @OnClick(R.id.left_camera)
    void clickLeftCamera() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.MOVE_LEFT);
        sendBroadcast(new Intent(Constants.MOVE_LEFT));
    }

    @OnClick(R.id.down_camera)
    void clickDownCamera() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.MOVE_DOWN);
        sendBroadcast(new Intent(Constants.MOVE_DOWN));
    }

    @OnClick(R.id.right_camera)
    void clickRightCamera() {
//        args.putSerializable(Constants.COMMAND, TypeCommand.MOVE_RIGHT);
        sendBroadcast(new Intent(Constants.MOVE_RIGHT));
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.COMMAND_UP);
        filter.addAction(Constants.COMMAND_LEFT);
        filter.addAction(Constants.COMMAND_DOWN);
        filter.addAction(Constants.COMMAND_RIGHT);
        filter.addAction(Constants.COMMAND_MOVE_UP);
        filter.addAction(Constants.COMMAND_MOVE_LEFT);
        filter.addAction(Constants.COMMAND_MOVE_DOWN);
        filter.addAction(Constants.COMMAND_MOVE_RIGHT);
        registerReceiver(commandReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(commandReceiver);
    }

    BroadcastReceiver commandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: " + intent.getAction());
            switch (intent.getAction()) {
                case Constants.COMMAND_UP:
                    sendCommand("w");
                    break;
                case Constants.COMMAND_LEFT:
                    sendCommand("a");
                    break;
                case Constants.COMMAND_DOWN:
                    sendCommand("s");
                    break;
                case Constants.COMMAND_RIGHT:
                    sendCommand("d");
                    break;
                case Constants.COMMAND_MOVE_UP:
                    sendCommand("i");
                    break;
                case Constants.COMMAND_MOVE_LEFT:
                    sendCommand("j");
                    break;
                case Constants.COMMAND_MOVE_DOWN:
                    sendCommand("k");
                    break;
                case Constants.COMMAND_MOVE_RIGHT:
                    sendCommand("l");
                    break;
            }
        }
    };

    /**
     * @param msg
     */
    private void sendCommand(String msg) {
        mBluetoothController.write(msg.getBytes());
        tvContent.append("Me: " + msg + "\n");
    }


}