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

    private TextView textView;


    public ParamsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_params, container, false);

        //textView = view.findViewById(R.id.txt_display);
        //textView.setText(getArguments().getString("message"));

        return view;
    }


    @Override
    public String getName() {
        return "Stats";
    }
}
