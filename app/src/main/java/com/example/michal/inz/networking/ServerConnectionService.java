package com.example.michal.inz.networking;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;

public class ServerConnectionService extends IntentService {
    private static final String TAG = "ServerConnectionService";
    private static final String ADDRESS = "http://inzservv.azurewebsites.net/home/archive";

    private float temperature = -1, speed = -1, fuelLevel = -1;
    private double voltage = -1;
    private int rpm = -1;

    RequestQueue requestQueue;

    public ServerConnectionService() {
        super("Server connection service");
        Log.d(TAG, "Constructor");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "started");
        requestQueue = Volley.newRequestQueue(getApplicationContext());

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroy");
        super.onDestroy();
    }

    public class StatsUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothConnectionService.STATS_UPDATE_INTENT)) {
                getStats(intent);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, ADDRESS,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                Log.d(TAG, "Response is: " + response);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "That didn't work!");
                    }
                }) {
    @Override
    public String getBodyContentType() {
        return "application/json; charset=utf-8";
    }

    @Override
    public byte[] getBody(){
        Stats stat = new Stats("SDhe", temperature, speed, (float) voltage, fuelLevel, rpm);
        try {
            String s =  stat.toJson();
            Log.d(TAG, s);
            return  s.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
                };
                requestQueue.add(stringRequest);
            }
        }
    }

    private void getStats(Intent intent) {
        try {
            temperature = intent.getFloatExtra(BluetoothConnectionService.TEMPERATURE_TAG, 0);
        } catch (Exception e) {
            temperature = -1;
            Log.d(TAG, "Failed retrieve temperature from car");
        }

        try {
            fuelLevel = intent.getFloatExtra(BluetoothConnectionService.FUEL_TAG, 0);
        } catch (Exception e) {
            fuelLevel = -1;
            Log.d(TAG, "Failed retrieve fuel from car");
        }

        try {
            rpm = intent.getIntExtra(BluetoothConnectionService.RPM_TAG, 0);
        } catch (Exception e) {
            rpm = -1;
            Log.d(TAG, "Failed retrieve fuel from car");
        }

        try {
            speed = intent.getIntExtra(BluetoothConnectionService.SPEED_TAG, 0);
        } catch (Exception e) {
            speed = -1;
            Log.d(TAG, "Failed retrieve speed from car");
        }
    }

}
