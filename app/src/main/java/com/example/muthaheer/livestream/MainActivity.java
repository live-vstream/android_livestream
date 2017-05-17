package com.example.muthaheer.livestream;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.muthaheer.livestream.app.AppConfig;
import com.example.muthaheer.livestream.app.AppController;
import com.example.muthaheer.livestream.fragments.CameraPreviewFragment;
import com.example.muthaheer.livestream.fragments.CreateStreamFragment;
import com.example.muthaheer.livestream.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CreateStreamFragment.OnFragmentInteractionListener{

    FrameLayout mContentFrame;
    String mAuthToken;
    AppController mApp;
    private SessionManager sm;

    ProgressDialog requestTokenProgress;

    String mFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApp = (AppController) getApplicationContext();

        mContentFrame = (FrameLayout) findViewById(R.id.main_content_frame);
        mAuthToken = mApp.getSessionManager().getAuthToken();

        sm = mApp.getSessionManager();

        // set the camera preview fragment as default for now
        getSupportFragmentManager().beginTransaction().add(R.id.main_content_frame, CreateStreamFragment.newInstance())
                .commit();

    }

    @Override
    public void onFragmentInteraction(final String streamName) {


        requestTokenProgress = new ProgressDialog(MainActivity.this, ProgressDialog.STYLE_SPINNER);
        requestTokenProgress.setMessage("Requesting Token for " + streamName);
        requestTokenProgress.setCancelable(false);
        requestTokenProgress.show();

        createStream(streamName);

    }

    public void displayTokenDialog(final String streamToken) {

        /* TODO: after clicking on 'SHARE', the dialog shouldn't be closed. May be
         * something other than AlertDialog
         */


        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Share this with Client")
                .setMessage("Token :  " + streamToken)
                .setCancelable(false)
                .setPositiveButton("continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // replace current fragment with CameraPreviewFragment
                        mApp.setCurrentStreamToken(streamToken);
                        mApp.setCurrentStreamFileName(mFileName);
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

                        mApp.setCurrentStreamToken(streamToken);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.main_content_frame, CameraPreviewFragment.newInstance())
                                .commit();
                    }
                })
                .show();

    }

    // Ignore the back button for now
    // TODO: ask the user what his/her real intention is on pressing back button
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Do you really want to quit?")
                .setMessage("Click Yes to quit \n No to cancel" )
                .setCancelable(false)
                .setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    public void createStream(final String streamTitle) {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_GETTOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                requestTokenProgress.dismiss();

                try {
                    JSONObject resJson = new JSONObject(response);
                    String msg = resJson.getString("message");
                    JSONObject stream = resJson.getJSONObject("stream");
                    JSONArray tokens = stream.getJSONArray("tokens");
                    JSONObject firstToken = tokens.getJSONObject(0);
                    String mToken = firstToken.getString("value");
                    mFileName = stream.getString("filename");
                    displayTokenDialog(mToken);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        requestTokenProgress.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Create stream" , "volley response error: " + error.getLocalizedMessage());

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", sm.getAuthToken());
                //params.put("title", mStreamNameET.getText().toString());
                Log.d("Create stream", "Auth: " + sm.getAuthToken());

                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject jsonBody = new JSONObject();
                String body="";
                try {
                    jsonBody.put("title", streamTitle);
                    body = jsonBody.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return body.getBytes();
            }
        };

        AppController.getInstance().addToRequestQueue(req, "token_req");


    }
}
