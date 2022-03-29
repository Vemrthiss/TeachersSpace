package com.teachersspace.schedule;

import static com.teachersspace.auth.SessionManager.getUserUid;
import static com.teachersspace.schedule.CalendarUtils.selectedDate;
import static com.teachersspace.schedule.EditEventFragment.hashMapCreator;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.teachersspace.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DailyScheduleFragment extends Fragment {
    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;
    private Button previousDay;
    private Button nextDay;
    private SwipeRefreshLayout swipe;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference docRef = db.collection("profschedule").document(String.valueOf(getUserUid()));

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_daily_schedule, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generateEventsInEventsList(view);
        initWidgets(view);
        swiper(view);
        //initialSwipe(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initWidgets(View view)
    {
        monthDayText = view.findViewById(R.id.monthDayText);
        dayOfWeekTV = view.findViewById(R.id.dayOfWeekTV);
        hourListView = view.findViewById(R.id.hourListView);

        previousDay = view.findViewById(R.id.previousDay);
        nextDay = view.findViewById(R.id.nextDay);

        previousDay.setOnClickListener(previousDayAction());
        nextDay.setOnClickListener(nextDayAction());

    }

    private void swiper(View view){
        swipe = view.findViewById(R.id.swipeRefresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onRefresh() {
                generateEventsInEventsList(view);
                setDayView(view);
                swipe.setRefreshing(false);
            }
        });
    }
    private void initialSwipe(View view){
        swipe.post(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                swipe.setRefreshing(true);
                setDayView(view);
                swipe.setRefreshing(false);
            }
        });
    }

    private void generateEventsInEventsList(View view){
        ArrayList<Event> tempEventList = new ArrayList<>();
        List<Map<String, String>> tempStringList = new ArrayList<>();
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {

                    //clear eventsList first
                    Event.eventsList.clear();

                    //get all events
                    Map<String, Object> temp = value.getData();
                    ArrayList<HashMap<String, String>> tempArray = (ArrayList<HashMap<String, String>>) temp.get("slots");

                    for (int i = 0; i < tempArray.size(); i++){

                        String eventName = tempArray.get(i).get("Event");
                        LocalDate date = LocalDate.parse(tempArray.get(i).get("Date"));
                        LocalTime time = LocalTime.parse(tempArray.get(i).get("Time"));
                        Boolean booking = Boolean.parseBoolean(tempArray.get(i).get("isBooked"));
                        String teacher_id = tempArray.get(i).get("Teacher_id");
                        String student_id = tempArray.get(i).get("Student_Name");

                        //making the objects as required
                        HashMap<String, String> tempObjectToPass = hashMapCreator(eventName, tempArray.get(i).get("Date"),
                                tempArray.get(i).get("Time"), teacher_id, tempArray.get(i).get("isBooked"), student_id);
                        Event newEvent = new Event(eventName, date, time, booking, teacher_id, student_id);

                        //adding to temporary lists
                        tempEventList.add(newEvent);
                        tempStringList.add(tempObjectToPass);
                    }
                    Event.eventsList = tempEventList;
                    Event.UserTextList = tempStringList;
                    initialSwipe(view);

                } else {
                    //do nothing
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setDayView(View view)
    {
        monthDayText.setText(CalendarUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);
        setHourAdapter(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setHourAdapter(View view)
    {
        HourAdapter hourAdapter = new HourAdapter(getContext(), hourEventList());
        hourListView.setAdapter(hourAdapter);
        hourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //creating bundle with required string
                Bundle b = new Bundle();
                b.putString("t", String.valueOf(hourEventList().get(i).time));
                openEditEvent(b);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<HourEvent> hourEventList()
    {
        ArrayList<HourEvent> list = new ArrayList<HourEvent>();

        for(int hour = 9; hour < 18; hour++)
        {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Event> events = Event.eventsForDateAndTime(selectedDate, time);
            HourEvent hourEvent = new HourEvent(time, events);
            list.add(hourEvent);
        }
        return list;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public View.OnClickListener previousDayAction()
    {
        return view -> {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
            setDayView(view);
        };
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View.OnClickListener nextDayAction()
    {
        return view -> {
            CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
            setDayView(view);
        };
    }

    public void openEditEvent(Bundle args) {
        NavDirections directions = new NavDirections() {
            @NonNull
            @Override
            public Bundle getArguments() {
                return args;
            }

            @Override
            public int getActionId() {
                    return R.id.navigate_edit_event_action;
                }
        };
        NavHostFragment.findNavController(DailyScheduleFragment.this).navigate(directions);
    }
}
