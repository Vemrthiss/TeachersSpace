package com.teachersspace.communications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.teachersspace.auth.SessionManager;
import com.teachersspace.data.UserRepository;
import com.teachersspace.models.User;
import com.teachersspace.parent.ParentActivity;
import com.teachersspace.student.StudentActivity;
import com.teachersspace.teacher.TeacherActivity;
import com.twilio.voice.CallInvite;

import com.teachersspace.R;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This service handles the notifications from outside the app, not the in-app dialog
 */
public class IncomingCallNotificationService extends Service {
    private static final String TAG = IncomingCallNotificationService.class.getSimpleName();
    private SessionManager sessionManager;
    private final UserRepository userRepository = new UserRepository();

    private Class<?> getUserActivityClass() {
        User user = this.sessionManager.getCurrentUser();
        if (user == null) {
            return null;
        }
        User.UserType userType = user.getUserType();
        if (userType == User.UserType.PARENT) {
            return ParentActivity.class;
        } else if (userType == User.UserType.STUDENT) {
            return StudentActivity.class;
        }
        return TeacherActivity.class; // default
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.sessionManager = new SessionManager(this);
        String action = intent.getAction();
        Log.d(TAG, "incoming call notification service started with action: " + action);

        if (action != null) {
            CallInvite callInvite = intent.getParcelableExtra(Constants.INCOMING_CALL_INVITE);
            int notificationId = intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0);
            switch (action) {
                case Constants.ACTION_INCOMING_CALL:
                    handleIncomingCall(callInvite, notificationId);
                    break;
                case Constants.ACTION_ACCEPT:
                    accept(callInvite, notificationId);
                    break;
                case Constants.ACTION_REJECT:
                    reject(callInvite);
                    break;
                case Constants.ACTION_CANCEL_CALL:
                    handleCancelledCall(intent);
                    break;
                case Constants.ACTION_INCOMING_MESSAGE:
                    handleIncomingMessage(intent);
                    break;
                default:
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Notification createNotification(CallInvite callInvite, int notificationId, int channelImportance, String callerName) {
        Log.d(TAG, "createNotification is called");
        /*
          call the different activities based on user type, and each activity then calls on the fragment itself
          This might make more sense because the examples here CALL activities, and fragments do not exist
          by default calls teacher activity, refer to getUserActivityClass above
          https://stackoverflow.com/questions/36100187/how-to-start-fragment-from-an-activity
        */
        Intent intent = new Intent(this, getUserActivityClass());
        intent.setAction(Constants.ACTION_INCOMING_CALL_NOTIFICATION);
        intent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        intent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_IMMUTABLE);
        /*
         * Pass the notification id and call sid to use as an identifier to cancel the
         * notification later
         */
        Bundle extras = new Bundle();
        extras.putString(Constants.CALL_SID_KEY, callInvite.getCallSid());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return buildNotification(callerName + " is calling.",
                    pendingIntent,
                    extras,
                    callInvite,
                    notificationId,
                    createChannel(channelImportance));
        } else {
            //noinspection deprecation
            return new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_call_end_white_24dp)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(callerName + " is calling.")
                    .setAutoCancel(true)
                    .setExtras(extras)
                    .setContentIntent(pendingIntent)
                    .setGroup("test_app_notification")
                    .setCategory(Notification.CATEGORY_CALL)
                    .setColor(Color.rgb(214, 10, 37)).build();
        }
    }

    /**
     * Build a notification.
     *
     * @param text          the text of the notification
     * @param pendingIntent the body, pending intent for the notification
     * @param extras        extras passed with the notification
     * @return the builder
     */
    @TargetApi(Build.VERSION_CODES.O)
    private Notification buildNotification(String text, PendingIntent pendingIntent, Bundle extras,
                                           final CallInvite callInvite,
                                           int notificationId,
                                           String channelId) {
        Intent rejectIntent = new Intent(getApplicationContext(), IncomingCallNotificationService.class);
        rejectIntent.setAction(Constants.ACTION_REJECT);
        rejectIntent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);
        rejectIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        PendingIntent piRejectIntent = PendingIntent.getService(getApplicationContext(), notificationId, rejectIntent, PendingIntent.FLAG_IMMUTABLE);

        Intent acceptIntent = new Intent(getApplicationContext(), getUserActivityClass());
        acceptIntent.setAction(Constants.ACTION_ACCEPT);
        acceptIntent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);
        acceptIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent piAcceptIntent = PendingIntent.getActivity(getApplicationContext(), notificationId, acceptIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_call_end_white_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(text)
                        .setCategory(Notification.CATEGORY_CALL)
                        .setExtras(extras)
                        .setAutoCancel(true)
                        .addAction(android.R.drawable.ic_menu_delete, getString(R.string.decline), piRejectIntent)
                        .addAction(android.R.drawable.ic_menu_call, getString(R.string.answer), piAcceptIntent)
                        .setFullScreenIntent(pendingIntent, true);

        return builder.build();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private String createChannel(int channelImportance) {
        NotificationChannel callInviteChannel = new NotificationChannel(Constants.VOICE_CHANNEL_HIGH_IMPORTANCE,
                "Primary Voice Channel", NotificationManager.IMPORTANCE_HIGH);
        String channelId = Constants.VOICE_CHANNEL_HIGH_IMPORTANCE;

        if (channelImportance == NotificationManager.IMPORTANCE_LOW) {
            callInviteChannel = new NotificationChannel(Constants.VOICE_CHANNEL_LOW_IMPORTANCE,
                    "Primary Voice Channel", NotificationManager.IMPORTANCE_LOW);
            channelId = Constants.VOICE_CHANNEL_LOW_IMPORTANCE;
        }
        callInviteChannel.setLightColor(Color.GREEN);
        callInviteChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(callInviteChannel);

        return channelId;
    }

    private void accept(CallInvite callInvite, int notificationId) {
        endForeground();
        Intent activeCallIntent = new Intent(this, getUserActivityClass());
        activeCallIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activeCallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activeCallIntent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);
        activeCallIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        activeCallIntent.setAction(Constants.ACTION_ACCEPT);
        startActivity(activeCallIntent);
    }

    private void reject(CallInvite callInvite) {
        endForeground();
        callInvite.reject(getApplicationContext());
    }

    private void handleCancelledCall(Intent intent) {
        endForeground();
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleIncomingCall(CallInvite callInvite, int notificationId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setCallInProgressNotification(callInvite, notificationId);
        }
        sendCallInviteToActivity(callInvite, notificationId);
    }

    private void endForeground() {
        stopForeground(true);
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void setCallInProgressNotification(CallInvite callInvite, int notificationId) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = new Handler(mainLooper);
        final String callerUid = CallEnabledActivity.getUidFromTwilioFrom(callInvite.getFrom());
        executor.execute(() -> {
            OnCompleteListener<QuerySnapshot> callback = task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    if (result != null && !result.isEmpty()) {
                        List<User> users = result.toObjects(User.class);
                        User callerUser = users.get(0);

                        handler.post(() -> {
                            String callerName = callerUser.getName();
                            if (isAppVisible()) {
                                Log.i(TAG, "setCallInProgressNotification - app is visible.");
                                startForeground(notificationId, createNotification(callInvite, notificationId, NotificationManager.IMPORTANCE_LOW, callerName));
                            } else {
                                Log.i(TAG, "setCallInProgressNotification - app is NOT visible.");
                                startForeground(notificationId, createNotification(callInvite, notificationId, NotificationManager.IMPORTANCE_HIGH, callerName));
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            };
            userRepository.getUserByUid(callerUid, callback);
        });
    }

    /**
     * Send the CallInvite to the relevant user activity (CallEnabledActivity).
     * Start the activity if it is not running already.
     * @param callInvite
     * @param notificationId
     */
    private void sendCallInviteToActivity(CallInvite callInvite, int notificationId) {
//        if (Build.VERSION.SDK_INT >= 29 && !isAppVisible()) {
//            return;
//        }
        Intent intent = new Intent(this, getUserActivityClass());
        intent.setAction(Constants.ACTION_INCOMING_CALL);
        intent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, notificationId);
        intent.putExtra(Constants.INCOMING_CALL_INVITE, callInvite);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    private boolean isAppVisible() {
        return ProcessLifecycleOwner
                .get()
                .getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.STARTED);
    }

    private void handleIncomingMessage(Intent intent) {
        String fromUid = intent.getStringExtra(Constants.INCOMING_MESSAGE_FROM);
        String body = intent.getStringExtra(Constants.INCOMING_MESSAGE_BODY);
        int notificationId = intent.getIntExtra(Constants.INCOMING_MESSAGE_NOTIFICATION_ID, 0);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Looper mainLooper = Looper.getMainLooper();
        Handler handler = new Handler(mainLooper);

        executor.execute(() -> {
            OnCompleteListener<QuerySnapshot> callback = task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    if (result != null && !result.isEmpty()) {
                        List<User> users = result.toObjects(User.class);
                        User fromUser = users.get(0);

                        handler.post(() -> {
                            String fromUserName = fromUser.getName();
                            if (isAppVisible()) {
                                startForeground(notificationId, createMessageNotification(notificationId, fromUid, fromUserName, body, NotificationManager.IMPORTANCE_LOW));
                            } else {
                                startForeground(notificationId, createMessageNotification(notificationId, fromUid, fromUserName, body, NotificationManager.IMPORTANCE_HIGH));
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            };
            userRepository.getUserByUid(fromUid, callback);
        });
    }

    private Notification createMessageNotification(int notificationId, String fromUid, String fromUserName, String body, int channelImportance) {
        Log.d(TAG, "createMessageNotification is called" + fromUserName + body);

        Intent intent = new Intent(this, getUserActivityClass());
        intent.setAction(Constants.ACTION_INCOMING_MESSAGE_NOTIFICATION);
        intent.putExtra(Constants.INCOMING_MESSAGE_NOTIFICATION_ID, notificationId);
        intent.putExtra(Constants.INCOMING_MESSAGE_FROM, fromUserName);
        intent.putExtra(Constants.INCOMING_MESSAGE_FROM_UID, fromUid);
        intent.putExtra(Constants.INCOMING_MESSAGE_BODY, body);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_IMMUTABLE);

        String text = fromUserName + ": " + body;

        Bundle extras = new Bundle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return buildMessageNotification(text, pendingIntent, extras, createChannel(channelImportance));
        } else {
            //noinspection deprecation
            return new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(text)
                    .setAutoCancel(true)
                    .setExtras(extras)
                    .setContentIntent(pendingIntent)
                    .setGroup("test_app_notification")
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setColor(Color.rgb(214, 10, 37)).build();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private Notification buildMessageNotification(String text,
                                                  PendingIntent pendingIntent,
                                                  Bundle extras,
                                                  String channelId) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(getApplicationContext(), channelId)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(text)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setExtras(extras)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
                        //.setFullScreenIntent(pendingIntent, true);

        return builder.build();
    }
}
