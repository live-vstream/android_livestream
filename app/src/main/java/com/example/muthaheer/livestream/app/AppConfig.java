package com.example.muthaheer.livestream.app;

import android.app.Application;
import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by muthaheer on 25/12/16.
 */

public class AppConfig extends Application {
    public static String URL_SERVER = "http://192.168.0.100:4300/api/user/";
    // Server user login url
    public static String URL_LOGIN = URL_SERVER + "authenticate";



}
