package com.teachersspace.data;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public abstract class SingleFirebaseCollectionRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CollectionReference getCollection() {
        return db.collection(getCollectionPath());
    }

    /**
     * @return the collection name in firestore
     */
    protected abstract String getCollectionPath();
}
