package com.teachersspace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.teachersspace.auth.FirebaseAuthActivity;
import com.teachersspace.auth.SessionManager;
import com.teachersspace.models.User;
import com.teachersspace.parent.ParentActivity;
import com.teachersspace.student.StudentActivity;
import com.teachersspace.teacher.TeacherActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button teacherActivityButton;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "main activity onCreate hook was called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.sessionManager = new SessionManager(this);

        this.teacherActivityButton = findViewById(R.id.navigate_main_teacher);
        this.teacherActivityButton.setOnClickListener(navigateToTeacherActivity());

        //for navbar
        //Initialize the bottom navigation view
        //create bottom navigation view object
        //https://developer.android.com/guide/navigation/navigation-ui#java
        NavController navController = Navigation.findNavController(this, R.id.nav_fragment);
        // will appBarConfiguration have a problem because the actionbar is hidden?
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController, appBarConfiguration);

        // redirect users to correct activity based on user type
        User user = this.sessionManager.getCurrentUser();
        if (SessionManager.getFirebaseLoginInfo() == null || user == null) {
            Log.d(TAG, "no login info");
            // no firebase login, make users login through firebase
            Intent activateLoginIntent = new Intent(this, FirebaseAuthActivity.class);
            startActivity(activateLoginIntent);
            // after login what happens? does it go back to the same activity that calls FirebaseAuthActivity?
            // suppose that it returns to this activity, does it call the onCreate hook again?
        } else {
            navigateToActivity();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        navigateToActivity();
    }

    private View.OnClickListener navigateToTeacherActivity() {
        // this in lambda expressions are based on enclosing scope
        return v -> {
            Intent i = new Intent(this, TeacherActivity.class);
            startActivity(i);
        };
    }

    private void navigateToActivity() {
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