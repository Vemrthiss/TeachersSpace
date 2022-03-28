package com.teachersspace.search;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.teachersspace.data.UserRepository;
import com.teachersspace.models.User;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
                    users.setValue(userList);
                }
            }
        };
        userRepository.getOtherUsers(user.getUserType(), onCompleteListener);
    }

    // a "computed" property so to speak
    // that sorts the contacts based on last activity and is a list of user objects
//    private MediatorLiveData<List<User>> resultsSorted;
//    public LiveData<List<User>> getResultsSorted() {
//        if (resultsSorted == null) {
//            resultsSorted = new MediatorLiveData<>();
//            loadResultsSorted();
//        }
//        return resultsSorted;
//    }
//    private void loadResultsSorted() {
//        resultsSorted.addSource(users, contactsMap -> {
//            // get uid of all contacts from the hashmap and sort them
//            List<String> contactsUid = new ArrayList<String>(contactsMap);
//            Log.i(TAG, "contactsUid: " + contactsUid.toString());
//            // https://stackoverflow.com/questions/5245093/how-do-i-use-comparator-to-define-a-custom-sort-order
//            Comparator<User> lastActivityComparator = (user1, user2) -> {
//                Date lastActivity1 = contactsMap.get(user1.getUid());
//                Date lastActivity2 = contactsMap.get(user2.getUid());
//                return lastActivity2.compareTo(lastActivity1); // descending sort, most recent at front of list
//            };
//
//            // convert it into a list of user objects
//            EventListener<QuerySnapshot> snapshotListener = (snapshot, error) -> {
//                if (snapshot != null) {
//                    List<User> contactsObj = snapshot.toObjects(User.class);
//                    // sort it based on last activity
//                    Collections.sort(contactsObj, lastActivityComparator);
//                    contactsSorted.setValue(contactsObj);
//                }
//            };
//            userRepository.getUsersbyUid(contactsUid, snapshotListener);
//        });
//    }

    private MutableLiveData<Map<String, Date>> contacts;
    public LiveData<Map<String, Date>> getContacts(String uid) {
        if (contacts == null) {
            contacts = new MutableLiveData<>();
            loadContacts(uid);
        }
        return contacts;
    }
    private void loadContacts(String uid) {
        EventListener<DocumentSnapshot> handler = (snapshot, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                User updatedUser = snapshot.toObject(User.class);
                if (updatedUser != null) contacts.setValue(updatedUser.getContacts());
            } else {
                Log.d(TAG, "data null");
            }
        };
        userRepository.watchSingleUser(uid, handler);
    }

}
