package com.example.muthaheer.livestream.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.muthaheer.livestream.R;
import com.example.muthaheer.livestream.app.AppConfig;
import com.example.muthaheer.livestream.app.AppController;
import com.example.muthaheer.livestream.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateStreamFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateStreamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateStreamFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ImageButton mCreateButton;
    private EditText mStreamNameET;
    private String mStreamToken;

    private SessionManager sm;
    private AppController mApp;
    public CreateStreamFragment() {
        // Required empty public constructor
    }

    public static CreateStreamFragment newInstance() {
        CreateStreamFragment fragment = new CreateStreamFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (AppController) getActivity().getApplicationContext();
        sm = mApp.getSessionManager();
        if (getArguments() != null) {

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCreateButton = (ImageButton) view.findViewById(R.id.create_stream_btn);
        mStreamNameET = (EditText) view.findViewById(R.id.create_stream_name);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mListener != null) {
                    createStream();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_stream, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String streamName,String streamToken);
    }

    public String generateToken(){
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 6; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String timeStamp = new SimpleDateFormat("MMddHHmmss").format(Calendar.getInstance().getTime());
        String token=""+timeStamp+sb.toString();
        return(token);
    }

    public void createStream() {
        StringRequest req = new StringRequest(Request.Method.POST, AppConfig.URL_GETTOKEN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject resJson = new JSONObject(response);
                    String msg = resJson.getString("message");
                    JSONObject stream = resJson.getJSONObject("stream");
                    JSONArray tokens = stream.getJSONArray("tokens");
                    JSONObject firstToken = tokens.getJSONObject(0);
                    mStreamToken = firstToken.getString("value");



                    mListener.onFragmentInteraction(mStreamNameET.getText().toString(), mStreamToken);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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
                    jsonBody.put("title", mStreamNameET.getText().toString());
                    body = jsonBody.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return body.getBytes();
            }
        };

        mApp.addToRequestQueue(req, "stream_create");


    }
}
