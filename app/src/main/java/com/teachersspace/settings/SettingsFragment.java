package com.teachersspace.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.teachersspace.MainActivity;
import com.teachersspace.R;
import com.teachersspace.auth.FirebaseAuthActivity;
import com.teachersspace.auth.SessionManager;

public class SettingsFragment extends Fragment {
    private SessionManager sessionManager;
    private Context context;
    private Button logoutButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        this.sessionManager = new SessionManager(context);
        logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(logout());
    }

    private View.OnClickListener logout() {
        OnCompleteListener<Void> logoutSuccess = task -> {
            this.sessionManager.clearCurrentUser();
            Log.d("FirebaseSignOutFunction", "signed out!");
            Intent activateMainActivityIntent = new Intent(getContext(), MainActivity.class);
            startActivity(activateMainActivityIntent);
        };

        return view -> {
            FirebaseAuthActivity.signOut(context, logoutSuccess);
        };
    }
}
