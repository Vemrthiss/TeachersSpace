package com.teachersspace.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.teachersspace.R;

public class StudentActivity extends AppCompatActivity implements BottomNavigationView.OnItemSelectedListener {
    private static final String TAG = "StudentActivity";

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        bottomNavigationView = findViewById(R.id.bottomNavigationViewStudent);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.contactsFragmentMain);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_student);
        int navItemId = item.getItemId();
        if (navItemId == R.id.action_setting) {
            navController.navigate(R.id.navigate_settings_action_global);
            return true;
        } else if (navItemId == R.id.action_contact) {
            navController.navigate(R.id.navigate_contacts_action_global);
            return true;
        } else {
            return false;
        }
    }
}