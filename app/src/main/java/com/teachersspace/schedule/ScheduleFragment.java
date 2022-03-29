package com.teachersspace.schedule;

import static com.teachersspace.auth.SessionManager.getUserUid;
import static com.teachersspace.schedule.CalendarUtils.daysInMonthArray;
import static com.teachersspace.schedule.CalendarUtils.monthYearFromDate;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.teachersspace.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleFragment extends Fragment implements CalendarAdapter.OnItemListener {
    private static final String TAG = "ScheduleFragment";

    private Button nextButton;
    private Button prevButton;
    private Button dailyButton;
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    public static String TeacherName;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("profschedule").document(String.valueOf(getUserUid()));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initWidgets(view);
        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();
        InitialiseArray();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, LocalDate date) {
        if(date != null)
        {
            CalendarUtils.selectedDate = date;
            setMonthView();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initWidgets(View view)
    {
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);
        nextButton = view.findViewById(R.id.nextMonth);
        prevButton = view.findViewById(R.id.prevMonth);
        dailyButton = view.findViewById(R.id.dailyButton);

        nextButton.setOnClickListener(nextMonthAction());
        prevButton.setOnClickListener(previousMonthAction());
        dailyButton.setOnClickListener(dailyAction());


    }

    //to update once
//    private void InitialiseEventsInEventsList() {
//        ArrayList<Event> tempEventList = new ArrayList<>();
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    //get all events
//                    Map<String, Object> temp = documentSnapshot.getData();
//                    ArrayList<HashMap<String, String>> tempArray = (ArrayList<HashMap<String, String>>) temp.get("slots");
//
//                    if (!tempArray.isEmpty()){
//                        for (int i = 0; i < tempArray.size(); i++){
//                            String eventName = tempArray.get(i).get("Event");
//                            LocalDate date = LocalDate.parse(tempArray.get(i).get("Date"));
//                            LocalTime time = LocalTime.parse(tempArray.get(i).get("Time"));
//                            Boolean booking = Boolean.parseBoolean(tempArray.get(i).get("isBooked"));
//                            String teacher_id = tempArray.get(i).get("Teacher_id");
//                            String student_id = tempArray.get(i).get("Student_Name");
//
//                            Event newEvent = new Event(eventName, date, time, booking, teacher_id, student_id);
//                            tempEventList.add(newEvent);
//                        }
//                        Event.eventsList = tempEventList;
//                    }
//                }
//            }
//        });
//    }


    private void InitialiseArray(){
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        if (map.size() == 0) {
                            HashMap<String, List> slotObject = new HashMap<String, List>();
                            slotObject.put("slots", new ArrayList());
                            //uploads onto firestore
                            db.collection("profschedule").document(String.valueOf(getUserUid())).set(slotObject)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("", "DocumentSnapshot successfully initialised!");
                                        }
                                    });
                        }
                    }

                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView()
    {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View.OnClickListener previousMonthAction() {
        return view -> {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
            setMonthView();
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View.OnClickListener nextMonthAction() {
        return view -> {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
            setMonthView();
        };
    }


    public View.OnClickListener dailyAction() {
        return view -> {
            NavDirections directions = new NavDirections() {
                @NonNull
                @Override
                public Bundle getArguments() {
                    return new Bundle();
                }

                @Override
                public int getActionId() {
                    return R.id.navigate_daily_schedule_action;
                }
            };
            NavHostFragment.findNavController(ScheduleFragment.this).navigate(directions);
        };
    }
}
