package com.teachersspace.settings;

import android.app.Activity;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.teachersspace.MainActivity;
import com.teachersspace.R;
import com.teachersspace.auth.FirebaseAuthActivity;
import com.teachersspace.auth.SessionManager;
import com.teachersspace.helpers.TimePickerFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment";
    private SessionManager sessionManager;
    private Context context;

    private SettingsViewModel vm;

    private class OfficeHoursObserver implements Observer<Map<TimePickerFragment.OfficeHourType, Date>> {
        private Button setStartOfficeHoursButton;
        private Button setEndOfficeHoursButton;

        public OfficeHoursObserver(Button setStartOfficeHoursButton, Button setEndOfficeHoursButton) {
            this.setStartOfficeHoursButton = setStartOfficeHoursButton;
            this.setEndOfficeHoursButton = setEndOfficeHoursButton;
        }

        @Override
        public void onChanged(Map<TimePickerFragment.OfficeHourType, Date> newOfficeHours) {
            Log.d(TAG, "office hours updated");

            Date startOfficeHours = newOfficeHours.get(TimePickerFragment.OfficeHourType.START);
            Date endOfficeHours = newOfficeHours.get(TimePickerFragment.OfficeHourType.END);
            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            if (startOfficeHours != null) {
                startCalendar.setTime(startOfficeHours);
            }
            if (endOfficeHours != null) {
                endCalendar.setTime(endOfficeHours);
            }
            String startTiming = formatDigits(Integer.toString(startCalendar.get(Calendar.HOUR_OF_DAY))) + ":" + formatDigits(Integer.toString(startCalendar.get(Calendar.SECOND)));
            String endTiming = formatDigits(Integer.toString(endCalendar.get(Calendar.HOUR_OF_DAY))) + ":" + formatDigits(Integer.toString(endCalendar.get(Calendar.SECOND)));

            Activity activity = requireActivity();
            final String startOfficeHoursText = activity.getString(R.string.start_office_hours, startTiming);
            final String endOfficeHoursText = activity.getString(R.string.end_office_hours, endTiming);
            this.setStartOfficeHoursButton.setText(startOfficeHoursText);
            this.setEndOfficeHoursButton.setText(endOfficeHoursText);
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
        Button setStartOfficeHoursButton = view.findViewById(R.id.set_start_office_hours);
        setStartOfficeHoursButton.setOnClickListener(showTimePicker(TimePickerFragment.OfficeHourType.START));
        Button setEndOfficeHoursButton = view.findViewById(R.id.set_end_office_hours);
        setEndOfficeHoursButton.setOnClickListener(showTimePicker(TimePickerFragment.OfficeHourType.END));

        vm = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);
        String userUid = this.sessionManager.getCurrentUser().getUid();
        vm.getOfficeHours(userUid).observe(getViewLifecycleOwner(), new OfficeHoursObserver(setStartOfficeHoursButton, setEndOfficeHoursButton));
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

    private String formatDigits(String s) {
        if (s.length() == 1) {
            return "0" + s;
        }
        return s;
    }
}
