package com.example.muthaheer.livestream.app;

import android.app.Application;

/**
 * Created by muthaheer on 25/12/16.
 */

public class AppConfig extends Application {
    public static String URL_SERVER = "http://192.168.43.219:3000/api/";
    // Server user login url
    public static String URL_LOGIN = URL_SERVER + "auth/login";
    // Server user signup url
    public static String URL_SIGNUP = URL_SERVER + "auth/register";

    public static String URL_GETTOKEN = URL_SERVER + "stream";

    public static String RED5_SERVER_HOST = "192.168.43.219";



}
