package com.example.muthaheer.livestream;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.muthaheer.livestream.app.AppController;
import com.example.muthaheer.livestream.helper.SessionManager;

public class LauncherActivity extends AppCompatActivity {

    SessionManager mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_launcher);

        mSession = ((AppController) getApplicationContext()).getSessionManager();

        Intent intent;
        if(mSession.isLoggedIn()) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, SignupActivity.class);
        }

        startActivity(intent);


    }
}
