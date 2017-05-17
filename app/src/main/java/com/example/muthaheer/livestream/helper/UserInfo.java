package com.example.muthaheer.livestream.helper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kashif on 17/5/17.
 */

public class UserInfo {
    private String firstName;
    private String lastName;

    public String getEmail() {
        return email;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    private String email;

    public UserInfo(JSONObject userJobj) {
        try {
            this.firstName = userJobj.getString("firstName");
            this.lastName = userJobj.getString("lastName");
            this.email = userJobj.getString("email");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
