package com.teachersspace.schedule;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.teachersspace.R;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourEvent>  {

    public HourAdapter(@NonNull Context context, List<HourEvent> hourEvents)
    {
        super(context, 0, hourEvents);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        HourEvent event = getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);
        }
        setHour(convertView, event.time);
        setEvents(convertView, event.events);
        return convertView;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setHour(View convertView, LocalTime time)
    {
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        timeTV.setText(CalendarUtils.formattedShortTime(time));
    }

    private void setEvents(View convertView, ArrayList<Event> events)
    {
        TextView event1 = convertView.findViewById(R.id.event1);

        if(events.size() == 0)
        {
            hideEvent(event1);
        }
        else if(events.size() == 1)
        {
            setEvent(event1, events.get(0));
        }
    }

    private void setEvent(TextView textView, Event event)
    {
        textView.setText(event.getName());
        textView.setVisibility(View.VISIBLE);
        if(event.getBookingStatus() == true){
            int color = Color.parseColor("#432f25");
            textView.setBackgroundColor(color);
        }
        else if (event.getBookingStatus() == false){
            int color = Color.parseColor("#B38B6D");
            textView.setBackgroundColor(color);
        }
    }


    private void hideEvent(TextView tv)
    {
        tv.setVisibility(View.INVISIBLE);
    }
}
