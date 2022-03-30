package com.teachersspace.schedule;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Event
{

    //used for storing users' events
    public static ArrayList<Event> eventsList = new ArrayList<>();

    //tempList will be used in generateEventsInEventsList when needed

    //this is the same as eventsList, but it will hold only strings. This is to store data into FireStore;
    //Will be generated later again when needed.
    public static List<Map<String, String>> UserTextList = new ArrayList<>();

    //students will refer to this list for button operations
    public static ArrayList<Event> totalEventsList = new ArrayList<>();

    //to initialise the arrays one time only
    public static int initialise_counter = 0;


    public static ArrayList<Event> eventsForDate(LocalDate date)
    {
        ArrayList<Event> events = new ArrayList<>();
        for(Event event : eventsList)
        {
            if(event.getDate().equals(date))
                events.add(event);
        }
        return events;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time)
    {
        ArrayList<Event> events = new ArrayList<>();

        for(Event event : eventsList)
        {
            int eventHour = event.time.getHour();
            int cellHour = time.getHour();
            if(event.getDate().equals(date) && eventHour == cellHour)
                events.add(event);
        }
        return events;
    }

    private String name;
    private LocalDate date;
    private LocalTime time;
    private boolean isBooked;
    private String teacher_id;
    private String student_id;


    public Event(String name, LocalDate date, LocalTime time, boolean isBooked, String teacher_id ,String student_id)
    {
        this.name = name;
        this.date = date;
        this.time = time;
        this.isBooked = isBooked;
        this.teacher_id = teacher_id;
        this.student_id = student_id;


    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public void setDate(LocalDate date)
    {
        this.date = date;
    }

    public LocalTime getTime()
    {
        return time;
    }

    public void setTime(LocalTime time)
    {
        this.time = time;
    }

    public boolean getBookingStatus()
    {
        return isBooked;
    }

    public String getTeacher_id()
    {
        return teacher_id;
    }

    public String getStudent_id()
    {
        return student_id;
    }
}
