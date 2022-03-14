package com.teachersspace.teacher;

import android.os.Bundle;
import android.util.Log;

// hopefully can remove
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.teachersspace.R;
import com.teachersspace.communications.CallEnabledActivity;

public class TeacherActivity extends CallEnabledActivity {
    private final String TAG = "TeacherActivity";

    @Override
    public int getNavFragmentContainer() {
        return R.id.nav_host_fragment_teacher;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_teacher);
//        NavController navController = navHostFragment.getNavController();
    }
}