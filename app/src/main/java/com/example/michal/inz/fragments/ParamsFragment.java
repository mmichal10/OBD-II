package com.example.michal.inz.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.michal.inz.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParamsFragment extends Fragment implements FragmentName {

    private TextView mSpeedTv;
    private TextView mRpmTv;
    private TextView mL_KmTv;
    private TextView mTempTv;


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

        //textView = view.findViewById(R.id.txt_display);
        //textView.setText(getArguments().getString("message"));


        return view;
    }


    @Override
    public String getName() {
        return "Stats";
    }
}
