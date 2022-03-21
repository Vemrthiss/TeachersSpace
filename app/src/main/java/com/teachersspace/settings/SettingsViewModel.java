package com.teachersspace.settings;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.teachersspace.data.UserRepository;
import com.teachersspace.helpers.TimePickerFragment;
import com.teachersspace.models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SettingsViewModel extends ViewModel {
    private static final String TAG = "SettingsViewModel";
    private final UserRepository userRepository = new UserRepository();

    private MutableLiveData<Map<TimePickerFragment.OfficeHourType, Date>> officeHours;
    public LiveData<Map<TimePickerFragment.OfficeHourType, Date>> getOfficeHours(String uid) {
        if (officeHours == null) {
            officeHours = new MutableLiveData<>();
            loadOfficeHours(uid);
        }
        return officeHours;
    }

    private void loadOfficeHours(String uid) {
        EventListener<DocumentSnapshot> handler = (snapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                User updatedUser = snapshot.toObject(User.class);
                if (updatedUser != null) {
                    Map<TimePickerFragment.OfficeHourType, Date> newOfficeHours = new HashMap<>();
                    newOfficeHours.put(TimePickerFragment.OfficeHourType.START, updatedUser.getOfficeStart());
                    newOfficeHours.put(TimePickerFragment.OfficeHourType.END, updatedUser.getOfficeEnd());
                    officeHours.setValue(newOfficeHours);
                }
            } else {
                Log.d(TAG, "data null");
            }
        };
        userRepository.watchSingleUser(uid, handler);
    }
}
