package com.teachersspace.communications;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.ListenerRegistration;
import com.teachersspace.data.UserRepository;
import com.teachersspace.models.User;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CommunicationsViewModel extends ViewModel {
    private static final String TAG = "CommunicationsViewModel";
    private final UserRepository userRepository = new UserRepository();
    private ListenerRegistration watcher;

    private MutableLiveData<User> activeContact;
    public LiveData<User> getActiveContact() {
        if (activeContact == null) {
            activeContact = new MutableLiveData<>();
        }
        return activeContact;
    }
    public void setActiveContact(User newValue) {
        activeContact.setValue(newValue);
    }

    private MutableLiveData<Boolean> isOutsideOfficeHours;
    public LiveData<Boolean> getIsOutsideOfficeHours() {
        if (isOutsideOfficeHours == null) {
            isOutsideOfficeHours = new MutableLiveData<>();
        }
        return isOutsideOfficeHours;
    }
    public void setIsOutsideOfficeHours(boolean newValue) {
        isOutsideOfficeHours.setValue(newValue);
    }

    public void watchActiveContact() {
        User activeContactObj = activeContact.getValue();
        if (activeContactObj == null) {
            return;
        }

        EventListener<DocumentSnapshot> handler = (snapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }
            if (snapshot != null && snapshot.exists()) {
                User updatedUser = snapshot.toObject(User.class);
                if (updatedUser != null) {
                    activeContact.setValue(updatedUser);

                    if (updatedUser.getUserType() == User.UserType.TEACHER) {
                        if (updatedUser.getOfficeStart() != null && updatedUser.getOfficeEnd() != null) {
                            boolean result = computeIsOutsideOfficeHours(updatedUser.getOfficeStart(), updatedUser.getOfficeEnd());
                            setIsOutsideOfficeHours(result);
                        } else {
                            setIsOutsideOfficeHours(false);
                        }
                    } else {
                        setIsOutsideOfficeHours(false);
                    }
                }
            } else {
                Log.d(TAG, "data null");
            }
        };

        watcher = userRepository.watchSingleUser(activeContactObj.getUid(), handler);
    }
    public void deregisterListener() {
        if (watcher != null) {
            watcher.remove();
            watcher = null;
        }
    }

    public static boolean computeIsOutsideOfficeHours(Date start, Date end) {
        Calendar currentDate = new GregorianCalendar();
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(start);
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(end);
        int startHours = startTime.get(Calendar.HOUR_OF_DAY);
        int endHours = endTime.get(Calendar.HOUR_OF_DAY);
        int startMinutes = startTime.get(Calendar.MINUTE);
        int endMinutes = endTime.get(Calendar.MINUTE);
        int currentHours = currentDate.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = currentDate.get(Calendar.MINUTE);

        if (startHours < endHours) {
            // conventional range
            if (currentHours < startHours || currentHours > endHours) {
                return true;
            } else if (currentHours == startHours || currentHours == endHours) {
                return currentMinutes < startMinutes || currentMinutes > endMinutes;
            } else {
                return false;
            }
        } else {
            // e.g. 8 am to 1am
            if (currentHours < startHours && currentHours > endHours) {
                return true;
            } else if (currentHours == startHours || currentHours == endHours) {
                return currentMinutes < startMinutes || currentMinutes > endMinutes;
            } else {
                return false;
            }
        }
    }
}
