package com.teachersspace.parent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.teachersspace.R;
import com.teachersspace.communications.CallEnabledActivity;

public class ParentActivity extends CallEnabledActivity implements BottomNavigationView.OnItemSelectedListener {
    private static final String TAG = "TeacherActivity";

    BottomNavigationView bottomNavigationView;

    @Override
    public int getNavFragmentContainer() {
        return R.id.nav_host_fragment_parent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        bottomNavigationView = findViewById(R.id.bottomNavigationViewParent);
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
        } else if (navItemId == R.id.action_contact) {
            navController.navigate(R.id.navigate_contacts_action_global);
            return true;
        } else {
            return false;
        }
    }
}