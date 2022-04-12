package com.teachersspace.schedule;

import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.format.DateFormat;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.teachersspace.R;
import com.teachersspace.auth.SessionManager;
import com.teachersspace.models.User;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentScheduleFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "StudentScheduleFragment";
    private View fragmentView;
    private SessionManager sessionManager;

    private ArrayList<Button> alb; //array to store all buttons
    private static ArrayList templ; //array to temporarily store the docsnapshot array

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_schedule, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentView = view;
        alb= new ArrayList<>();
        templ= new ArrayList<>();

        /*adding all book buttons to the button array*/
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


        /*all parent variables*/
        db=FirebaseFirestore.getInstance();
        LinearLayout ll= fragmentView.findViewById(R.id.linearlayout);

        for (Button b:alb){
            b.setOnClickListener(this);
        }

        /*getting document reference for the first time*/

        DocumentReference docRef= getcurrentdocref(view);
        getcurrentdocref(view);
    }

    /*setting on clicks for all the buttons*/
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button0:
                AlertDialog.Builder builder0 = new AlertDialog.Builder(getContext());
                builder0.setCancelable(true);
                builder0.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder0.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder0.setPositiveButton("Confirm",
                        (dialog, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview0);
                            String tvreftemp= tv.getText().toString();
                            String tvref= convertdateback(tvreftemp);
                            Log.d(TAG, tvref);
                            updateref(tvref, fragmentView);
                        });
                builder0.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog = builder0.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
            case R.id.button1:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                builder1.setCancelable(true);
                builder1.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder1.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder1.setPositiveButton("Confirm",
                        (dialog1, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview1);
                            String tvreftemp= tv.getText().toString();
                            String tvref= convertdateback(tvreftemp);
                            Log.d(TAG, tvref);
                            updateref(tvref, fragmentView);
                        });
                builder1.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog1 = builder1.create();
                dialog1.show();
                dialog1.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
            case R.id.button2:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());
                builder2.setCancelable(true);
                builder2.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder2.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder2.setPositiveButton("Confirm",
                        (dialog2, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview2);
                            String tvreftemp= tv.getText().toString();
                            String tvref= convertdateback(tvreftemp);
                            Log.d(TAG, tvref);
                            updateref(tvref, fragmentView);
                        });
                builder2.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog2 = builder2.create();
                dialog2.show();
                dialog2.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
            case R.id.button3:
                AlertDialog.Builder builder3 = new AlertDialog.Builder(getContext());
                builder3.setCancelable(true);
                builder3.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder3.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder3.setPositiveButton("Confirm",
                        (dialog3, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview3);
                            String tvreftemp= tv.getText().toString();
                            String tvref= convertdateback(tvreftemp);
                            Log.d(TAG, tvref);
                            updateref(tvref, fragmentView);
                        });
                builder3.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog3 = builder3.create();
                dialog3.show();
                dialog3.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
            case R.id.button4:
                AlertDialog.Builder builder4 = new AlertDialog.Builder(getContext());
                builder4.setCancelable(true);
                builder4.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder4.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder4.setPositiveButton("Confirm",
                        (dialog4, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview4);
                            String tvreftemp= tv.getText().toString();
                            String tvref= convertdateback(tvreftemp);
                            Log.d(TAG, tvref);
                            updateref(tvref, fragmentView);
                        });
                builder4.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog4 = builder4.create();
                dialog4.show();
                dialog4.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
            case R.id.button5:
                AlertDialog.Builder builder5 = new AlertDialog.Builder(getContext());
                builder5.setCancelable(true);
                builder5.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder5.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder5.setPositiveButton("Confirm",
                        (dialog5, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview5);
                            String tvreftemp= tv.getText().toString();
                            String tvref= convertdateback(tvreftemp);
                            Log.d(TAG, tvref);
                            updateref(tvref, fragmentView);
                        });
                builder5.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog5 = builder5.create();
                dialog5.show();
                dialog5.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
            case R.id.button6:
                AlertDialog.Builder builder6 = new AlertDialog.Builder(getContext());
                builder6.setCancelable(true);
                builder6.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder6.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder6.setPositiveButton("Confirm",
                        (dialog6, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview6);
                            String tvreftemp= tv.getText().toString();
                            String tvref= convertdateback(tvreftemp);
                            Log.d(TAG, tvref);
                            updateref(tvref, fragmentView);
                        });
                builder6.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog6 = builder6.create();
                dialog6.show();
                dialog6.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
            case R.id.button7:
                AlertDialog.Builder builder7 = new AlertDialog.Builder(getContext());
                builder7.setCancelable(true);
                builder7.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder7.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder7.setPositiveButton("Confirm",
                        (dialog7, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview7);
                            String tvreftemp= tv.getText().toString();
                            String tvref= convertdateback(tvreftemp);
                            Log.d(TAG, tvref);
                            updateref(tvref, fragmentView);
                        });
                builder7.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog7 = builder7.create();
                dialog7.show();
                dialog7.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
            case R.id.button8:
                AlertDialog.Builder builder8 = new AlertDialog.Builder(getContext());
                builder8.setCancelable(true);
                builder8.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder8.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder8.setPositiveButton("Confirm",
                        (dialog8, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview8);
                            String tvreftemp= tv.getText().toString();
                            String tvref= convertdateback(tvreftemp);
                            Log.d(TAG, tvref);
                            updateref(tvref, fragmentView);
                        });
                builder8.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog8 = builder8.create();
                dialog8.show();
                dialog8.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
            case R.id.button9:
                AlertDialog.Builder builder9 = new AlertDialog.Builder(getContext());
                builder9.setCancelable(true);
                builder9.setTitle(Html.fromHtml("<font color='#0F232D'>Book Slot? </font>"));
                builder9.setMessage("Are you sure you want to book the slot you just selected? : ");
                builder9.setPositiveButton("Confirm",
                        (dialog9, which) -> {
                            TextView tv= fragmentView.findViewById(R.id.textview6);
                            String tvreftemp= tv.getText().toString();
                            String tvref= convertdateback(tvreftemp);
                            Log.d(TAG, tvref);
                            updateref(tvref, fragmentView);
                        });
                builder9.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                AlertDialog dialog9 = builder9.create();
                dialog9.show();
                dialog9.getWindow().setBackgroundDrawableResource(R.color.beige);
                break;
        }
    }

    /*main button to access the states of slots and add them to UI*/
    public void createbuttons(ArrayList<Map> x, View view){
        /*converting the arraylist to a 2d array for easy accessing*/
        ArrayList<Map> newar= x;
        Object[][] main= new Object[0][5];
        Object[][] bookedslots= new Object[0][5];
        for (Map m:newar){
            Object[] temp= new Object[0];

            String d= (String) m.get("Date");
            temp= addo(temp, d);

            String t= (String) m.get("Time");
            temp= addo(temp, t);

            String e= (String) m.get("Event");
            temp= addo(temp,e);

            String s= (String) m.get("Student_Name");
            temp= addo(temp, s);

            String b= (String) m.get("isBooked");
            temp= addo(temp, b);

            if (m.get("isBooked").equals("false")){
                main= addo(main, temp);
            }
            else{
                bookedslots=addo(main, temp);
            }
        }

        /*updating UI*/
        if (main.length>0){
            for (int i=0; i<main.length; i++){
                String tempdnt = main[i][0].toString() + " " + main[i][1].toString();
                String dateandtime= converttoMM(tempdnt);
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
            ;
            }

    }

    /*method to add objects in 1d array*/
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

    /*method to add objects in 2d array*/
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

    /*updateref which has String(date+time) as tvref argument*/
    public void updateref(String tvref, View view){
        HashMap<String, String> mrem;

        DocumentReference docRef= getcurrentdocref(view);
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
                                    handler.post(() -> {
                                        getdocref(view, docRef);
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

    /*method to create a new hashmap given the args*/
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

    /*method to update the databse (remove+add updated entry)*/
    public void book(Map<String, String> map,DocumentReference docRef , ArrayList<Map<String, String>> temp, View view){
        docRef.update("slots", FieldValue.arrayRemove(map));
        this.sessionManager = new SessionManager(getContext());
        String studentuserid= this.sessionManager.getCurrentUser().getUid();
        DocumentReference docRefstd= db.collection("users").document(studentuserid);
        String name = null;

        docRefstd.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()){
                    String name= task.getResult().getString("name");
                    String event= "Meeting with "+ name;
                    String date2= (String) map.get("Date");
                    String time2= (String) map.get("Time");
                    String teacher_id= (String) map.get("Teacher_id");
                    String booking= "true";
                    String stu= "Student "+ name;

                    HashMap<String, String> newmap= hashMapCreator(event, date2, time2, teacher_id, booking, stu );
                    docRef.update("slots", FieldValue.arrayUnion(newmap));
                }
                else{
                    Log.d(TAG, "getting name didn't work");
                }
            }
        });


        //createbuttons(temp, view);
    }

    /*getting the docref and updating UI through the createbuttons method called*/
    public void getdocref(View view, DocumentReference docRef){

        for (int i=0; i<10; i++){
            String gettv= "textview"+String.valueOf(i);
            String getll= "hlinearlo"+String.valueOf(i);
            String packageName = getContext().getPackageName();
            int tvID = getResources().getIdentifier(gettv, "id", packageName);
            int llID = getResources().getIdentifier(getll, "id", packageName);

            LinearLayout hlo= view.findViewById(llID);
            hlo.setVisibility(hlo.INVISIBLE);

        }

        /*set prof name in total*/
        Bundle args= getArguments();
        if (args != null) {
            Log.d(TAG, "Student Schedule Args");
            User activeTeacher = User.deserialise(args.getString("contact"));
            String userid = activeTeacher.getUid();
            DocumentReference docRefprof = db.collection("users").document(userid);
            docRefprof.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()){
                        String name= task.getResult().getString("name");
                        setheading(name);
                    }
                    else{
                        Log.d(TAG, "getting name didn't work");
                    }
                }
            });

        }

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        SlotsModel s = new SlotsModel();
                        s.setSlots((ArrayList<Map>) document.get("slots"));
                        createbuttons((ArrayList<Map>) s.getSlots(), fragmentView);
                        Log.d(TAG, "Inital DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    /*converting date+time to UI String*/
    public String converttoMM(String s){
        SimpleDateFormat month_date = new SimpleDateFormat("dd'th of' MMM yyyy 'at' HH:mm", Locale.ENGLISH); //converted into
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//to be converted from

        String actualDate = s;

        Date date = null;
        try {
            date = sdf.parse(actualDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String result = month_date.format(date);
        Log.d(TAG, result);
        return result;
    }

    /*converting the UI String back for comparing when updating*/
    public String convertdateback(String s){
        SimpleDateFormat month_date = new SimpleDateFormat("yyyy-MM-ddHH:mm", Locale.ENGLISH); //converted into
        SimpleDateFormat sdf = new SimpleDateFormat("dd'th of' MMM yyyy 'at' HH:mm"); //to be converted from

        String actualDate = s;

        Date date = null;
        try {
            date = sdf.parse(actualDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String result = month_date.format(date);
        Log.d(TAG, result);
        return result;
    }

    public DocumentReference getcurrentdocref(View view){
        Bundle args= getArguments();
        if (args != null) {
            Log.d(TAG, "Student Schedule Args");
            User activeTeacher = User.deserialise(args.getString("contact"));
            String userid= activeTeacher.getUid();
            DocumentReference docRef = db.collection("profschedule").document(userid);
            getdocref(view, docRef);
            return docRef;
        }
        else{
            return null;
        }
    }

    public void setheading(String s){
        TextView heading=fragmentView.findViewById(R.id.headingwprof);
        heading.setText("Schedule a Meeting With "+s+ " BELOW: " );
    }

}
