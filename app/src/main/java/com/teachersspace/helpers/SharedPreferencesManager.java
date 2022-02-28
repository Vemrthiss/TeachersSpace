package com.teachersspace.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class SharedPreferencesManager {
    public SharedPreferences sessionObject;

    public SharedPreferences.Editor sessionObjectEditor;

    public Context context;

    int PRIVATE_MODE = 0;

    public abstract String getPrefName();

    public SharedPreferencesManager(Context ctx) {
        this.context = ctx;
        this.sessionObject = context.getSharedPreferences(this.getPrefName(), PRIVATE_MODE);
        this.sessionObjectEditor = sessionObject.edit();
    }
}
