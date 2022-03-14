package com.teachersspace.communications;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.teachersspace.data.UserRepository;
import com.teachersspace.models.User;

public class CommunicationsViewModel extends ViewModel {
    private static final String TAG = "CommunicationsViewModel";

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
}
