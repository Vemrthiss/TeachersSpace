package com.teachersspace.auth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.teachersspace.helpers.SharedPreferencesManager;
import com.teachersspace.models.User;
import com.teachersspace.parent.ParentActivity;
import com.teachersspace.student.StudentActivity;
import com.teachersspace.teacher.TeacherActivity;

import java.util.List;
import java.util.Objects;

public class SessionManager extends SharedPreferencesManager {
    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "LoginSession";
    private static final String USER_PREF_KEY = "currentUser";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AlertDialog confirmUserTypeDialog;
    private String chosenUserType;

    public CollectionReference getCollection() {
        return db.collection("users");
    }

    public SessionManager(Context ctx) {
        super(ctx);
    }

    @Override
    public String getPrefName() {
        return SessionManager.PREF_NAME;
    }

    public User getCurrentUser() {
        String userSerialised = this.sessionObject.getString(USER_PREF_KEY, "");
        if (userSerialised.equals("")) {
            return null;
        }
        return new Gson().fromJson(userSerialised, User.class);
    }
    public void setCurrentUser(User newUser) {
        String userSerialised = new Gson().toJson(newUser);
        this.sessionObjectEditor.putString(USER_PREF_KEY, userSerialised);
        this.sessionObjectEditor.apply();
    }
    public void clearCurrentUser() {
        this.sessionObjectEditor.putString(USER_PREF_KEY, "");
        this.sessionObjectEditor.apply();
    }

    public static FirebaseUser getFirebaseLoginInfo() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static String getUserUid() {
        FirebaseUser user = getFirebaseLoginInfo();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    public User createUser(String uid, String name, User.UserType userType) {
        User user = new User(uid, name, userType);

        getCollection()
                .add(user.convertToMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        return user;
    }

    /**
     * Called after SUCCESSFUL firebase login, i.e. firebase user is not null
     */
    public void onLogin(Activity activity) {
        FirebaseUser user = getFirebaseLoginInfo();
        String uid = getUserUid();
        String name = user.getDisplayName();
        User.UserType userType = User.UserType.TEACHER; // just for testing

        getCollection()
                .whereEqualTo("uid", uid)
                .limit(1)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            if (result != null) {
                                if (result.isEmpty()) {
                                    // detects a NEW USER, onboards user by asking them for user type in a dialog
                                    // create user data because the associated user cannot be found in db
                                    confirmUserTypeDialog = chooseUserTypeDialog(activity, confirmUserType(uid, name, activity));
                                    confirmUserTypeDialog.show();

                                } else {
                                    // get the user from firestore user collection
                                    List<User> users = result.toObjects(User.class);
                                    User user = users.get(0);
                                    // set user info
                                    setCurrentUser(user);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void setChosenUserType(String chosenUserType) {
        this.chosenUserType = chosenUserType;
    }
    private DialogInterface.OnClickListener confirmUserType(String uid, String name, Activity activity) {
        return (dialog, which) -> {
            User.UserType userType;
            switch (this.chosenUserType) {
                case "PARENT":
                    userType = User.UserType.PARENT;
                    break;
                case "STUDENT":
                    userType = User.UserType.STUDENT;
                    break;
                case "TEACHER":
                default:
                    userType = User.UserType.TEACHER;
            }
            User user = createUser(uid, name, userType);
            setCurrentUser(user);
            confirmUserTypeDialog.dismiss();

            // navigate to correct activity
            switch (userType) {
                case PARENT:
                    activity.startActivity(new Intent(activity, ParentActivity.class));
                    break;
                case STUDENT:
                    activity.startActivity(new Intent(activity, StudentActivity.class));
                    break;
                case TEACHER:
                default:
                    activity.startActivity(new Intent(activity, TeacherActivity.class));
            }
        };
    }

    public AlertDialog chooseUserTypeDialog(Activity activity,
                                            final DialogInterface.OnClickListener confirmUserType) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Select User Type");
        CharSequence[] choices = {"TEACHER", "PARENT", "STUDENT"};
        alertDialogBuilder.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String chosenUserType = (String) choices[i];
                setChosenUserType(chosenUserType);
            }
        });
        alertDialogBuilder.setPositiveButton("Confirm", confirmUserType);
        alertDialogBuilder.setCancelable(false);
        return alertDialogBuilder.create();
    }
}
