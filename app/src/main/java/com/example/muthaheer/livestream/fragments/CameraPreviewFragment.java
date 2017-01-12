package com.example.muthaheer.livestream.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.muthaheer.livestream.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraPreviewFragment extends Fragment {


    public CameraPreviewFragment() {
        // Required empty public constructor
    }

    public static CameraPreviewFragment newInstance() {
        CameraPreviewFragment fragment = new CameraPreviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera_preview, container, false);
    }

}
