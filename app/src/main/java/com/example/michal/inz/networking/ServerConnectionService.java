package com.example.michal.inz.networking;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
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

    RequestQueue requestQueue;

    public ServerConnectionService() {
        super("Server connection service");
        Log.d(TAG, "Constructor");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "started");
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        requestQueue.add(stringRequest);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroy");
        super.onDestroy();
    }

    // Request a string response from the provided URL.
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
        public byte[] getBody() throws AuthFailureError {
            Stats stat = new Stats("SDhe", 10.f,
                    10.f, 10.f, 10.f, 10);
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

}
