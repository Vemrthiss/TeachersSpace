package com.teachersspace.models;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class Message {
    private String uid;
    public String getUid() {
        return this.uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    private String body;
    public String getBody() {
        return this.body;
    }
    public void setBody(String bodyText) {
        this.body = bodyText;
    }

    private Map<String, String> users;
    public Map<String, String> getUsers() {
        return this.users;
    }
    public void setUsers(Map<String, String> users) {
        this.users = users;
    }
    public String getFromUserUid() throws NoSuchFieldError {
        if (!this.getUsers().containsKey("from")) {
            throw new NoSuchFieldError("users does not have a FROM field. Please check validity of document in firebase");
        } else {
            return this.getUsers().get("from");
        }
    }
    public String getToUserUid() {
        if (!this.getUsers().containsKey("to")) {
            throw new NoSuchFieldError("users does not have a TO field. Please check validity of document in firebase");
        } else {
            return this.getUsers().get("to");
        }
    }

    private Date sent;
    public Date getSent() {
        return this.sent;
    }
    public void setSent(Date sentDateTime) {
        this.sent = sentDateTime;
    }

    private Date edited;
    public Date getEdited() {
        return this.edited;
    }
    public void setEdited(Date editedDateTime) {
        this.edited = editedDateTime;
    }

    public Message(String body, Map<String, String> users, Date sent, Date edited) {
        this(body);
        setUsers(users);
        setSent(sent);
        setEdited(edited);
    }
    public Message(String body) {
        String randomUid = UUID.randomUUID().toString(); // random uuid as message uid/document id in firestore
        setUid(randomUid);
        setBody(body);
    }
    public Message() {}

    public String serialise() {
        return new Gson().toJson(this);
    }

    public static Message deserialise(String messageSerialised) {
        return new Gson().fromJson(messageSerialised, Message.class);
    }

    @NonNull
    @Override
    public String toString() {
        return "Message{" +
                "uid='" + uid + '\'' +
                ", body='" + body + '\'' +
                ", users=" + users.toString() +
                ", sent=" + sent +
                ", edited=" + edited +
                '}';
    }
}
