package com.teachersspace.communications;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.teachersspace.helpers.SharedPreferencesManager;

public class TwilioTokenManager extends SharedPreferencesManager {
    private static final String TAG = "TwilioTokenManager";
    private static final String PREF_NAME = "TwilioToken";
    private static final String TWILIO_SERVER_URL= "https://twilio-server-6765-dev.twil.io";
    private static String twilioIdentity = "defaultIdentity";
    private RequestQueue queue = Volley.newRequestQueue(this.context);

    public TwilioTokenManager(Context ctx, String uid) {
        super(ctx);
        twilioIdentity = uid;
        generateNewAccessToken();
    }

    @Override
    public String getPrefName() {
        return TwilioTokenManager.PREF_NAME;
    }

    public void setTwilioAccessToken(String jwtToken) {
        this.sessionObjectEditor.putString("twilioAccessToken", jwtToken);
        this.sessionObjectEditor.apply();
    }

    public String getTwilioAccessToken() {
        return this.sessionObject.getString("twilioAccessToken", "");
    }

    public void generateNewAccessToken() {
        String queryUrl = TWILIO_SERVER_URL + "/token?identity=" + twilioIdentity;
        StringRequest tokenRequest = new StringRequest(Request.Method.GET, queryUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String newToken = response.substring(1, response.length() - 1);
                setTwilioAccessToken(newToken);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "Token error is: " + error.getMessage());
            }
        });

        queue.add(tokenRequest);
    }
}
