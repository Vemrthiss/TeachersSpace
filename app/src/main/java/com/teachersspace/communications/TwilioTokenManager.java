package com.teachersspace.communications;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.teachersspace.helpers.SharedPreferencesManager;

public class TwilioTokenManager extends SharedPreferencesManager {
    private static final String PREF_NAME = "TwilioToken";
    private static final String TWILIO_SERVER_URL= "http://twilio-server-6765-dev.twil.io/";
    private RequestQueue queue = Volley.newRequestQueue(this.context);

    public TwilioTokenManager(Context ctx) {
        super(ctx);
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

    public String generateNewAccessToken() {

    }
}
