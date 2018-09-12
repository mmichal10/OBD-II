package com.example.michal.inz;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public final String appName = "INZ";
    public final String myUUID = "602da6e0-042e-49ac-93c2-db5d318b29d";
    public final UUID MY_UUID_INSECURE = java.util.UUID.fromString(myUUID);
    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;

    // Parts of UI
    Button btnEnableDisable_Discoverable;

    BluetoothConnectionService mBluetoothConnection;

    Button btnStartConnection;


    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();

    public DeviceListAdapter mDeviceListAdapter;

    BluetoothDevice mBTDevice;

    ListView lvNewDevices;

    Button fuelBtn;
    Button rpmBtn;
    Button temperatureBtn;

    // Create a BroadcastReceiver for ACTION_FOUND
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
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mMessageRevicer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String response = intent.getStringExtra("SERVER_RESPONSE");
            Log.d(TAG, "Server response in UT thread");
            TextView responseTV = (TextView)findViewById(R.id.serverResponseTV);
            responseTV.setText(response);
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mMessageRevicer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnONOFF = (Button) findViewById(R.id.BT_ON_OFF);
        btnEnableDisable_Discoverable = (Button) findViewById(R.id.discover_BTN);
        lvNewDevices = (ListView) findViewById(R.id.newDevices);
        mBTDevices = new ArrayList<>();

        btnStartConnection = (Button) findViewById(R.id.startConnectionBtn);

        fuelBtn = (Button)findViewById(R.id.fuelBtn) ;
        temperatureBtn = (Button)findViewById(R.id.temperatureBtn) ;
        rpmBtn = (Button)findViewById(R.id.rpmBtn);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        lvNewDevices.setOnItemClickListener(MainActivity.this);


        mBluetoothConnection = new BluetoothConnectionService(this);

        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });

        registerReceiver(mMessageRevicer, new IntentFilter("com.android.activity.SEND_DATA"));

        btnStartConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConnection();
            }
        });

        fuelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mBluetoothConnection.write(0);
                } catch (Exception e) {
                    //Toast.makeText(this, "Socket inactive", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Socket inactive", Toast.LENGTH_LONG).show();
                }
            }
        });

        rpmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mBluetoothConnection.write(1);
                } catch (Exception e) {
                    //Toast.makeText(this, "Socket inactive", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Socket inactive", Toast.LENGTH_LONG).show();
                }
            }
        });

        temperatureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mBluetoothConnection.write(2);
                } catch (Exception e) {
                    //Toast.makeText(this, "Socket inactive", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Socket inactive", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void startConnection(){
        startBTConnection(mBTDevice, MY_UUID_INSECURE);
    }

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        if (device == null) {
            Toast.makeText(this, "You didn't choose a device!", Toast.LENGTH_LONG).show();
            return;
        }

        Log.d(TAG, "Starting connection thread");

        mBluetoothConnection.startClient(device, uuid);
    }

    public void enableDisableBT(){
        if(mBluetoothAdapter == null){
            Log.d(TAG, "enableDisableBT: Does not have BT capabilities.");
        }
        if(!mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if(mBluetoothAdapter.isEnabled()){
            Log.d(TAG, "enableDisableBT: disabling BT.");
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }

    public void btnShowPairedDevices(View view) {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : pairedDevices)
            mBTDevices.add(device);
        mDeviceListAdapter = new DeviceListAdapter(this, R.layout.device_adapter_view, mBTDevices);
        lvNewDevices.setAdapter(mDeviceListAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        /* This construction is written on purpose, as making sure this step can't fail.
           There is assumption, information about remote device can change
           and we want the have the latest one.
         */
        mBTDevice = mBTDevices.get(i);
        mBTDevice = mBluetoothAdapter.getRemoteDevice(mBTDevice.getAddress());
    }
}
