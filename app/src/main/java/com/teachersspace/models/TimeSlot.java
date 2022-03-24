package com.teachersspace.models;

import java.util.HashMap;

public class TimeSlot extends HashMap<String, Object> {
//    private String uid;
//    public String getUid() {
//        return (String) this.get("uid");
//    }
//    public void setUid(String uid) {
//        this.put("uid", uid);
//    }

    private String date;
    public String getDate() {
        return (String) this.get("date");
    }
    public void setDate(String date) {
        this.put("date", date);
    }

    private String time;
    public String getTime() {
        return (String) this.get("time");
    }
    public void setTime(String time) {
        this.put("time", time);
    }

    private String event;
    public String getEvent() {
        return (String) this.get("event");
    }
    public void setEvent(String event) {
        this.put("event", event);
    }

    private String teacherUid;
    public String getTeacherUid() {
        return (String) this.get("teacherUid");
    }
    public void setTeacherUid(String uid) {
        this.put("teacherUid", uid);
    }

    private String studentUid;
    public String getStudentUid() {
        return (String) this.get("studentUid");
    }
    public void setStudentUid(String uid) {
        this.put("studentUid", uid);
    }

    private boolean isBooked;
    public boolean getIsBooked() {
        Boolean result = (Boolean) this.get("isBooked");
        if (result != null) {
            return result;
        } else {
            return false;
        }
    }
    public void setIsBooked(boolean isBooked) {
        this.put("isBooked", isBooked);
    }
}
