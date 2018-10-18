package com.example.michal.inz;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.fuel.FuelLevelCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ConnectException;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService extends IntentService {
    public static boolean isRuuning;

    public final String TAG = "connectionService";

    private BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    BluetoothSocket mSocket;

    private InputStream mInStream;
    private OutputStream mOutStream;

    private BluetoothDevice mmDevice;
    private UUID deviceUUID;

    private Intent mIntent = new Intent();

    private long statsUpdateFrequency;

    public BluetoothConnectionService() {
        super("Bluetooth connection service");
        statsUpdateFrequency = 1000;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Preparing bluetooth connection service");

        mContext = getApplicationContext();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mmDevice = intent.getParcelableExtra("elmDevice");
        deviceUUID = UUID.fromString(intent.getStringExtra("UUID"));

        if (!establishConnection()) {
            cleanup();
            Log.d(TAG, "Preparing bluetooth connection failed");
            return;
        }

        Log.d(TAG, "Successfully prepared bluetooth connection service");

        getStatistics();
    }

    private void getStatistics() {
        Log.d(TAG, "Running BT statistics loop");

        while (isRuuning) {
            Log.d(TAG, "Getting stats in loop");

            updateStats();

            SystemClock.sleep(statsUpdateFrequency);
        }

        Log.d(TAG, "BT statistics loop terminated");
    }

    private void updateStats() {
        getTemperature();
        getFuel();
        getRpm();
    }

    private boolean establishConnection() {
        Log.d(TAG, "Attempt to establish connection");
        try {
            createSocket();
            connectSocket();
            openStreams();
        } catch (Exception e) {
            Log.d(TAG, "Failed to establish connection");
            cleanup();
            return false;
        }

        Log.d(TAG, "Connection established successfully");
        isRuuning = true;
        return true;
    }

    private void createSocket() throws Exception {
        Log.d(TAG, "Attempt to create socket");

        BluetoothSocket tmp = null;

        try {
            Method m = null;
            m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
            tmp = (BluetoothSocket) m.invoke(mmDevice, 1);
        } catch (Exception e) {
            Log.d(TAG, "Failed to create socket with!");
            e.printStackTrace();
            throw e;
        }

        Log.d(TAG, "Socket opened!");

        mSocket = tmp;
    }

    private void connectSocket() throws IOException {
        Log.d(TAG, "Attempt to connect to socket");

        try {
            mSocket.connect();
        } catch (IOException e) {
            Log.d(TAG, "Connecting to socket failed!");
            throw e;
        }

        Log.d(TAG, "Successfully connected to socket");
    }

    private void openStreams() throws IOException {
        Log.d(TAG, "Attempt to open streams");

        try {
            openOutStream();
            openInStream();
        } catch (ConnectException e) {
            Log.d(TAG, "Failed to open streams");
            throw e;
        }

        Log.d(TAG, "Successfully opened streams");
    }

    private void openOutStream() throws IOException {
        Log.d(TAG, "Attempt to open output stream");
        OutputStream tmpStream = null;

        try {
            tmpStream = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.d(TAG, "Failed to open output stream");
            throw e;
        }

        mOutStream = tmpStream;
        Log.d(TAG, "Successfully opened output stream");
    }

    private void openInStream() throws IOException {
        Log.d(TAG, "Attempt to open input stream");
        InputStream tmpStream = null;

        try {
            tmpStream = mSocket.getInputStream();
        } catch (IOException e) {
            Log.d(TAG, "Failed to open input stream");
            throw e;
        }

        mInStream = tmpStream;
        Log.d(TAG, "Successfully opened input stream");
    }

    private void cleanup() {
        isRuuning = false;
        try {
            mSocket.close();
            Log.d(TAG, "Socket closed");
            mOutStream.close();
            Log.d(TAG, "Output stream closed");
            mInStream.close();
            Log.d(TAG, "Input stream closed");
        } catch (Exception e) {}
    }

    @Override
    public void onDestroy() {
        cleanup();
        Log.d(TAG, "Terminating bluetooth connection service");
        super.onDestroy();
    }

    public void getTemperature() {
        EngineCoolantTemperatureCommand temp = new EngineCoolantTemperatureCommand();
        try {
            temp.run(mInStream, mOutStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mIntent.putExtra("SERVER_RESPONSE", String.valueOf(temp.getTemperature()));
        mIntent.setAction("com.android.activity.SEND_DATA");
        mContext.sendBroadcast(mIntent);
    }
    public void getRpm() {
        RPMCommand rpm = new RPMCommand();
        try {
            rpm.run(mInStream, mOutStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mIntent.putExtra("SERVER_RESPONSE", String.valueOf(rpm.getRPM()));
        mIntent.setAction("com.android.activity.SEND_DATA");
        mContext.sendBroadcast(mIntent);
    }
    public void getFuel() {
        FuelLevelCommand fuel = new FuelLevelCommand();
        try {
            fuel.run(mInStream, mOutStream);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mIntent.putExtra("SERVER_RESPONSE", String.valueOf(fuel.getFuelLevel()));
        mIntent.setAction("com.android.activity.SEND_DATA");
        mContext.sendBroadcast(mIntent);
    }

    private void write_raw(byte[] bytes) {
        String text = new String(bytes, Charset.defaultCharset());
        text = text.concat(text.endsWith("\r") ? "" : "\r");
        Log.d(TAG, "write: Writing to output stream: " + text);
        try {
            mOutStream.write(text.getBytes());
        } catch (IOException e) {
            Log.e(TAG, "write: Error writing: " + e.getMessage());
        }
    }
}
