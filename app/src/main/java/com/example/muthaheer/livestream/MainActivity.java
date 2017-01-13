package com.example.muthaheer.livestream;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.muthaheer.livestream.app.AppConfig;
import com.example.muthaheer.livestream.app.AppController;
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
        getSupportFragmentManager().beginTransaction().add(R.id.main_content_frame, CreateStreamFragment.newInstance())
                .commit();

    }

    @Override
    public void onFragmentInteraction(final String streamName) {

        final ProgressDialog requestTokenProgress;
        requestTokenProgress = new ProgressDialog(MainActivity.this, ProgressDialog.STYLE_SPINNER);
        requestTokenProgress.setMessage("Requesting Token for " + streamName);
        requestTokenProgress.setCancelable(false);
        requestTokenProgress.show();

        // request for a token from server
        StringRequest request = new StringRequest(Request.Method.POST, AppConfig.URL_GETTOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        requestTokenProgress.hide();
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

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "JWT eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJfaWQiOiI1ODVlNmJkN2JkYmJkMjMzMTRlZDJkMmMiLCJuYW1lIjoia2FzaGlmIiwicGFzc3dvcmQiOiIkMmEkMTAkMGZrckZSVjc1VVoxS2Q5RjhNLmZQZU41aWRHRVFmaW1VQjkuaGVKWEJvc0dGM3Q1cWsyME8iLCJfX3YiOjB9.S6Op-lD8JdU9pAg5FhNk2lNM1KdATbl5xkdMo5NhS7Q");

                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(request, "token_req");


//        new AlertDialog.Builder(MainActivity.this)
//                .setTitle("Share this with Client")
//                .setMessage("Token :25632-25632 ")
//
//                .setCancelable(false)
//                .setPositiveButton("continue", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
////                        Intent i = new Intent(getApplicationContext(),Main2Activity.class);
////                        startActivity(i);
//                    }
//                })
//                .setNegativeButton("share via", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
//                        sharingIntent.setType("text/html");
//                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml("<p>This is the text shared.</p>"));
//                        startActivity(Intent.createChooser(sharingIntent,"Share using"));
//                    }
//                })
//
//                .show();



        }

}
