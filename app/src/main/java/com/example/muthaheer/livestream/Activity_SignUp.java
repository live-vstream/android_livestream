package com.example.muthaheer.livestream;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Activity_SignUp extends AppCompatActivity {

    TextView _sigin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__sign_up);

        _sigin=(TextView) findViewById(R.id.link_login);


        _sigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity_SignUp.this, Activity_signin.class);
                startActivity(i);
            }
        });
    }

}
