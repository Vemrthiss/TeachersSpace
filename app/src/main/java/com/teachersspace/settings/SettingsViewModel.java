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

    private MutableLiveData<User> currentUser;
    public LiveData<User> getCurrentUser(String uid) {
        if (currentUser == null) {
            currentUser = new MutableLiveData<>();
            watchCurrentUser(uid);
        }
        return currentUser;
    }
    public void watchCurrentUser(String uid) {
        EventListener<DocumentSnapshot> handler = (snapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                User updatedUser = snapshot.toObject(User.class);
                if (updatedUser != null) {
                    currentUser.setValue(updatedUser);
                }
            } else {
                Log.d(TAG, "data null");
            }
        };
        userRepository.watchSingleUser(uid, handler);
    }

    public void updateDisplayName(String uid, String newName) {
        userRepository.updateDisplayName(uid, newName);
    }
}
