package com.example.muthaheer.livestream;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.muthaheer.livestream.fragments.CameraPreviewFragment;

public class MainActivity extends AppCompatActivity {

    FrameLayout mContentFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);

        // set the camera preview fragment as default for now
        getSupportFragmentManager().beginTransaction().add(R.id.main_content_frame, CameraPreviewFragment.newInstance())
                .commit();

    }
}
