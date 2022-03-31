package com.teachersspace.schedule;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teachersspace.R;
import com.teachersspace.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentScheduleFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "StudentScheduleFragment";
    private View fragmentView;

    private ArrayList<TextView> alt;
    private ArrayList<Button> alb;
    private ArrayList<String> bsa;
    private ArrayList<String> list;
    private ListView lv;
    private static ArrayList templ;
    private static ArrayList mainl;

    String tvref0= "";
    String tvref1= "";
    String tvref2= "";
    String tvref3= "";
    String tvref4= "";
    String tvref5= "";
    String tvref6= "";
    String tvref7= "";
    String tvref8= "";
    String tvref9= "";

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_schedule, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentView = view;
        //adding all textviews and buttons in arraylists
        alt= new ArrayList<>();
        alb= new ArrayList<>();
        bsa= new ArrayList<>();
        list= new ArrayList<>();
        templ= new ArrayList<>();
        mainl= new ArrayList<>();

        alb.add(fragmentView.findViewById(R.id.button0));
        alb.add(fragmentView.findViewById(R.id.button1));
        alb.add(fragmentView.findViewById(R.id.button2));
        alb.add(fragmentView.findViewById(R.id.button3));
        alb.add(fragmentView.findViewById(R.id.button4));
        alb.add(fragmentView.findViewById(R.id.button5));
        alb.add(fragmentView.findViewById(R.id.button6));
        alb.add(fragmentView.findViewById(R.id.button7));
        alb.add(fragmentView.findViewById(R.id.button8));
        alb.add(fragmentView.findViewById(R.id.button9));

        db=FirebaseFirestore.getInstance();

        //getting LinearLayout variable
        LinearLayout ll= fragmentView.findViewById(R.id.linearlayout);

        //setting onclicklistener for all buttons in alb
        for (Button b:alb){
            b.setOnClickListener(this);
        }

        DocumentReference docRef = db.collection("profschedule").document("dqpqTSphxfS4xy0B2R5NeHv638D2");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        templ= (ArrayList<Map>) document.get("slots");
                        createbuttons(templ, fragmentView);
                        Log.d(TAG, "Inital DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        Bundle args = getArguments();
        if (args != null) {
            Log.d(TAG, "Student Schedule Args");
            User activeTeacher = User.deserialise(args.getString("contact"));
        }
    }
    //all on clicks
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button0:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(true);
                builder.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder.setPositiveButton("Confirm",
                        (dialog, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview0);
                            tvref0= tv.getText().toString();
                            Log.d(TAG, tvref0);
                            updateref(tvref0, fragmentView);

                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
            case R.id.button1:
                break;
            case R.id.button2:
                break;
            case R.id.button3:
                break;
            case R.id.button4:
                break;
            case R.id.button5:
                break;
            case R.id.button6:
                break;
            case R.id.button7:
                break;
            case R.id.button8:
                break;
            case R.id.button9:
                break;

        }
    }

    public void createbuttons(ArrayList<Map<String, String>> x, View view){
        //ArrayList<Map> alm = (ArrayList<Map>) x;
        ArrayList<Map<String, String>> newar= x;
        Object[][] main= new Object[0][5];
        for (Map m:newar){
            Object[] temp= new Object[0];
//            ArrayList<Integer> keys= new ArrayList<Integer>(m.keySet());
//            ArrayList<Object> vals= new ArrayList<Object>(m.values());

            String d= (String) m.get("Date");
            temp= addo(temp, d);

            String t= (String) m.get("Time");
            temp= addo(temp, t);

            String e= (String) m.get("Event");
            temp= addo(temp,e);

            String s= (String) m.get("Student_Name");
            temp= addo(temp, s);

            Boolean b= (Boolean) m.get("is_booked");
            temp= addo(temp, b);

            main= addo(main, temp);
        }

        if (main.length<=10){
            TextView intro= fragmentView.findViewById(R.id.intro);
            intro.setText("The following slots for Prof. X are available: ");
            for (int i=0; i<main.length; i++){
                String dateandtime = main[i][0].toString() + main[i][1].toString();
                String gettv= "textview"+String.valueOf(i);
                String getll= "hlinearlo"+String.valueOf(i);
                String packageName = getContext().getPackageName();
                int tvID = getResources().getIdentifier(gettv, "id", packageName);
                int llID = getResources().getIdentifier(getll, "id", packageName);

                LinearLayout hlo= view.findViewById(llID);
                hlo.setVisibility(hlo.VISIBLE);
                TextView temptv= view.findViewById(tvID);
                temptv.setText(dateandtime);


            }
        }
        else{
//            TextView intro= view.findViewById(R.id.intro);
//            main= sort(main);
//            intro.setText("The following slots for Prof. X are available: ");
//            for (int i=0; i<10; i++){
//                String dateandtime = "Date: " + main[i][0].toString() + " Time: " + main[i][1].toString();
//                String gettv= "textview"+String.valueOf(i);
//                String getll= "hlinearlo"+String.valueOf(i);
//                int tvID = getResources().getIdentifier(gettv, "id", getPackageName());
//                int llID = getResources().getIdentifier(getll, "id", getPackageName());
//
//                LinearLayout hlo= findViewById(llID);
//                hlo.setVisibility(hlo.VISIBLE);
//                TextView temptv= findViewById(tvID);
//                temptv.setText(dateandtime);
//
//
//            }
        }


    }

    public Object[] addo(Object[] arr, Object x){
        int i;
        int n= arr.length;
        Object[] narr= new Object[n+1];

        for (i=0; i<n; i++){
            narr[i]= arr[i];
        }
        narr[n]=x;
        return narr;
    }

    public Object[][] addo(Object[][] twodarr, Object[] x){
        int i;
        int n= twodarr.length;
        Object[][] narr= new Object[n+1][3];

        for (i=0; i<n; i++){
            narr[i]= twodarr[i];
        }
        narr[n]=x;
        return narr;
    }

    public Object[][] sort(Object[][] twodarr){
        return twodarr;
    }

    public void updateref(String tvref, View view){
        HashMap<String, String> mrem;

        DocumentReference docRef = db.collection("profschedule").document("dqpqTSphxfS4xy0B2R5NeHv638D2");

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = new Handler(mainLooper);
        executor.execute(() -> {
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<Map<String, String>> slotlist= (ArrayList<Map<String, String>>) document.get("slots");
                            for (Map<String, String> mx:slotlist){
                                String date= mx.get("Date");
                                String time= mx.get("Time");
                                Log.d(TAG, (date+time).toString());

                                if ((date+time).equals(tvref)){
                                    handler.post(() -> {
                                        book(mx, docRef, slotlist, view);
                                    });

                                    break;
                                }
                            }
                            // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
        });


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

    public void book(Map<String, String> map,DocumentReference docRef , ArrayList<Map<String, String>> temp, View view){
        docRef.update("slots", FieldValue.arrayRemove(map));
        String event= "Meeting with "+ "Student Id";
        String date2= (String) map.get("Date");
        String time2= (String) map.get("Time");
        String teacher_id= (String) map.get("Teacher_id");
        String booking= "true";
        String stu= "Student X";

        HashMap<String, String> newmap= hashMapCreator(event, date2, time2, teacher_id, booking, stu );
        docRef.update("slots", FieldValue.arrayUnion(newmap));

        createbuttons(temp, view);
    }
}
