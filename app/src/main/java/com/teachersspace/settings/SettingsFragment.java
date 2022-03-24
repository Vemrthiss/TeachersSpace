package com.teachersspace.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.teachersspace.MainActivity;
import com.teachersspace.R;
import com.teachersspace.auth.FirebaseAuthActivity;
import com.teachersspace.auth.SessionManager;
import com.teachersspace.helpers.TimeFormatter;
import com.teachersspace.helpers.TimePickerFragment;
import com.teachersspace.models.User;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";
    private SessionManager sessionManager;
    private Context context;
    private AlertDialog displayNameDialog;

    private SettingsViewModel vm;

    private class CurrentUserObserver implements Observer<User> {
        private Button setStartOfficeHoursButton;
        private Button setEndOfficeHoursButton;
        private Button setDisplayNameButton;

        public CurrentUserObserver(Button setDisplayNameButton, Button setStartOfficeHoursButton, Button setEndOfficeHoursButton) {
            this.setDisplayNameButton = setDisplayNameButton;
            this.setStartOfficeHoursButton = setStartOfficeHoursButton;
            this.setEndOfficeHoursButton = setEndOfficeHoursButton;
        }

        public CurrentUserObserver(Button setDisplayNameButton) {
            this.setDisplayNameButton = setDisplayNameButton;
        }

        @Override
        public void onChanged(User user) {
            Activity activity = requireActivity();
            final String displayNameButtonText = activity.getString(R.string.edit_display_name, user.getName());
            this.setDisplayNameButton.setText(displayNameButtonText);

            if (user.getUserType() == User.UserType.TEACHER) {
                Date startOfficeHours = user.getOfficeStart();
                Date endOfficeHours = user.getOfficeEnd();
                Calendar startCalendar = Calendar.getInstance();
                Calendar endCalendar = Calendar.getInstance();
                if (startOfficeHours != null) {
                    startCalendar.setTime(startOfficeHours);
                }
                if (endOfficeHours != null) {
                    endCalendar.setTime(endOfficeHours);
                }

                String startTiming = TimeFormatter.formatTime(startCalendar);
                String endTiming = TimeFormatter.formatTime(endCalendar);

                final String startOfficeHoursText = activity.getString(R.string.start_office_hours, startTiming);
                final String endOfficeHoursText = activity.getString(R.string.end_office_hours, endTiming);
                this.setStartOfficeHoursButton.setText(startOfficeHoursText);
                this.setEndOfficeHoursButton.setText(endOfficeHoursText);
            }
        }
    }

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

        Button logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(logout());

        Button setDisplayNameButton = view.findViewById(R.id.set_display_name);
        setDisplayNameButton.setOnClickListener(showEditDisplayNameDialog());

        User currentUser = this.sessionManager.getCurrentUser();
        String userUid = currentUser.getUid();
        vm = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        if (currentUser.getUserType() == User.UserType.TEACHER) {
            // if user is teacher, setup office hours settings
            Button setStartOfficeHoursButton = view.findViewById(R.id.set_start_office_hours);
            setStartOfficeHoursButton.setVisibility(View.VISIBLE);
            setStartOfficeHoursButton.setOnClickListener(showTimePicker(TimePickerFragment.OfficeHourType.START));
            Button setEndOfficeHoursButton = view.findViewById(R.id.set_end_office_hours);
            setEndOfficeHoursButton.setVisibility(View.VISIBLE);
            setEndOfficeHoursButton.setOnClickListener(showTimePicker(TimePickerFragment.OfficeHourType.END));

            vm.getCurrentUser(userUid)
                    .observe(
                            getViewLifecycleOwner(),
                            new CurrentUserObserver(setDisplayNameButton, setStartOfficeHoursButton, setEndOfficeHoursButton)
                    );
        } else {
            vm.getCurrentUser(userUid).observe(getViewLifecycleOwner(), new CurrentUserObserver(setDisplayNameButton));
        }
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

    private View.OnClickListener showTimePicker(TimePickerFragment.OfficeHourType pickerType) {
        return view -> {
            DialogFragment timepicker = new TimePickerFragment(pickerType, getContext());
            timepicker.show(getParentFragmentManager(), "timepicker-"+ pickerType);
        };
    }

    private View.OnClickListener showEditDisplayNameDialog() {
        return view -> {
            User currentUser = this.sessionManager.getCurrentUser();
            DialogInterface.OnClickListener editDisplayName = (dialogInterface, i) -> {
                EditText displayNameEditText = ((AlertDialog) dialogInterface).findViewById(R.id.edit_display_name);
                String newDisplayName = displayNameEditText.getText().toString();
                vm.updateDisplayName(currentUser.getUid(), newDisplayName);
                displayNameDialog.dismiss();
            };
            DialogInterface.OnClickListener dismissDialog = (dialogInterface, i) -> {
                if (displayNameDialog != null && displayNameDialog.isShowing()) {
                    displayNameDialog.dismiss();
                }
            };
            displayNameDialog = createDisplayNameEditDialog(editDisplayName, dismissDialog, getActivity());
            displayNameDialog.show();
        };
    }

    private static AlertDialog createDisplayNameEditDialog(final DialogInterface.OnClickListener submitDisplayNameChange, final DialogInterface.OnClickListener cancelListener ,Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Edit Display Name");
        alertDialogBuilder.setPositiveButton("Edit", submitDisplayNameChange);
        alertDialogBuilder.setNegativeButton("Cancel", cancelListener);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater li = LayoutInflater.from(activity);
        View dialogView = li.inflate(
                R.layout.dialog_edit_name,
                activity.findViewById(android.R.id.content),
                false);

        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();
    }
}
