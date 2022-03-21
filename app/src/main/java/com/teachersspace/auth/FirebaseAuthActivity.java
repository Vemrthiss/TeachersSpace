package com.teachersspace.auth;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

// hopefully gradle builds next time without this direct import...
import com.google.firebase.auth.FirebaseUserMetadata;
import com.teachersspace.R;
import com.teachersspace.models.User;

public class FirebaseAuthActivity extends AppCompatActivity {
    private static final String TAG = "FirebaseAuthActivity";
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_auth);
        this.sessionManager = new SessionManager(this);
        this.createSignInIntent();
    }

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                @Override
                public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                    onSignInResult(result);
                }
            }
    );

    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .build();
        signInLauncher.launch(signInIntent);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            // Successfully signed in

            // here I am attempting to check if the user is a new user on sign in
            // might not be logging because of async operations, not sure how to deal with them
            // ideally, I want to listen to the user creation event, and categorise the NEW users
            // separately since custom metadata is not supported for firebase auth users
            // another way is to use Firebase functions, but it would not know how to categorise users
            // due to different runtime environment

//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            FirebaseUserMetadata userMetadata = user.getMetadata();
//            if (userMetadata.getCreationTimestamp() == userMetadata.getLastSignInTimestamp()) {
//                Log.d(TAG, "a new user!");
//            }

            this.sessionManager.onLogin(this);

            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
        }
    }

    public static void signOut(Context context, OnCompleteListener<Void> callback) {
        AuthUI.getInstance()
                .signOut(context)
                .addOnCompleteListener(callback);
    }
}