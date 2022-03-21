package com.teachersspace.data;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.teachersspace.models.Message;

import java.util.List;

public class MessageRepository extends SingleFirebaseCollectionRepository {
    private static final String TAG = "MessageRepository";

    @Override
    protected String getCollectionPath() {
        return "messages";
    }

    /**
     * listens to messages for a n-party CONVERSATION
     * useful for providing realtime updates to a single contact view
     * https://firebase.google.com/docs/firestore/query-data/listen#listen_to_multiple_documents_in_a_collection
     * @param conversationUsers list of user UIDs in the conversation
     */
    public void listenMessageByConversation(List<String> conversationUsers, EventListener<QuerySnapshot> listenerCallback) {
        /* TODO: verify if the limit of 10 applies
        *   https://firebase.google.com/docs/firestore/query-data/queries#in_not-in_and_array-contains-any
        *   might not be because up to "10 equality clauses on the SAME field"
        */
        getCollection()
                .whereIn("users.from", conversationUsers)
                .whereIn("users.to", conversationUsers) // TODO: verify if querying nested fields like this is correct
                .addSnapshotListener(listenerCallback);
    }

    /**
     * creates a message in firestore
     */
    public void postMessage(Message message, OnFailureListener onFailureListener) {
        getCollection()
                .document(message.getUid())
                .set(message)
                .addOnSuccessListener(unused -> {
                    Log.d(TAG, "successfully created message document");
                    // only update users upon success
                    String fromUserUid = message.getFromUserUid();
                    String toUserUid = message.getToUserUid();
                    notifyUsers(fromUserUid, toUserUid);
                })
                .addOnFailureListener(onFailureListener);
    }

    /**
     * edits the body of a specific message in firestore
     * @param messageUid uid/document ID of the current message
     * @param newBodyText new b
     */
    public void editMessage(String messageUid, String newBodyText) {
        // TODO: low priority, do if timeline permits
    }

    /**
     * updates the FROM and TO users' "contacts" field in firestore
     * with the CURRENT DATETIME
     * actually, should call methods from UserRepository since it concerns users
     * @param fromUserUid user UID of the user who sent the message
     * @param toUserUid user UID of the user who is to receive the message
     */
    private void notifyUsers(String fromUserUid, String toUserUid) {
        // TODO
    }
}
