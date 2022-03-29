package com.teachersspace.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.teachersspace.data.UserRepository;
import com.teachersspace.models.User;

import java.util.Comparator;
import java.util.List;

public class SearchViewModel extends ViewModel {
    private static final String TAG = "SearchViewModel";
    private final UserRepository userRepository = new UserRepository();

    private MutableLiveData<List<User>> users;
    public LiveData<List<User>> getUsers(User user) {
        if (users == null) {
            users = new MutableLiveData<>();
            loadUsers(user);
        }
        return users;
    }
    public void loadUsers(User user) {
        OnCompleteListener<QuerySnapshot> onCompleteListener = task -> {
            if (task.isSuccessful()) {
                QuerySnapshot result = task.getResult();
                if (!result.isEmpty()) {
                    Log.i(TAG, "Loading all users");
                    List<User> userList = result.toObjects(User.class);
                    userList.sort(Comparator.comparing(User::getName));
                    users.setValue(userList);
                }
            }
        };
        userRepository.getOtherUsers(user.getUserType(), onCompleteListener);
    }

}
