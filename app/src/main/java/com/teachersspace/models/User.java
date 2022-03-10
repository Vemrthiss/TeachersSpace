package com.teachersspace.models;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

// POJO for converting documents into usable User objects
// private fields not preprended with underscore in fear of interfering with firestore deserialisation
// https://firebase.google.com/docs/firestore/query-data/get-data#custom_objects
public class User {
    private String uid;
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    private String name;
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public enum UserType {
        TEACHER, PARENT, STUDENT
    }
    private UserType userType;
    public UserType getUserType() {
        return userType;
    }
    public void setUserType(UserType uType) {
        this.userType = uType;
    }

    public User(String uid, String name, UserType userType) {
        setUid(uid);
        setName(name);
        setUserType(userType);
    }
    public User() {}

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                "name='" + name + '\'' +
                ", userType=" + userType +
                '}';
    }

    public Map<String, Object> convertToMap() {
        Map<String, Object> document = new HashMap<>();
        document.put("uid", getUid());
        document.put("name", getName());
        document.put("userType", getUserType());
        return document;
    }
}
