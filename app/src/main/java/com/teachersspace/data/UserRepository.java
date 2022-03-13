package com.teachersspace.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.teachersspace.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UserRepository extends SingleFirebaseCollectionRepository {
    private static final String TAG = "UserRepository";

    @Override
    protected String getCollectionPath() {
        return "users";
    }

    public void getUserByUid(String uid, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        getCollection()
                .whereEqualTo("uid", uid)
                .limit(1)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void createUser(User user, OnSuccessListener<Void> onSuccessListener, OnFailureListener onFailureListener) {
        getCollection()
                .document(user.getUid())
                .set(user)
                .addOnSuccessListener(onSuccessListener)
                .addOnFailureListener(onFailureListener);
    }

    public void watchSingleUser(String uid, EventListener<DocumentSnapshot> watchCallback) {
        DocumentReference userDocRef = getCollection().document(uid); // since user document ids are their UIDs
        userDocRef.addSnapshotListener(watchCallback);
    }

    /**
     * note that firestore has a max limit of 10 for whereIn clauses and similar
     * If uids.size() > 10, split array to chunks
     * @param uids array list of user UIDs, get user documents whose uid is inside this list
     * @param snapshotListener
     */
    public void getUsersbyUid(List<String> uids, EventListener<QuerySnapshot> snapshotListener) {
        // TODO: how to handle more than 10 contacts
        int size = uids.size();
        if (size < 1) {
            return;
        }
//        final int queryLimit = 10;
//        if (size > 10) {
//            List<List<String>> chunkedUids = new ArrayList<>();
//            for (int i = 0; i < size; i += queryLimit) {
//                chunkedUids.add(uids.subList(i, i + queryLimit));
//            }
//            return;
//        }
        getCollection()
                .whereIn("uid", uids)
                .addSnapshotListener(snapshotListener);
    }

//    public void addContact() {
//
//    }
}