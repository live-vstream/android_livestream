package com.example.muthaheer.livestream;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.muthaheer.livestream.app.AppConfig;
import com.example.muthaheer.livestream.app.AppController;
import com.example.muthaheer.livestream.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_SignUp extends AppCompatActivity {
    private static final String TAG = "Activity_SignUp";
    TextView _loginLink;
    Button _signUpButton;
    EditText _inputName,_inputPassword;

    private SessionManager session;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__sign_up);

        _inputName=(EditText) findViewById(R.id.input_name);
        _inputPassword=(EditText) findViewById(R.id.input_password);

        _loginLink = (TextView) findViewById(R.id.link_login);
        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Activity_SignUp.this, Activity_signin.class);
                startActivity(i);

            }
        });

        session = new SessionManager(getApplicationContext());


        _signUpButton = (Button) findViewById(R.id.btn_signup);
        _signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        progressDialog = new ProgressDialog(Activity_SignUp.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String name = _inputName.getText().toString();
        final String password = _inputPassword.getText().toString();



        checkSignup(name, password);

    }




    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signUpButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _inputName.getText().toString();
        String password = _inputPassword.getText().toString();

        if (name.isEmpty()) {
            _inputName.setError("Enter a valid Name");
            valid = false;
        } else {
            _inputName.setError(null);
        }

        if (password.isEmpty() || password.length() < 4) {
            _inputPassword.setError("Password length must be > 4");
            valid = false;
        } else {
            _inputPassword.setError(null);
        }

        return valid;
    }

    private void checkSignup(final String name, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_signup";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SIGNUP, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "SignUp Response: " + response.toString());

                progressDialog.dismiss();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean success = jObj.getBoolean("success");

                    // Check for error node in json
                    if (success) {
                        session.setLogin(true);
                        String successMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(),
                                successMsg, Toast.LENGTH_LONG).show();

                        // Launch signin activity
                        Intent intent = new Intent(Activity_SignUp.this,
                                Activity_signin.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        // Error in Signup. Get the error message
                        String errorMsg = jObj.getString("msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
}
