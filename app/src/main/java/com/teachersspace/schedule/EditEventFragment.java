package com.teachersspace.schedule;


import static com.teachersspace.auth.SessionManager.getUserUid;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teachersspace.R;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditEventFragment extends Fragment {

    private TextView eventDateTV, eventTimeTV;
    private String timeFromArrString;
    private LocalTime time;
    private String eventName = "Free Time Allocated";
    private Button saveButton;
    private Button deleteButton;
    private boolean notBooked = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("profschedule").document(String.valueOf(getUserUid()));
    private String teacherid = getUserUid();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_edit, container, false);
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        Bundle b = new Bundle();
        b = getArguments();
        timeFromArrString = CalendarUtils.selectedDate.toString() + " " + b.getString("t") + ":00";
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        initWidgets(view);
        time = LocalDateTime.parse(timeFromArrString, timeFmt).toLocalTime();


        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText("Time: " + CalendarUtils.formattedShortTime(time));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initWidgets(View view) {
        eventDateTV = view.findViewById(R.id.eventDateTV);
        eventTimeTV = view.findViewById(R.id.eventTimeTV);

        saveButton = view.findViewById(R.id.saveButton);
        deleteButton = view.findViewById(R.id.deleteButton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEventAction();

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteEventAction();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveEventAction() {
        boolean eventAlreadyMade = false;
        for (int i = 0; i < Event.eventsList.size(); i++){
            if (time.equals(Event.eventsList.get(i).getTime())
                    && CalendarUtils.selectedDate.equals(Event.eventsList.get(i).getDate())){
                eventAlreadyMade = true;
            }
        }
        if (eventAlreadyMade){
            Toast.makeText(getContext(), "Event already made in this slot.", Toast.LENGTH_SHORT).show();
        }
        else{
            Event newEvent = new Event(eventName, CalendarUtils.selectedDate, time, notBooked, teacherid,"");

            //creates hashmap
            HashMap<String, String> userData = hashMapCreator(eventName, CalendarUtils.selectedDate.toString(),
                    CalendarUtils.formattedShortTime(time), teacherid, String.valueOf(notBooked), "");

            //add events to their respective lists
            Event.UserTextList.add(userData);
            Event.eventsList.add(newEvent);

            //updates firestore - change method
            docRef.update("slots", FieldValue.arrayUnion(userData));

            //go back to previous fragment
            NavHostFragment.findNavController(this).navigateUp();

        }

    }

    public void DeleteEventAction() {

        int index_event = -1;
        String dontHave = "There is no existing event in this timeslot to delete.";
        for (int i = 0; i < Event.eventsList.size(); i++){
            if (time.equals(Event.eventsList.get(i).getTime())
                    && CalendarUtils.selectedDate.equals(Event.eventsList.get(i).getDate())){
                index_event = i;
                break;
            }
        }

        if (index_event != -1){

            Event.eventsList.remove(index_event);
            //updates firestore - change method
            docRef.update("slots", FieldValue.arrayRemove(Event.UserTextList.get(index_event)));
            NavHostFragment.findNavController(this).navigateUp();

        }

        else{
            Toast.makeText(getContext(), "No event here to delete.", Toast.LENGTH_SHORT).show();
            deleteButton.setTextColor(Color.LTGRAY);
        }

    }


    public static HashMap<String, String> hashMapCreator(String event, String date, String time, String teacher_id, String booking, String student_id)
    {
        HashMap<String, String> temp = new HashMap<>();
        temp.put("Event", event);
        temp.put("Date", date);
        temp.put("Time", time);
        temp.put("Teacher_id", teacher_id);
        temp.put("isBooked", booking);
        temp.put("Student_Name", student_id);
        return temp;
    }

    private void updateFireStore(List<Map<String, String>> userList){
        HashMap<String, List> slotObject = new HashMap<String, List>();
        slotObject.put("slots", userList);
        //uploads onto firestore
        db.collection("profschedule").document(String.valueOf(getUserUid())).set(slotObject)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("", "DocumentSnapshot successfully re-written!");
                    }
                });
    }
}
