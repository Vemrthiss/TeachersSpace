package com.teachersspace.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.teachersspace.helpers.SharedPreferencesManager;

public class SessionManager extends SharedPreferencesManager {
    private static final String PREF_NAME = "LoginSession";

    public SessionManager(Context ctx) {
        super(ctx);
    }

    @Override
    public String getPrefName() {
        return SessionManager.PREF_NAME;
    }

    public static FirebaseUser getFirebaseLoginInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user;
    }

//    public static String getUserIdToken() {
//        FirebaseUser user = getFirebaseLoginInfo();
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
