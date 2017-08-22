package com.tororobot.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.tororobot.R;
import com.tororobot.bluetooth.bluetooh2.Bluetooh2Activity;
import com.tororobot.ui.activity.ContainerActivity;
import com.tororobot.util.Constants;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.palaima.smoothbluetooth.SmoothBluetooth;

public class MainFragment extends BaseFragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    private SmoothBluetooth mSmoothBluetooth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_init2, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSmoothBluetooth = new SmoothBluetooth(getActivity());
    }


    @OnClick(R.id.btn_bluetooth)
    void clickBluetooth() {
        if (!mSmoothBluetooth.isBluetoothEnabled()) {
            new MaterialDialog.Builder(getActivity())
                    .iconRes(R.mipmap.ic_launcher)
                    .title("Activar Bluetooth")
                    .content("Por favor, para enviar los comandos a su dispositivo Tororobot necesitar encender el Bluetooth")
                    .positiveText("Aceptar")
                    .negativeText("Rechazar")
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                            startActivity(new Intent().setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                            dialog.dismiss();
                        }
                    })
                    .show();
            return;
        }

//        getActivity().startActivity(new Intent(getActivity(), ContainerActivity.class).putExtra(Constants.ID_WHERE_FRAGMENT, Constants.ID_BLUETOOTH));
//        getActivity().startActivity(new Intent(getActivity(), ClassicBluetoothActivity.class));
        getActivity().startActivity(new Intent(getActivity(), Bluetooh2Activity.class));

    }


    @OnClick(R.id.btn_streaming)
    void clickStreaming() {

        getActivity().startActivity(new Intent(getActivity(), ContainerActivity.class).putExtra(Constants.ID_WHERE_FRAGMENT, Constants.ID_STREAMING));
    }
}