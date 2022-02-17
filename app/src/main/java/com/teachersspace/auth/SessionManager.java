package com.teachersspace.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

public class SessionManager {
    public SharedPreferences sessionObject;

    public SharedPreferences.Editor sessionObjectEditor;

    private Context context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "LoginSession";

    public SessionManager(Context ctx) {
        this.context = ctx;
        this.sessionObject = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.sessionObjectEditor = sessionObject.edit();
    }

    public static FirebaseUser getFirebaseLoginInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user;
    }

//    public static String getUserIdToken() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            user.getIdToken(false)
//                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<GetTokenResult> task) {
//                            return task.getResult().getToken();
//                        }
//                    });
//        }
//    }
}
