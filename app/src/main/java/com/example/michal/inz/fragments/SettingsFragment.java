package com.example.michal.inz.fragments;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.michal.inz.bt_connection.BluetoothConnectionService;
import com.example.michal.inz.DeviceListAdapter;
import com.example.michal.inz.R;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment implements FragmentName, AdapterView.OnItemClickListener {

    private static final String TAG = "SettingsFragment";

    public final String myUUID = "602da6e0-042e-49ac-93c2-db5d318b29d";
    public final UUID UUID = java.util.UUID.fromString(myUUID);

    private Switch mBtOnOffSwitch;
    private Button mShowPairedDevicesButton;
    private ListView mDevicesListView;
    private Button mConnectButton;

    private DeviceListAdapter mDeviceListAdapter;
    private ArrayList<BluetoothDevice> mBTDevices;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothDevice mElmAdapterDevice;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mBroadcastReceiver1);
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Bluetooth On/Off
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBtOnOffSwitch = view.findViewById(R.id.switch_bluetooth);
        mBtOnOffSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableDisableBT();
            }
        });

        // Choose device
        mShowPairedDevicesButton = view.findViewById(R.id.btn_show_devices);
        mShowPairedDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPairedDevices();
            }
        });

        mDevicesListView = view.findViewById(R.id.lv_devices);
        mDevicesListView.setOnItemClickListener(SettingsFragment.this);

        // Connect
        mConnectButton = view.findViewById(R.id.btn_connect);
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btConnect();
            }
        });

        updateBtSwitch();
        return view;
    }

    private void enableDisableBT(){
        Log.d(TAG, "onClick: enabling/disabling bluetooth.");

        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
            return;
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enable BT");
            mBluetoothAdapter.enable();
        } else {
            Log.d(TAG, "disable BT");
            mBluetoothAdapter.disable();
        }

        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mBroadcastReceiver1, BTIntent);

        updateBtSwitch();
    }

    private void showPairedDevices() {
        mBTDevices = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : pairedDevices)
            mBTDevices.add(device);
        mDeviceListAdapter = new DeviceListAdapter(getContext(), R.layout.device_adapter_view, mBTDevices);
        mDevicesListView.setAdapter(mDeviceListAdapter);
    }

    private void btConnect(){
        if (mElmAdapterDevice == null) {
            Toast.makeText(getContext(), "You didn't choose a device!",Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Starting connection thread");

        try {
            Intent intent = new Intent();
            intent.setClass(getContext(), BluetoothConnectionService.class);
            intent.putExtra("elmDevice", mElmAdapterDevice);
            intent.putExtra("UUID", myUUID);
            intent.putExtra("receiver", new ResultReceiver(null){
                @Override
                protected void onReceiveResult(int resultCode, Bundle resultData) {
                    Log.d(TAG, "received result");
                    Log.d(TAG, resultData.getString("resultReceiverTag"));
                }
            });
            getActivity().startService(intent);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            Toast.makeText(getContext(), "Socket inactive", Toast.LENGTH_LONG).show();
        }
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        mBtOnOffSwitch.setChecked(false);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        mBtOnOffSwitch.setChecked(true);
                        break;
                }
            }
        }
    };

    private void updateBtSwitch() {
        if (mBluetoothAdapter.isEnabled()) {
            mBtOnOffSwitch.setChecked(true);
        } else {
            mBtOnOffSwitch.setChecked(false);
        }
    }

    @Override
    public String getName() {
        return "Settings";
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "Choose device from list");
        String deviceName = mBTDevices.get(position).getName();
        String deviceAddress = mBTDevices.get(position).getAddress();

        Log.d(TAG, "deviceName = " + deviceName);
        Log.d(TAG, "deviceAddress = " + deviceAddress);

        /* This construction is written on purpose, as making sure this step can't fail.
           There is assumption, information about remote device can change
           and we want the have the latest one.
         */
         mElmAdapterDevice = mBTDevices.get(position);
         mElmAdapterDevice = mBluetoothAdapter.getRemoteDevice(mElmAdapterDevice.getAddress());
    }
}
