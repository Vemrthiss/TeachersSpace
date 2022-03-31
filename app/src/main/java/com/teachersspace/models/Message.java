package com.teachersspace.models;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Date;
import java.util.UUID;

public class Message {
    private String uid;
    public String getUid() {
        return this.uid;
    }

    private String body;
    public String getBody() {
        return this.body;
    }

    private String senderUID;
    public String getSenderUID() {
        return this.senderUID;
    }

    private String receiverUID;
    public String getReceiverUID(){
        return this.receiverUID;
    }

    private Date timeSent;
    public Date getTimeSent() {
        return this.timeSent;
    }

    public Message(String body, String senderUID, Date timeSent) {
        this.uid = UUID.randomUUID().toString();
        this.body = body;
        this.senderUID = senderUID;
        //this.receiverUID = receiverUID;
        this.timeSent = timeSent;
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
                ", sender=" + senderUID +
                ", timeSent=" + timeSent +
                '}';
    }
}
