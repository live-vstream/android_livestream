package com.example.muthaheer.livestream.fragments;


import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.example.muthaheer.livestream.R;
import com.example.muthaheer.livestream.app.AppController;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraPreviewFragment extends Fragment implements SurfaceHolder.Callback {

    private AppController mApp;
    private Camera mCamera;
    private SurfaceView mCameraView;

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mApp = (AppController) context.getApplicationContext();
    }

    @Override
    public void onResume() {
        super.onResume();

        // TODO: Request permission for camera from the user
        initCameraPreview();
    }

    private void initCameraPreview() {
        // Use rear camera by default for now
        // TODO: allow the user to choose the camera
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCameraView = (SurfaceView) getActivity().findViewById(R.id.surfaceView);
        mCameraView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera.setDisplayOrientation(90); // portrait view
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
