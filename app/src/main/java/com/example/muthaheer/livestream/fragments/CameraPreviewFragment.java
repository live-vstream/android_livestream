package com.example.muthaheer.livestream.fragments;


import android.app.ActionBar;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.example.muthaheer.livestream.R;
import com.example.muthaheer.livestream.app.AppController;
import com.red5pro.streaming.R5Connection;
import com.red5pro.streaming.R5Stream;
import com.red5pro.streaming.R5StreamProtocol;
import com.red5pro.streaming.config.R5Configuration;
import com.red5pro.streaming.event.R5ConnectionEvent;
import com.red5pro.streaming.event.R5ConnectionListener;
import com.red5pro.streaming.source.R5Camera;
import com.red5pro.streaming.source.R5Microphone;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraPreviewFragment extends Fragment implements SurfaceHolder.Callback {

    private AppController mApp;
    private Camera mCamera;
    private SurfaceView mCameraView;
    private Button mStartButton;

    private R5Stream mStream;
    private R5Configuration mConfig;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConfig = new R5Configuration(R5StreamProtocol.RTSP, "192.168.0.101", 8554, "live", 1.0f);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mStartButton = (Button) getActivity().findViewById(R.id.publishButton);
        mCameraView = (SurfaceView) getActivity().findViewById(R.id.surfaceView);

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStream();

            }
        });
    }

    private void initCameraPreview() {
        // Use rear camera by default for now
        // TODO: allow the user to choose the camera
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        mCameraView.getHolder().addCallback(this);
    }

    private void startStream() {

        mStream = new R5Stream(new R5Connection(mConfig));
        mStream.setLogLevel(R5Stream.LOG_LEVEL_DEBUG);
        mStream.connection.addListener(new R5ConnectionListener() {
            @Override
            public void onConnectionEvent(R5ConnectionEvent event) {
                Log.d("publish","connection event code "+event.value()+"\n");
                switch(event.value()){
                    case 0://open
                        System.out.println("Connection Listener - Open");
                        break;
                    case 1://close
                        System.out.println("Connection Listener - Close");
                        break;
                    case 2://error
                        System.out.println("Connection Listener - Error: " + event.message);
                        break;

                }
            }
        });

        mStream.setListener(new R5ConnectionListener() {
            @Override
            public void onConnectionEvent(R5ConnectionEvent event) {
                switch (event) {
                    case CONNECTED:
                        System.out.println("Stream Listener - Connected");
                        break;
                    case DISCONNECTED:
                        System.out.println("Stream Listener - Disconnected");
                        System.out.println(event.message);
                        break;
                    case START_STREAMING:
                        System.out.println("Stream Listener - Started Streaming");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mStartButton.setText("STOP");
                                mStartButton.setBackgroundResource(R.color.colorButtonStop);
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "You are now live! Token: " + mApp.getCurrentStreamToken(), Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                        break;
                    case STOP_STREAMING:
                        System.out.println("Stream Listener - Stopped Streaming");
                        break;
                    case CLOSE:
                        System.out.println("Stream Listener - Close");
                        break;
                    case TIMEOUT:
                        System.out.println("Stream Listener - Timeout");
                        break;
                    case ERROR:
                        System.out.println("Stream Listener - Error: " + event.message);
                        break;
                }
            }
        });

        mStream.setView(mCameraView);
        R5Camera r5Camera = new R5Camera(mCamera, mCameraView.getWidth(), mCameraView.getHeight());
        R5Microphone r5Microphone = new R5Microphone();
        mStream.attachCamera(r5Camera);
        mStream.attachMic(r5Microphone);
        mStream.publish(mApp.getCurrentStreamToken(), R5Stream.RecordType.Live);
        mCamera.startPreview();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera.setDisplayOrientation(90); // portrait view
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
       // mCamera.startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
