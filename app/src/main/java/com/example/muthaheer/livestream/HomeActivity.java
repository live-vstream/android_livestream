package com.example.muthaheer.livestream;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.muthaheer.livestream.app.AppConfig;
import com.example.muthaheer.livestream.app.AppController;
import com.example.muthaheer.livestream.fragments.AboutUsFragment;
import com.example.muthaheer.livestream.fragments.CameraPreviewFragment;
import com.example.muthaheer.livestream.fragments.ContactUsFragment;
import com.example.muthaheer.livestream.fragments.CreateStreamFragment;
import com.example.muthaheer.livestream.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,CreateStreamFragment.OnFragmentInteractionListener {

    private static final String TAG = "Home Activity";
    String mAuthToken;
    AppController mApp;

    ProgressDialog requestTokenProgress;

    String mFileName;


    Toolbar toolbar;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawer;

    private SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mApp = (AppController) getApplicationContext();
        sm = mApp.getSessionManager();

        mAuthToken = sm.getAuthToken();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_signout) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle("Live Stream")
                    .setMessage("Are you sure you want to logout?");
            dialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logoutUser();
                }
            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment=null;
        String actionbarTitle = "Home";

        switch(id){
            case R.id.nav_home :
                actionbarTitle = "Home";
                break;
            case R.id.nav_create_stream :
                fragment=new CreateStreamFragment();
                actionbarTitle = "Create Stream";
                break;
            case R.id.nav_about_us :
                fragment=new AboutUsFragment();
                actionbarTitle = "About Us";
                Log.e(TAG, "Selected : " + id);
                break;
            case R.id.nav_contact_us :
                fragment =new ContactUsFragment();
                actionbarTitle = "Contact Us";
                Log.e(TAG, "Selected : " + id);
                break;
        }

        if(fragment != null && (id == R.id.nav_about_us || id == R.id.nav_contact_us || id==R.id.nav_create_stream)){
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.fragment_content, fragment)
                    .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }


        updateActionbarTitle(actionbarTitle);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void updateActionbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onFragmentInteraction(final String streamName) {


        requestTokenProgress = new ProgressDialog(HomeActivity.this, ProgressDialog.STYLE_SPINNER);
        requestTokenProgress.setMessage("Requesting Token for " + streamName);
        requestTokenProgress.setCancelable(false);
        requestTokenProgress.show();

        createStream(streamName);

    }

    public void displayTokenDialog(final String streamToken) {

        /* TODO: after clicking on 'SHARE', the dialog shouldn't be closed. May be
         * something other than AlertDialog
         */


        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
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
                                .replace(R.id.fragment_content, CameraPreviewFragment.newInstance())
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

    private void logoutUser() {
        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Logging out...");
        progressDialog.setTitle("Please wait");
        progressDialog.show();
        sm.setLogin(false);

        progressDialog.dismiss();
        Intent i = new Intent(getApplicationContext(),SignupActivity.class);
        startActivity(i);
        Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_LONG).show();
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
                        error.printStackTrace();

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

        req.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        AppController.getInstance().addToRequestQueue(req, "token_req");


    }


}
