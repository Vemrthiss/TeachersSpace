package com.teachersspace.helpers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.teachersspace.auth.SessionManager;
import com.teachersspace.data.UserRepository;
import com.teachersspace.models.User;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TimePickerFragment";
    private OfficeHourType pickerType;
    private final UserRepository userRepository = new UserRepository();
    private SessionManager sessionManager;

    public enum OfficeHourType {
        START, END
    }

    public TimePickerFragment(OfficeHourType pickerType, Context context) {
        this.sessionManager = new SessionManager(context);
        this.pickerType = pickerType;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(
                getActivity(),
                this,
                hour,
                minute,
                DateFormat.is24HourFormat(getActivity())
        );
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
        // time has been chosen by user
        User user = this.sessionManager.getCurrentUser();
        String userUid = user.getUid();
        User.UserType userType = user.getUserType();
        if (userType == User.UserType.TEACHER) {
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            calendar.set(Calendar.SECOND, 0);
            Date date = calendar.getTime();
            if (pickerType == OfficeHourType.START) {
                userRepository.updateStartOfficeHours(userUid, date);
            } else {
                userRepository.updateEndOfficeHours(userUid, date);
            }
        }
    }
}
