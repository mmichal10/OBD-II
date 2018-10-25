package com.example.michal.inz.fragments;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.michal.inz.R;
import com.example.michal.inz.bt_connection.BluetoothConnectionService;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParamsFragment extends Fragment implements FragmentName {
    public final String TAG = "paramsFragment";

    private TextView mSpeedTv;
    private TextView mRpmTv;
    private TextView mL_KmTv;
    private TextView mTempTv;

    private IntentFilter mFilter;

    private StatsUpdateReceiver mStatsUpdateReceiver;

    public ParamsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_params, container, false);

        mSpeedTv = view.findViewById(R.id.tv_speed);
        mRpmTv = view.findViewById(R.id.tv_rpm);
        mL_KmTv = view.findViewById(R.id.tv_l_km);
        mTempTv = view.findViewById(R.id.tv_temperature);

        mFilter = new IntentFilter(BluetoothConnectionService.STATS_UPDATE_INTENT);

        mStatsUpdateReceiver = new StatsUpdateReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mStatsUpdateReceiver, mFilter);

        return view;
    }

    public class StatsUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothConnectionService.STATS_UPDATE_INTENT)) {
                updateStatsUI(intent);
            }
        }
    }

    private void updateStatsUI(Intent intent) {
        float temperature, fuel;
        int rpm, speed;

        try {
            temperature = intent.getFloatExtra(BluetoothConnectionService.TEMPERATURE_TAG, 0);
            mTempTv.setText(Float.toString(temperature));
        } catch (Exception e) {
            temperature = 0;
            mTempTv.setText(Float.toString(temperature));
            Log.d(TAG, "Failed retrieve temperature from car");
        }

        try {
            fuel = intent.getFloatExtra(BluetoothConnectionService.FUEL_TAG, 0);

        } catch (Exception e) {
            fuel = 0;
            Log.d(TAG, "Failed retrieve fuel from car");
        }

        try {
            rpm = intent.getIntExtra(BluetoothConnectionService.RPM_TAG, 0);
            mRpmTv.setText(Integer.toString(rpm));
        } catch (Exception e) {
            rpm = 0;
            mRpmTv.setText(Integer.toString(rpm));
            Log.d(TAG, "Failed retrieve fuel from car");
        }

        try {
            speed = intent.getIntExtra(BluetoothConnectionService.SPEED_TAG, 0);
            mSpeedTv.setText(Integer.toString(speed));
        } catch (Exception e) {
            speed = 0;
            mSpeedTv.setText(Integer.toString(speed));
            Log.d(TAG, "Failed retrieve speed from car");
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mStatsUpdateReceiver, mFilter);
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mStatsUpdateReceiver);
        super.onPause();
    }

    @Override
    public String getName() {
        return "Stats";
    }
}
