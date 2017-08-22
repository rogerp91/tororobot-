package com.tororobot.bluetooth.bluetooh2;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.tororobot.R;
import com.tororobot.TororobotAplications;
import com.tororobot.ui.fragment.BaseFragment;
import com.tororobot.util.ThreadConnected;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by bemonio on 7/16/2016.
 */

public class RemoteControlFragment extends BaseFragment {

    public static final String TAG = RemoteControlFragment.class.getSimpleName();

    private static Context contexts;
    ThreadConnected myThreadConnected;

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter;
    ArrayList<BluetoothDevice> pairedDeviceArrayList;

    @BindView(R.id.pairedlist)
    ListView listViewPairedDevice;

    @BindView(R.id.inputpane)
    LinearLayout inputPane;

    @BindView(R.id.inputCommand)
    EditText inputField;

    @BindView(R.id.btnSend)
    Button btnSend;

    @BindView(R.id.btnForward)
    Button btnForward;

    @BindView(R.id.btnBackward)
    Button btnBackward;

    @BindView(R.id.btnLeft)
    Button btnLeft;

    @BindView(R.id.btnRight)
    Button btnRigth;

    @BindView(R.id.btnDisconnect)
    Button btnDisconnect;

    ArrayAdapter<BluetoothDevice> pairedDeviceAdapter;
    private UUID myUUID;
    private final String UUID_STRING_WELL_KNOWN_SPP = "00001101-0000-1000-8000-00805F9B34FB";

    ThreadConnectBTdevice myThreadConnectBTdevice;

    protected View view;

    public static RemoteControlFragment newInstance() {
        RemoteControlFragment fragment = new RemoteControlFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_remote_control, container, false);
        ButterKnife.bind(this, view);

        btnSend.setOnClickListener(v -> {
            if (myThreadConnected != null) {
                byte[] bytesToSend = inputField.getText().toString().getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });

        btnForward.setOnClickListener(v -> {
            if (myThreadConnected != null) {
                byte[] bytesToSend = "w".toString().getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });

        btnBackward.setOnClickListener(v -> {
            if (myThreadConnected != null) {
                byte[] bytesToSend = "s".toString().getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });

        btnLeft.setOnClickListener(v -> {
            if (myThreadConnected != null) {
                byte[] bytesToSend = "a".toString().getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });

        btnRigth.setOnClickListener(v -> {
            if (myThreadConnected != null) {
                byte[] bytesToSend = "d".toString().getBytes();
                myThreadConnected.write(bytesToSend);
            }
        });

        btnDisconnect.setOnClickListener(v -> {
            if (myThreadConnected != null) {
                myThreadConnected.cancel();
                listViewPairedDevice.setVisibility(View.VISIBLE);
                inputPane.setVisibility(View.GONE);
            }
        });

        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            Toast.makeText(TororobotAplications.getContext(), "FEATURE_BLUETOOTH NOT support", Toast.LENGTH_LONG).show();
        }

        //using the well-known SPP UUID
        myUUID = UUID.fromString(UUID_STRING_WELL_KNOWN_SPP);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(super.getContext(), "Bluetooth is not supported on this hardware platform", Toast.LENGTH_LONG).show();
        }

        //Turn ON BlueTooth if it is OFF
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        setup();

        return view;
    }

    private void setup() {
        final Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            pairedDeviceArrayList = new ArrayList<>();
            ArrayList<String> bluetoothNameList = new ArrayList<String>();

            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceArrayList.add(device);
                bluetoothNameList.add(device.getName() + "\n" + device.getAddress());
            }

            pairedDeviceAdapter = new ArrayAdapter<>(TororobotAplications.getContext(), android.R.layout.simple_list_item_1, pairedDeviceArrayList);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(TororobotAplications.getContext(), android.R.layout.simple_list_item_1, bluetoothNameList);
            listViewPairedDevice.setAdapter(adapter);

            listViewPairedDevice.setOnItemClickListener((parent, view1, position, id) -> {
                BluetoothDevice device = pairedDeviceArrayList.get(position);
                Toast.makeText(TororobotAplications.getContext(),
                        "Name: " + device.getName() + "\n"
                                + "Address: " + device.getAddress() + "\n"
                                + "BondState: " + device.getBondState() + "\n"
                                + "BluetoothClass: " + device.getBluetoothClass() + "\n"
                                + "Class: " + device.getClass(),
                        Toast.LENGTH_LONG).show();

                //textStatus.setText("start ThreadConnectBTdevice");
                myThreadConnectBTdevice = new ThreadConnectBTdevice(device);
                myThreadConnectBTdevice.start();
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (myThreadConnectBTdevice != null) {
            myThreadConnectBTdevice.cancel();
        }
    }

    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket) {
        myThreadConnected = TororobotAplications.setThreadConnected(socket);
        myThreadConnected.start();
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }

    /*
    ThreadConnectBTdevice:
    Background Thread to handle BlueTooth connecting
    */
    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;

            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
                //textStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            boolean success = false;
            try {
                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();

                final String eMessage = e.getMessage();
                getActivity().runOnUiThread(() -> {
                    //textStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if (success) {
                //connect successful
                final String msgconnected = "connect successful:\n" + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                getActivity().runOnUiThread(() -> {
                    //textStatus.setText(msgconnected);

                    listViewPairedDevice.setVisibility(View.GONE);
                    inputPane.setVisibility(View.VISIBLE);
                });

                startThreadConnected(bluetoothSocket);
            } else {
                //fail
            }
        }

        public void cancel() {

            Toast.makeText(TororobotAplications.getContext(), "close bluetoothSocket", Toast.LENGTH_LONG).show();

            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }
}
