package com.teachersspace.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

// hopefully can remove
import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.teachersspace.R;
import com.teachersspace.auth.FirebaseAuthActivity;
import com.teachersspace.auth.SessionManager;
import com.teachersspace.communications.CallEnabledActivity;
import com.teachersspace.contacts.ContactsFragment;
import com.teachersspace.data.UserRepository;
import com.teachersspace.models.User;
import com.teachersspace.parent.ParentActivity;
import com.teachersspace.settings.SettingsFragment;
import com.teachersspace.student.StudentActivity;

public class TeacherActivity extends CallEnabledActivity implements BottomNavigationView.OnItemSelectedListener {
    private final String TAG = "TeacherActivity";

    MenuItem prevMenuItem;
    SettingsFragment settingsFragment = new SettingsFragment();
    ContactsFragment contactsFragment = new ContactsFragment();
    BottomNavigationView bottomNavigationView;

    private SessionManager sessionManager;
    private final UserRepository userRepository = new UserRepository();

    @Override
    public int getNavFragmentContainer() {
        return R.id.nav_host_fragment_teacher;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "main activity onCreate hook was called");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
//        this.sessionManager = new SessionManager(this);
//
//        navigateToActivity();
        bottomNavigationView = findViewById(R.id.bottomNavigationViewTeacher);

        bottomNavigationView.setOnItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.contactsFragmentMain);

        }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int teacherFragmentContainer = R.id.nav_host_fragment_teacher;
        switch (item.getItemId()) {
            case R.id.action_setting:
                //viewPager.setCurrentItem(1);
                getSupportFragmentManager().beginTransaction().replace(teacherFragmentContainer, settingsFragment).commit();
                return true;

            case R.id.action_contact:
                //viewPager.setCurrentItem(2);
                getSupportFragmentManager().beginTransaction().replace(teacherFragmentContainer, contactsFragment).commit();
                return true;
        }
        return false;
    }
//
}