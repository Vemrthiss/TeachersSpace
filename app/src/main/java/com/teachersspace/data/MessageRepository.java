package com.teachersspace.data;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.QuerySnapshot;
import com.teachersspace.models.Message;

import java.util.List;

public class MessageRepository extends SingleFirebaseCollectionRepository {
    private static final String TAG = "MessageRepository";
    private String chatUID;

    @Override
    protected String getCollectionPath() {
        return "chats";
    }

    public void setChatUID(String chatUID){
        this.chatUID = chatUID;
        Log.d(TAG, chatUID);
    }

    public DocumentReference getDocumentReference(){
        return getCollection().document(chatUID);
    }

    public void postMessage(Message message){
        getDocumentReference()
                .collection("messages")
                .add(message)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>(){
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()){
                            String[] uids = chatUID.split("-");
                            notifyUsers(uids[0], uids[1]);
                        }
                    }
                });
    }

    public void subscribeToMessageUpdates(EventListener<QuerySnapshot> listenerCallback){
        getDocumentReference()
                .collection("messages")
                .orderBy("timeSent")
                .addSnapshotListener(listenerCallback);
    }

    private void notifyUsers(String fromUserUid, String toUserUid) {
        UserRepository userRepo = new UserRepository();
        userRepo.addContact(fromUserUid, toUserUid);
        userRepo.addContact(toUserUid, fromUserUid);
    }
}
