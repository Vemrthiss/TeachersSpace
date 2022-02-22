package com.teachersspace.teacher;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

// hopefully can remove
import com.google.firebase.auth.FirebaseUser;
import com.teachersspace.R;
import com.teachersspace.auth.FirebaseAuthActivity;
import com.teachersspace.auth.SessionManager;

public class TeacherActivity extends AppCompatActivity {
    private String TAG = "TeacherActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        // here trying to make sure that the user is logged in before proceeding,
        // else redirect to login
        // TODO: verify that it works
        FirebaseUser currentUser = SessionManager.getFirebaseLoginInfo();
        if (currentUser == null) {
            Intent activateLoginIntent = new Intent(this, FirebaseAuthActivity.class);
            startActivity(activateLoginIntent);
        }

        // TODO: request permission for microphone
    }
}