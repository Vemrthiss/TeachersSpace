package com.teachersspace.teacher;

import android.os.Bundle;

// hopefully can remove
import com.teachersspace.R;
import com.teachersspace.communications.CallEnabledActivity;

public class TeacherActivity extends CallEnabledActivity {
    private final String TAG = "TeacherActivity";

    @Override
    public int getCommunicationsFragmentContainer() {
        return R.id.teacher_communications_fragment_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);
    }
}