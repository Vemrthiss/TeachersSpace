package com.teachersspace;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.teachersspace.teacher.TeacherActivity;

public class MainActivity extends AppCompatActivity {
    private Button teacherActivityButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.teacherActivityButton = findViewById(R.id.navigate_main_teacher);
        this.teacherActivityButton.setOnClickListener(navigateToTeacherActivity());

        //for navbar
        //Initialize the bottom navigation view
        //create bottom navigation view object
        //https://developer.android.com/guide/navigation/navigation-ui#java
        NavController navController = Navigation.findNavController(this, R.id.nav_fragment);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        NavigationUI.setupWithNavController(bottomNavigationView, navController, appBarConfiguration);
    }

    private View.OnClickListener navigateToTeacherActivity() {
        // this in lambda expressions are based on enclosing scope
        return v -> {
            Intent i = new Intent(this, TeacherActivity.class);
            startActivity(i);
        };
    }
}