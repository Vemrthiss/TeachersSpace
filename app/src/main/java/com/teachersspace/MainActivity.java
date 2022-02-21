package com.teachersspace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.teachersspace.teacher.TeacherActivity;

public class MainActivity extends AppCompatActivity {
    private Button teacherActivityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.teacherActivityButton = findViewById(R.id.navigate_main_teacher);
        this.teacherActivityButton.setOnClickListener(navigateToTeacherActivity());
    }

    private View.OnClickListener navigateToTeacherActivity() {
        // this in lambda expressions are based on enclosing scope
        return v -> {
            Intent i = new Intent(this, TeacherActivity.class);
            startActivity(i);
        };
    }
}