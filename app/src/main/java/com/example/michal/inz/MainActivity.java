package com.example.michal.inz;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michal.inz.fragments.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public final String appName = "INZ";
    public final String myUUID = "602da6e0-042e-49ac-93c2-db5d318b29d";
    public final UUID MY_UUID_INSECURE = java.util.UUID.fromString(myUUID);
    private static final String TAG = "MainActivity";

    BluetoothAdapter mBluetoothAdapter;

    BluetoothConnectionService mBluetoothConnection;

    Button btnStartConnection;


    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();


    BluetoothDevice mBTDevice;

    ListView lvNewDevices;

    Button fuelBtn;
    Button rpmBtn;
    Button temperatureBtn;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;


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
        unregisterReceiver(mMessageRevicer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.pager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);




        btnStartConnection = (Button) findViewById(R.id.startConnectionBtn);

        fuelBtn = (Button)findViewById(R.id.fuelBtn) ;
        temperatureBtn = (Button)findViewById(R.id.temperatureBtn) ;
        rpmBtn = (Button)findViewById(R.id.rpmBtn);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mBluetoothConnection = new BluetoothConnectionService(this);

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
