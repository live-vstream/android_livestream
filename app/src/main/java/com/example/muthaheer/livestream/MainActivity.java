package com.example.muthaheer.livestream;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.muthaheer.livestream.app.AppConfig;
import com.example.muthaheer.livestream.app.AppController;
import com.example.muthaheer.livestream.fragments.CameraPreviewFragment;
import com.example.muthaheer.livestream.fragments.CreateStreamFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CreateStreamFragment.OnFragmentInteractionListener{

    FrameLayout mContentFrame;
    String mAuthToken;
    AppController mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApp = (AppController) getApplicationContext();

        mContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        mAuthToken = mApp.getSessionManager().getAuthToken();

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
                        try {
                            JSONObject resJson = new JSONObject(response);
                            boolean success = resJson.getBoolean("success");
                            if(success) {
                                String streamToken = resJson.getString("token");

                                displayTokenDialog(streamToken);
                            } else {
                                Toast.makeText(MainActivity.this, resJson.getString("msg"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
                headers.put("Authorization", mAuthToken);

                return headers;
            }

        };

        AppController.getInstance().addToRequestQueue(request, "token_req");






        }

    public void displayTokenDialog(final String streamToken) {

        /* TODO: after clicking on 'SHARE', the dialog shouldn't be closed. May be
         * something other than AlertDialog
         */
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Share this with Client")
                .setMessage("Token :  " + streamToken)
                .setCancelable(false)
                .setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // replace current fragment with CameraPreviewFragment
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_content_frame, CameraPreviewFragment.newInstance())
                                .commit();
                    }
                })
                .setNegativeButton("share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "To view my live broadcast, use this token " + streamToken);
                        startActivity(Intent.createChooser(sharingIntent,"Share using"));
                    }
                })

                .show();

    }

}
