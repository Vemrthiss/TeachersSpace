package com.teachersspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.teachersspace.auth.FirebaseAuthActivity;
import com.teachersspace.auth.SessionManager;
import com.teachersspace.contacts.ContactsFragment;
import com.teachersspace.data.UserRepository;
import com.teachersspace.models.User;
import com.teachersspace.parent.ParentActivity;
import com.teachersspace.settings.SettingsFragment;
import com.teachersspace.student.StudentActivity;
import com.teachersspace.teacher.TeacherActivity;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private SessionManager sessionManager;
    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "main activity onCreate hook was called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.sessionManager = new SessionManager(this);

        User user = this.sessionManager.getCurrentUser();
        if (SessionManager.getFirebaseLoginInfo() == null || user == null) {
            Log.d(TAG, "no login info");
            Button loginButton = findViewById(R.id.login_button_main);
            loginButton.setOnClickListener(view -> {
                // no firebase login, make users login through firebase
                Intent activateLoginIntent = new Intent(this, FirebaseAuthActivity.class);
                startActivity(activateLoginIntent);
            });
        } else {
            navigateToActivity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigateToActivity();
    }

    private void navigateToActivity(){
        User user = this.sessionManager.getCurrentUser();
        if (user == null) {
            return;
        }
        User.UserType userType = user.getUserType();
        if (userType == User.UserType.TEACHER) {
            startActivity(new Intent(this, TeacherActivity.class));
        } else if (userType == User.UserType.PARENT) {
            startActivity(new Intent(this, ParentActivity.class));
        } else if (userType == User.UserType.STUDENT) {
            startActivity(new Intent(this, StudentActivity.class));
        }
    }
}