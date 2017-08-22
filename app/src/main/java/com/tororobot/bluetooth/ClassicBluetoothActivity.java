package com.tororobot.bluetooth;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tororobot.R;
import com.tororobot.chat.ChatActivity;
import com.tororobot.service.CommandSocketService;
import com.tororobot.ui.activity.ContainerActivity;
import com.tororobot.util.Constants;
import com.tororobot.util.StateBluetooth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;

public class ClassicBluetoothActivity extends AppCompatActivity {

    private BluetoothController mBluetoothController;

    private Button btnScanAvaliabe, btnScan, btnOpen, btnStartServer;
    private TextView tvBTState;
    private ListView lvDevices;

    private List<String> mList;
    private BaseAdapter mFoundAdapter;
    private String mMacAddress;

    private static final String TAG = "LMBluetoothSdk";

    private BluetoothListener mListener = new BluetoothListener() {
        @Override
        public void onActionStateChanged(int preState, int state) {
            tvBTState.setText("Bluetooth state: " + StateBluetooth.transBtStateAsString(state));
        }

        @Override
        public void onActionDiscoveryStateChanged(String discoveryState) {
            if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                Toast.makeText(ClassicBluetoothActivity.this, "scanning!", Toast.LENGTH_SHORT).show();
            } else if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                Toast.makeText(ClassicBluetoothActivity.this, "scan finished!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onActionScanModeChanged(int preScanMode, int scanMode) {
            Log.d(TAG, "preScanMode:" + preScanMode + ", scanMode:" + scanMode);
        }

        @Override
        public void onBluetoothServiceStateChanged(int state) {
            Log.d(TAG, "bluetooth service state:" + state);
            if (state == State.STATE_CONNECTED) {
                Intent intent = new Intent(ClassicBluetoothActivity.this, ChatActivity.class);
                startActivityForResult(intent, 4);
            }
        }

        @Override
        public void onActionDeviceFound(BluetoothDevice device, short rssi) {
            mList.add(device.getName() + "@" + device.getAddress());
            mFoundAdapter.notifyDataSetChanged();
        }

        @Override
        public void onReadData(final BluetoothDevice device, final byte[] data) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classic_bluetooth);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Classic Bluetooth Sample");
        init();
    }

    private void initBT() {
        mBluetoothController = BluetoothController.getInstance().build(this);
        mBluetoothController.setAppUuid(UUID.fromString("fa87c0d0-afac-12de-8a39-0450200c9a66"));
        mBluetoothController.setBluetoothListener(mListener);
        tvBTState.setText("Bluetooth state: " + StateBluetooth.transBtStateAsString(mBluetoothController.getBluetoothState()));
    }

    private void init() {
        mList = new ArrayList<>();
        mFoundAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mList);

        btnScanAvaliabe = (Button) findViewById(R.id.btn_scan_avaliable);
        btnScan = (Button) findViewById(R.id.btn_scan);
        btnOpen = (Button) findViewById(R.id.btn_open_bt);
        btnStartServer = (Button) findViewById(R.id.btn_start_as_server);
        tvBTState = (TextView) findViewById(R.id.tv_bt_state);
        lvDevices = (ListView) findViewById(R.id.lv_devices);

        lvDevices.setAdapter(mFoundAdapter);
        initBT();

        btnScanAvaliabe.setOnClickListener(v -> mBluetoothController.setDiscoverable(60));

        btnScan.setOnClickListener(v -> {
            // for Android 6.0+, request dangerous permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(
                        ClassicBluetoothActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ClassicBluetoothActivity.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }

            mList.clear();
            mFoundAdapter.notifyDataSetChanged();
            if (!mBluetoothController.startScan()) {
                Toast.makeText(ClassicBluetoothActivity.this, "Start scan failed!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ClassicBluetoothActivity.this, "Start scan success!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        btnOpen.setOnClickListener(v -> {
            if (!mBluetoothController.isEnabled()) {
                mBluetoothController.openBluetooth();
            } else {
                Toast.makeText(ClassicBluetoothActivity.this, "Bluetooth has opened!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        btnStartServer.setOnClickListener(v -> {
            mBluetoothController.startAsServer();
            Toast.makeText(ClassicBluetoothActivity.this, "Start as a server!",
                    Toast.LENGTH_SHORT).show();
        });
        lvDevices.setOnItemClickListener((parent, view, position, id) -> {
            String itemStr = mList.get(position);
            mMacAddress = itemStr.substring(itemStr.length() - 17);

//            Intent i = new Intent();
//            i.setAction("com.tororobot.action.command.UPDATE");
//            i.putExtra("mac", mMacAddress);
//            sendBroadcast(i);


            CommandSocketService.actionUpdate(this, mMacAddress);

            Intent intent = new Intent(ClassicBluetoothActivity.this, ContainerActivity.class);
            intent.putExtra(Constants.ID_WHERE_FRAGMENT, Constants.ID_STREAMING);
//            intent.putExtra("name", itemStr.substring(0, itemStr.length() - 18));
//            intent.putExtra("mac", mMacAddress);
//
//
//            ManagerPrefs.putString("name", itemStr.substring(0, itemStr.length() - 18));
//            ManagerPrefs.putString("mac", mMacAddress);

            startActivityForResult(intent, 4);

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4) {
            if (mBluetoothController != null) {
                mBluetoothController.release();
            }
            mBluetoothController.build(this);
            mBluetoothController.setBluetoothListener(mListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothController.release();
    }
}
