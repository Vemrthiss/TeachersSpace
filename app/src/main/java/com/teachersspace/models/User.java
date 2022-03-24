package com.teachersspace.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    private String email;
    public String getEmail() {
        return this.email;
    }
    public void setEmail(String email) {
        this.email = email;
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

    private Map<String, Date> contacts = new HashMap<>();
    public Map<String, Date> getContacts() {
        return contacts;
    }
    public void setContacts(Map<String, Date> contacts) {
        this.contacts = contacts;
    }

    private Date officeStart;
    public Date getOfficeStart() {
        return officeStart;
    }
    public void setOfficeStart(Date officeStart) {
        this.officeStart = officeStart;
    }

    private Date officeEnd;
    public Date getOfficeEnd() {
        return officeEnd;
    }
    public void setOfficeEnd(Date officeEnd) {
        this.officeEnd = officeEnd;
    }

    private ArrayList<TimeSlot> timeSlots;
    public ArrayList<TimeSlot> getTimeSlots() {
        return timeSlots;
    }
    public void setTimeSlots(ArrayList<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public User(String uid, String name, String email, UserType userType, Map<String, Date> contacts, ArrayList<TimeSlot> timeSlots) {
        this(uid, name, email, userType);
        setContacts(contacts);
        setTimeSlots(timeSlots);
    }
    public User(String uid, String name, String email, UserType userType) {
        setUid(uid);
        setName(name);
        setEmail(email);
        setUserType(userType);
    }
    public User() {}

    public String serialise() {
        return new Gson().toJson(this);
    }

    public static User deserialise(String userSerialised) {
        return new Gson().fromJson(userSerialised, User.class);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                "name='" + name + '\'' +
                "email='" + email + '\'' +
                "contacts='" + contacts.toString() + '\'' +
                "officeStart='" + officeStart + '\'' +
                "officeEnd='" + officeEnd + '\'' +
                ", userType=" + userType +
                '}';
    }

    /**
     * Converts the object to a map representation
     * @deprecated not using this method anymore to put to firebase
     * @return a map of instance properties as keys and its values as the map values.
     */
    @Deprecated
    public Map<String, Object> convertToMap() {
        Map<String, Object> document = new HashMap<>();
        document.put("uid", getUid());
        document.put("name", getName());
        document.put("email", getEmail());
        document.put("userType", getUserType());
        document.put("contacts", getContacts());
        return document;
    }
}
