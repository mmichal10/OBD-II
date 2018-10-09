package com.example.michal.inz;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
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

public class BluetoothConnectionService {
    public final UUID myUUID = UUID.fromString("602da6e0-042e-49ac-93c2-db5d318b29d6");
    public final String TAG = "connectionService";

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;

    private ConnectThread mConnectThread;
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;

    private ConnectedThread mConnectedThread;
    private Intent mIntent = new Intent();

    public BluetoothConnectionService(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        start();
    }


    private class ConnectThread extends Thread {
        BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started");
            mmDevice = device;
            deviceUUID = uuid;
        }

        public void run() {
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread");

            try {
                Log.d(TAG, "ConnectThread: Trying to crate InsecureRfcommSocket using UUID" + deviceUUID);
                Method m = mmDevice.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
                tmp = (BluetoothSocket) m.invoke(mmDevice, 1);
            } catch (Exception e) {
                Log.d(TAG, "Failed to create socket with!");
                return;
            }

            Log.d(TAG, "Socket opened!");

            mmSocket = tmp;

            try {
                Log.d(TAG, "Trying to connect to socket");
                mmSocket.connect();
            } catch (IOException e) {
                Log.d(TAG, "Connecting to socket failed!");
                try {
                    mmSocket.close();
                    Log.d(TAG, "Socket closed");
                } catch (IOException e1) {
                    Log.d(TAG, "Failed to close socket");
                }
            }
            Log.d(TAG, "Successfully connected to socket");

            try {
                connected(mmSocket, mmDevice);
            } catch (ConnectException e) {
                Log.d(TAG, "Couldn't open IO streams - closing the socket");
                try {
                    mmSocket.close();
                    Log.d(TAG, "Socket closed");
                } catch (IOException e1) {}
            }
        }

        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket");
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void start() {
        Log.d(TAG, "Attempt to establish connection");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

    }

    public void startClient(BluetoothDevice device, UUID uuid) {
        mProgressDialog = ProgressDialog.show(mContext, "Connecting Bluetooth,",
                "Please wait...", true);

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) throws ConnectException {
            Log.d(TAG, " starting");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {}

            try {
                Log.d(TAG, "Trying to get output stream");
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.d(TAG, "Failed to get output stream");
                throw new ConnectException("No output stream!");
            }

            try {
                Log.d(TAG, "Trying to get input stream");
                tmpIn = mmSocket.getInputStream();
            } catch (IOException e) {
                Log.d(TAG, "Failed to get input stream");
                throw new ConnectException("No input stream!");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

            Log.d(TAG, "output stream ready!");
        }

        public void run() {
            byte[] buffer = new byte[1024];

            int bytes;

            return;

            /*
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream " + incomingMessage);
                    mIntent.putExtra("SERVER_RESPONSE", incomingMessage);
                    mIntent.setAction("com.android.activity.SEND_DATA");
                    mContext.sendBroadcast(mIntent);
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading: " + e.getMessage());
                    break;
                }
            }
            */

        }

        public void getTemperature() {
            EngineCoolantTemperatureCommand temp = new EngineCoolantTemperatureCommand();
            try {
                temp.run(mmInStream, mmOutStream);
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
                rpm.run(mmInStream, mmOutStream);
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
                fuel.run(mmInStream, mmOutStream);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mIntent.putExtra("SERVER_RESPONSE", String.valueOf(fuel.getFuelLevel()));
            mIntent.setAction("com.android.activity.SEND_DATA");
            mContext.sendBroadcast(mIntent);
        }

        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            text = text.concat(text.endsWith("\r") ? "" : "\r");
            Log.d(TAG, "write: Writing to output stream: " + text);
            try {
                mmOutStream.write(text.getBytes());
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing: " + e.getMessage());
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) throws ConnectException {
        Log.d(TAG, "");

        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    public void write(int cmd_id) {
        switch (cmd_id) {
            case 0:
                mConnectedThread.getFuel();
                break;
            case 1:
                mConnectedThread.getRpm();
                break;
            case 2:
                mConnectedThread.getTemperature();
                break;
        }
    }
}
