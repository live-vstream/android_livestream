package com.example.muthaheer.livestream.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.muthaheer.livestream.helper.SessionManager;

/**
 * Created by muthaheer on 25/12/16.
 */

public class AppController extends Application{
    public static final String TAG = AppController.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static AppController mInstance;

    private SessionManager mSessionManager;
    private String mCurrentStreamToken;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mSessionManager = new SessionManager(this);
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public SessionManager getSessionManager() {
        return mSessionManager;
    }

    public String getCurrentStreamToken() {
        return mCurrentStreamToken;
    }

    public void setCurrentStreamToken(String mCurrentStreamToken) {
        this.mCurrentStreamToken = mCurrentStreamToken;
    }
}
