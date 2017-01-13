package com.example.muthaheer.livestream;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.muthaheer.livestream.app.AppConfig;
import com.example.muthaheer.livestream.fragments.CameraPreviewFragment;
import com.example.muthaheer.livestream.fragments.CreateStreamFragment;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CreateStreamFragment.OnFragmentInteractionListener{

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

    @Override
    public void onFragmentInteraction(final String streamName) {

        // request for a token from server
        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_GETTOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", streamName);

                return params;
            }

        };


        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Share this with Client")
                .setMessage("Token :25632-25632 ")

                .setCancelable(false)
                .setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent i = new Intent(getApplicationContext(),Main2Activity.class);
//                        startActivity(i);
                    }
                })
                .setNegativeButton("share via", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/html");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>This is the text shared.</p>"));
                        startActivity(Intent.createChooser(sharingIntent,"Share using"));
                    }
                })

                .show();



        }

}
