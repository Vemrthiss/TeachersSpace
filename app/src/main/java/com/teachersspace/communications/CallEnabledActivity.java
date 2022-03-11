package com.teachersspace.communications;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.teachersspace.R;
import com.teachersspace.auth.SessionManager;
import com.twilio.audioswitch.AudioDevice;
import com.twilio.audioswitch.AudioSwitch;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.ConnectOptions;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import kotlin.Unit;

public abstract class CallEnabledActivity extends AppCompatActivity implements CommunicationsFragment.CommunicationsFragmentProps {
    private static final String TAG = "CallEnabledActivity";
    public AlertDialog alertDialog;
    public CallInvite activeCallInvite;
    public int activeCallNotificationId;
    private Call activeCall;
    RegistrationListener registrationListener = registrationListener();
    Call.Listener callListener = callListener();
    private NotificationManager notificationManager;

    private CoordinatorLayout coordinatorLayout; // used for snackbars, the overall layout container for the 5 elements of the fragment
//    private FloatingActionButton callActionFab;
//    private FloatingActionButton hangupActionFab;
//    private FloatingActionButton holdActionFab;
//    private FloatingActionButton muteActionFab;
    // private Chronometer chronometer;

    private boolean isReceiverRegistered = false;
    private VoiceBroadcastReceiver voiceBroadcastReceiver;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int MIC_PERMISSION_REQUEST_CODE = 1;

    // Audio Device Management
    private AudioSwitch audioSwitch;
    private int savedVolumeControlStream;
    private MenuItem audioDeviceMenuItem;

    private String accessToken;
    private TwilioTokenManager tokenManager;

    private CommunicationsFragment communicationsFragment;
    public abstract int getCommunicationsFragmentContainer();

    // twilio access token
    private String getStoredTwilioToken() {
        if (tokenManager == null) {
            tokenManager = new TwilioTokenManager(this, SessionManager.getUserUid());
        }
        return tokenManager.getTwilioAccessToken();
    }

    // TODO: review!
    // https://stackoverflow.com/questions/61970100/oncreateoptionsmenu-not-being-called
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        audioDeviceMenuItem = menu.findItem(R.id.menu_audio_device);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_audio_device) {
            showAudioDevices();
            return true;
        }
        return false;
    }

    /*
     * Show the current available audio devices.
     */
    private void showAudioDevices() {
        AudioDevice selectedDevice = audioSwitch.getSelectedAudioDevice();
        List<AudioDevice> availableAudioDevices = audioSwitch.getAvailableAudioDevices();

        if (selectedDevice != null) {
            int selectedDeviceIndex = availableAudioDevices.indexOf(selectedDevice);

            ArrayList<String> audioDeviceNames = new ArrayList<>();
            for (AudioDevice a : availableAudioDevices) {
                audioDeviceNames.add(a.getName());
            }

            new AlertDialog.Builder(this)
                    .setTitle(R.string.select_device)
                    .setSingleChoiceItems(
                            audioDeviceNames.toArray(new CharSequence[0]),
                            selectedDeviceIndex,
                            (dialog, index) -> {
                                dialog.dismiss();
                                AudioDevice selectedAudioDevice = availableAudioDevices.get(index);
                                updateAudioDeviceIcon(selectedAudioDevice);
                                audioSwitch.selectDevice(selectedAudioDevice);
                            }).create().show();
        }
    }

    @Override
    protected void onStart() {
        getCommunicationsFragment();
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver();
        startAudioSwitch();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tokenManager = new TwilioTokenManager(this, SessionManager.getUserUid());

        // These flags ensure that the activity can be launched when the screen is locked.
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        coordinatorLayout = findViewById(R.id.coordinator_layout);
//        callActionFab = findViewById(R.id.call_action_fab);
//        hangupActionFab = findViewById(R.id.hangup_action_fab);
//        holdActionFab = findViewById(R.id.hold_action_fab);
//        muteActionFab = findViewById(R.id.mute_action_fab);
//        chronometer = findViewById(R.id.chronometer);

        // register click event listeners
//        callActionFab.setOnClickListener(callActionFabClickListener());
//        hangupActionFab.setOnClickListener(hangupActionFabClickListener());
//        holdActionFab.setOnClickListener(holdActionFabClickListener());
//        muteActionFab.setOnClickListener(muteActionFabClickListener());

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /*
         * Setup the broadcast receiver to be notified of FCM Token updates
         * or incoming call invite in this fragment.
         */
        voiceBroadcastReceiver = new VoiceBroadcastReceiver();
        registerReceiver();

        // Displays a call dialog if the intent contains a call invite
        handleIncomingCallIntent(getIntent());

        // Ensure required permissions are enabled
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            if (!hasPermissions(this, Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.BLUETOOTH_CONNECT)) {
                requestPermissionForMicrophoneAndBluetooth();
            } else {
                registerForCallInvites();
            }
        } else {
            if (!hasPermissions(this, Manifest.permission.RECORD_AUDIO)) {
                requestPermissionForMicrophone();
            } else {
                registerForCallInvites();
            }
        }

        /*
         * Setup audio device management and set the volume control stream
         */
        audioSwitch = new AudioSwitch(getApplicationContext());
        savedVolumeControlStream = getVolumeControlStream();
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    private void getCommunicationsFragment() {
        if (communicationsFragment == null) {
            communicationsFragment = (CommunicationsFragment) getSupportFragmentManager().findFragmentById(getCommunicationsFragmentContainer());
        }
    }
    private void getCoordinatorLayout() {
        if (coordinatorLayout == null) {
            coordinatorLayout = findViewById(R.id.coordinator_layout);
        }
    }

    /*
     * Tear down audio device management and restore previous volume stream
     */
    @Override
    public void onDestroy() {
        audioSwitch.stop();
        setVolumeControlStream(savedVolumeControlStream);
        SoundPoolManager.getInstance(this).release();
        super.onDestroy();
    }

    public View.OnClickListener callActionFabClickListener() {
        return v -> {
            alertDialog = createCallDialog(callClickListener(), cancelCallClickListener(), CallEnabledActivity.this);
            alertDialog.show();
        };
    }

    public View.OnClickListener hangupActionFabClickListener() {
        return v -> {
            SoundPoolManager.getInstance(CallEnabledActivity.this).playDisconnect();
            getCommunicationsFragment();
            communicationsFragment.resetUI();
            disconnect();
        };
    }

    public View.OnClickListener holdActionFabClickListener() {
        return v -> hold();
    }

    public View.OnClickListener muteActionFabClickListener() {
        return v -> mute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIncomingCallIntent(intent);
    }

    public void handleIncomingCallIntent(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            activeCallInvite = intent.getParcelableExtra(Constants.INCOMING_CALL_INVITE);
            activeCallNotificationId = intent.getIntExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, 0);

            switch (action) {
                case Constants.ACTION_INCOMING_CALL:
                    handleIncomingCall();
                    break;
                case Constants.ACTION_INCOMING_CALL_NOTIFICATION:
                    showIncomingCallDialog();
                    break;
                case Constants.ACTION_CANCEL_CALL:
                    handleCancel();
                    break;
                case Constants.ACTION_FCM_TOKEN:
                    registerForCallInvites();
                    break;
                case Constants.ACTION_ACCEPT:
                    answer();
                    break;
                default:
                    break;
            }
        }
    }

    private void handleIncomingCall() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showIncomingCallDialog();
        } else {
            if (isAppVisible()) {
                showIncomingCallDialog();
            }
        }
    }

    private void showIncomingCallDialog() {
        SoundPoolManager.getInstance(this).playRinging();
        if (activeCallInvite != null) {
            alertDialog = createIncomingCallDialog(CallEnabledActivity.this,
                    activeCallInvite,
                    answerCallClickListener(),
                    cancelCallClickListener());
            alertDialog.show();
        }
    }

    private DialogInterface.OnClickListener answerCallClickListener() {
        return (dialog, which) -> {
            Log.d(TAG, "Clicked accept");
            Intent acceptIntent = new Intent(getApplicationContext(), IncomingCallNotificationService.class);
            acceptIntent.setAction(Constants.ACTION_ACCEPT);
            acceptIntent.putExtra(Constants.INCOMING_CALL_INVITE, activeCallInvite);
            acceptIntent.putExtra(Constants.INCOMING_CALL_NOTIFICATION_ID, activeCallNotificationId);
            Log.d(TAG, "Clicked accept startService");
            startService(acceptIntent);
        };
    }

    private DialogInterface.OnClickListener callClickListener() {
        return (dialog, which) -> {
            // TODO: Place a call
            EditText contact = ((AlertDialog) dialog).findViewById(R.id.contact);
            HashMap<String, String> params = new HashMap<>();
            params.put("to", contact.getText().toString());
            params.put("from", SessionManager.getUserUid());
            accessToken = getStoredTwilioToken();
            ConnectOptions connectOptions = new ConnectOptions.Builder(accessToken)
                    .params(params)
                    .build();
            activeCall = Voice.connect(this, connectOptions, callListener);
            alertDialog.dismiss();
            getCommunicationsFragment();
            communicationsFragment.setCallUI();
        };
    }

    private DialogInterface.OnClickListener cancelCallClickListener() {
        return (dialogInterface, i) -> {
            SoundPoolManager.getInstance(CallEnabledActivity.this).stopRinging();
            if (activeCallInvite != null) {
                Intent intent = new Intent(CallEnabledActivity.this, IncomingCallNotificationService.class);
                intent.setAction(Constants.ACTION_REJECT);
                intent.putExtra(Constants.INCOMING_CALL_INVITE, activeCallInvite);
                startService(intent);
            }
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        };
    }

    public static AlertDialog createIncomingCallDialog(
            Context context,
            CallInvite callInvite,
            DialogInterface.OnClickListener answerCallClickListener,
            DialogInterface.OnClickListener cancelClickListener) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp);
        alertDialogBuilder.setTitle("Incoming Call");
        alertDialogBuilder.setPositiveButton("Accept", answerCallClickListener);
        alertDialogBuilder.setNegativeButton("Reject", cancelClickListener);
        alertDialogBuilder.setMessage(callInvite.getFrom() + " is calling with " + callInvite.getCallerInfo().isVerified() + " status");
        return alertDialogBuilder.create();
    }

    private boolean isAppVisible() {
        return ProcessLifecycleOwner
                .get()
                .getLifecycle()
                .getCurrentState()
                .isAtLeast(Lifecycle.State.STARTED);
    }

    private void handleCancel() {
        if (alertDialog != null && alertDialog.isShowing()) {
            SoundPoolManager.getInstance(this).stopRinging();
            alertDialog.cancel();
        }
    }

    /*
     * Register your FCM token with Twilio to receive incoming call invites
     */
    private void registerForCallInvites() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this, tokenResult -> {
            if (tokenResult != null) {
                String accessToken = getStoredTwilioToken();
                Log.i(TAG, accessToken + " Registering with FCM" + tokenResult);
                Voice.register(accessToken, Voice.RegistrationChannel.FCM, tokenResult, registrationListener);
            }
        });
    }

    private RegistrationListener registrationListener() {
        return new RegistrationListener() {
            @Override
            public void onRegistered(@NonNull String accessToken, @NonNull String fcmToken) {
                Log.d(TAG, "Successfully registered FCM " + fcmToken);
            }

            @Override
            public void onError(@NonNull RegistrationException error,
                                @NonNull String accessToken,
                                @NonNull String fcmToken) {
                String message = String.format(
                        Locale.US,
                        "Registration Error: %d, %s, AccessToken: %s",
                        error.getErrorCode(),
                        error.getMessage(),
                        accessToken);
                Log.e(TAG, message);
                getCoordinatorLayout();
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
            }
        };
    }

    /**
     * Accept an incoming Call
     */
    private void answer() {
        SoundPoolManager.getInstance(this).stopRinging();
        activeCallInvite.accept(this, callListener);
        notificationManager.cancel(activeCallNotificationId);
        stopService(new Intent(getApplicationContext(), IncomingCallNotificationService.class));
        getCommunicationsFragment();
        communicationsFragment.setCallUI();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    /**
     * Disconnect from Call
     */
    private void disconnect() {
        if (activeCall != null) {
            activeCall.disconnect();
            activeCall = null;
        }
    }

    private void hold() {
        if (activeCall != null) {
            boolean hold = !activeCall.isOnHold();
            activeCall.hold(hold);
            getCommunicationsFragment();
            communicationsFragment.applyFabState("hold", hold);
        }
    }

    private void mute() {
        if (activeCall != null) {
            boolean mute = !activeCall.isMuted();
            activeCall.mute(mute);
            getCommunicationsFragment();
            communicationsFragment.applyFabState("mute", mute);
        }
    }

    private void applyFabState(FloatingActionButton button, boolean enabled) {
        // Set fab as pressed when call is on hold
        ColorStateList colorStateList = enabled ?
                ColorStateList.valueOf(ContextCompat.getColor(this,
                        R.color.colorPrimaryDark)) :
                ColorStateList.valueOf(ContextCompat.getColor(this,
                        R.color.colorAccent));
        button.setBackgroundTintList(colorStateList);
    }

//    /*
//     * The UI state when there is an active call
//     */
//    private void setCallUI() {
//        callActionFab.hide();
//        hangupActionFab.show();
//        holdActionFab.show();
//        muteActionFab.show();
//        chronometer.setVisibility(View.VISIBLE);
//        chronometer.setBase(SystemClock.elapsedRealtime());
//        chronometer.start();
//    }

//    /*
//     * Reset UI elements
//     */
//    private void resetUI() {
//        callActionFab.show();
//        muteActionFab.setImageDrawable(ContextCompat.getDrawable(CallEnabledActivity.this, R.drawable.ic_mic_white_24dp));
//        holdActionFab.hide();
//        holdActionFab.setBackgroundTintList(ColorStateList
//                .valueOf(ContextCompat.getColor(CallEnabledActivity.this, R.color.colorAccent)));
//        muteActionFab.hide();
//        hangupActionFab.hide();
//        chronometer.setVisibility(View.INVISIBLE);
//        chronometer.stop();
//    }

    private Call.Listener callListener() {
        return new Call.Listener() {
            /*
             * This callback is emitted once before the Call.Listener.onConnected() callback when
             * the callee is being alerted of a Call. The behavior of this callback is determined by
             * the answerOnBridge flag provided in the Dial verb of your TwiML application
             * associated with this client. If the answerOnBridge flag is false, which is the
             * default, the Call.Listener.onConnected() callback will be emitted immediately after
             * Call.Listener.onRinging(). If the answerOnBridge flag is true, this will cause the
             * call to emit the onConnected callback only after the call is answered.
             * See answeronbridge for more details on how to use it with the Dial TwiML verb. If the
             * twiML response contains a Say verb, then the call will emit the
             * Call.Listener.onConnected callback immediately after Call.Listener.onRinging() is
             * raised, irrespective of the value of answerOnBridge being set to true or false
             */
            @Override
            public void onRinging(@NonNull Call call) {
                Log.d(TAG, "Ringing");
                /*
                 * When [answerOnBridge](https://www.twilio.com/docs/voice/twiml/dial#answeronbridge)
                 * is enabled in the <Dial> TwiML verb, the caller will not hear the ringback while
                 * the call is ringing and awaiting to be accepted on the callee's side. The application
                 * can use the `SoundPoolManager` to play custom audio files between the
                 * `Call.Listener.onRinging()` and the `Call.Listener.onConnected()` callbacks.
                 */
                SoundPoolManager.getInstance(CallEnabledActivity.this).playRinging();
            }

            @Override
            public void onConnectFailure(@NonNull Call call, @NonNull CallException error) {
                audioSwitch.deactivate();
                SoundPoolManager.getInstance(CallEnabledActivity.this).stopRinging();
                Log.d(TAG, "Connect failure");
                String message = String.format(
                        Locale.US,
                        "Call Error: %d, %s",
                        error.getErrorCode(),
                        error.getMessage());
                Log.e(TAG, message);
                getCoordinatorLayout();
                getCommunicationsFragment();
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
                communicationsFragment.resetUI();
            }

            @Override
            public void onConnected(@NonNull Call call) {
                audioSwitch.activate();
                SoundPoolManager.getInstance(CallEnabledActivity.this).stopRinging();
                Log.d(TAG, "Connected");
                activeCall = call;
            }

            @Override
            public void onReconnecting(@NonNull Call call, @NonNull CallException callException) {
                Log.d(TAG, "onReconnecting");
            }

            @Override
            public void onReconnected(@NonNull Call call) {
                Log.d(TAG, "onReconnected");
            }

            @Override
            public void onDisconnected(@NonNull Call call, CallException error) {
                audioSwitch.deactivate();
                SoundPoolManager.getInstance(CallEnabledActivity.this).stopRinging();
                Log.d(TAG, "Disconnected");
                if (error != null) {
                    String message = String.format(
                            Locale.US,
                            "Call Error: %d, %s",
                            error.getErrorCode(),
                            error.getMessage());
                    Log.e(TAG, message);
                    getCoordinatorLayout();
                    Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
                }
                getCommunicationsFragment();
                communicationsFragment.resetUI();
            }
            /*
             * currentWarnings: existing quality warnings that have not been cleared yet
             * previousWarnings: last set of warnings prior to receiving this callback
             *
             * Example:
             *   - currentWarnings: { A, B }
             *   - previousWarnings: { B, C }
             *
             * Newly raised warnings = currentWarnings - intersection = { A }
             * Newly cleared warnings = previousWarnings - intersection = { C }
             */
            public void onCallQualityWarningsChanged(@NonNull Call call,
                                                     @NonNull Set<Call.CallQualityWarning> currentWarnings,
                                                     @NonNull Set<Call.CallQualityWarning> previousWarnings) {

                if (previousWarnings.size() > 1) {
                    Set<Call.CallQualityWarning> intersection = new HashSet<>(currentWarnings);
                    currentWarnings.removeAll(previousWarnings);
                    intersection.retainAll(previousWarnings);
                    previousWarnings.removeAll(intersection);
                }

                String message = String.format(
                        Locale.US,
                        "Newly raised warnings: " + currentWarnings + " Clear warnings " + previousWarnings);
                Log.e(TAG, message);
                getCoordinatorLayout();
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
            }
        };
    }

    private class VoiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && (action.equals(Constants.ACTION_INCOMING_CALL) || action.equals(Constants.ACTION_CANCEL_CALL))) {
                // Handle the incoming or cancelled call invite
                handleIncomingCallIntent(intent);
            }
        }
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.ACTION_INCOMING_CALL);
            intentFilter.addAction(Constants.ACTION_CANCEL_CALL);
            intentFilter.addAction(Constants.ACTION_FCM_TOKEN);
            LocalBroadcastManager.getInstance(this).registerReceiver(
                    voiceBroadcastReceiver, intentFilter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterReceiver() {
        if (isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(voiceBroadcastReceiver);
            isReceiverRegistered = false;
        }
    }

    private void startAudioSwitch() {
        /*
         * Start the audio device selector after the menu is created and update the icon when the
         * selected audio device changes.
         */
        audioSwitch.start((audioDevices, audioDevice) -> {
            Log.d(TAG, "Updating AudioDeviceIcon");
            updateAudioDeviceIcon(audioDevice);
            return Unit.INSTANCE;
        });
    }

    /*
     * Update the menu icon based on the currently selected audio device.
     */
    private void updateAudioDeviceIcon(AudioDevice selectedAudioDevice) {
        int audioDeviceMenuIcon = R.drawable.ic_phonelink_ring_white_24dp;

        if (selectedAudioDevice instanceof AudioDevice.BluetoothHeadset) {
            audioDeviceMenuIcon = R.drawable.ic_bluetooth_white_24dp;
        } else if (selectedAudioDevice instanceof AudioDevice.WiredHeadset) {
            audioDeviceMenuIcon = R.drawable.ic_headset_mic_white_24dp;
        } else if (selectedAudioDevice instanceof AudioDevice.Earpiece) {
            audioDeviceMenuIcon = R.drawable.ic_phonelink_ring_white_24dp;
        } else if (selectedAudioDevice instanceof AudioDevice.Speakerphone) {
            audioDeviceMenuIcon = R.drawable.ic_volume_up_white_24dp;
        }

        if (audioDeviceMenuItem != null) {
            audioDeviceMenuItem.setIcon(audioDeviceMenuIcon);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestPermissionForMicrophoneAndBluetooth() {
        if (!hasPermissions(this, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.BLUETOOTH_CONNECT)) {
            requestPermissions(
                    new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.BLUETOOTH_CONNECT},
                    PERMISSIONS_REQUEST_CODE);
        } else {
            registerForCallInvites();
        }
    }

    private void requestPermissionForMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            getCoordinatorLayout();
            Snackbar.make(coordinatorLayout,
                    "Microphone permissions needed. Please allow in your application settings.",
                    Snackbar.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION_REQUEST_CODE);
        }
    }

    private static AlertDialog createCallDialog(final DialogInterface.OnClickListener callClickListener,
                                                final DialogInterface.OnClickListener cancelClickListener,
                                                final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        alertDialogBuilder.setIcon(R.drawable.ic_call_black_24dp);
        alertDialogBuilder.setTitle("Call");
        alertDialogBuilder.setPositiveButton("Call", callClickListener);
        alertDialogBuilder.setNegativeButton("Cancel", cancelClickListener);
        alertDialogBuilder.setCancelable(false);

        LayoutInflater li = LayoutInflater.from(activity);
        View dialogView = li.inflate(
                R.layout.dialog_call,
                activity.findViewById(android.R.id.content),
                false);
        final EditText contact = dialogView.findViewById(R.id.contact);
        contact.setHint(R.string.callee);
        alertDialogBuilder.setView(dialogView);

        return alertDialogBuilder.create();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
         * Check if required permissions are granted
         */
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        getCoordinatorLayout();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermissions(this, Manifest.permission.RECORD_AUDIO)) {
                Snackbar.make(coordinatorLayout,
                        "Microphone permission needed. Please allow in your application settings.",
                        Snackbar.LENGTH_LONG).show();
            } else {
                if (!hasPermissions(this, Manifest.permission.BLUETOOTH_CONNECT)) {
                    Snackbar.make(coordinatorLayout,
                            "Without bluetooth permission app will fail to use bluetooth.",
                            Snackbar.LENGTH_LONG).show();
                }
                /*
                 * Due to bluetooth permissions being requested at the same time as mic
                 * permissions, AudioSwitch should be started after providing the user the option
                 * to grant the necessary permissions for bluetooth.
                 */
                startAudioSwitch();
                registerForCallInvites();
            }
        } else {
            if (!hasPermissions(this, Manifest.permission.RECORD_AUDIO)) {
                Snackbar.make(coordinatorLayout,
                        "Microphone permissions needed. Please allow in your application settings.",
                        Snackbar.LENGTH_LONG).show();
            } else {
                startAudioSwitch();
                registerForCallInvites();
            }
        }
    }
}
