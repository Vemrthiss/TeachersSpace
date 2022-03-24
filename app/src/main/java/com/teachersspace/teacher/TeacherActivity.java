package com.teachersspace.teacher;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.teachersspace.R;
import com.teachersspace.communications.CallEnabledActivity;

public class TeacherActivity extends CallEnabledActivity implements BottomNavigationView.OnItemSelectedListener {
    private static final String TAG = "TeacherActivity";

    BottomNavigationView bottomNavigationView;

    @Override
    public int getNavFragmentContainer() {
        return R.id.nav_host_fragment_teacher;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        bottomNavigationView = findViewById(R.id.bottomNavigationViewTeacher);
        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.contactsFragmentMain);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        NavController navController = Navigation.findNavController(this, getNavFragmentContainer());
        int navItemId = item.getItemId();
        if (navItemId == R.id.action_setting) {
            navController.navigate(R.id.navigate_settings_action_global);
            return true;
        } else if (navItemId == R.id.action_schedule) {
            navController.navigate(R.id.navigate_schedule_action_global);
            return true;
        } else if (navItemId == R.id.action_contact) {
            navController.navigate(R.id.navigate_contacts_action_global);
            return true;
        } else {
            return false;
        }
    }
//
}