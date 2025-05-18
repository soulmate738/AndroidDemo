package com.example.sparkchaindemo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.sparkchaindemo.R;

public class XiaoXiFragment extends Fragment {
    private String TAG  = "XiaoXiFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_xiao_xi, container, false);
    }

    private void initListener() {

    }


}