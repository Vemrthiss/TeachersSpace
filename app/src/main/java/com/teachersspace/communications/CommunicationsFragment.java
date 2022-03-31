package com.teachersspace.communications;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.teachersspace.R;
import com.teachersspace.auth.SessionManager;
import com.teachersspace.helpers.TimeFormatter;
import com.teachersspace.models.Message;
import com.teachersspace.models.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.teachersspace.data.MessageRepository;


public class CommunicationsFragment extends Fragment {
    private static final String TAG = "CommunicationsFragment";

    private MessageRepository messageRepository;

    private CommunicationsViewModel vm;

    private FloatingActionButton callActionFab;
    private FloatingActionButton hangupActionFab;
    private FloatingActionButton holdActionFab;
    private FloatingActionButton muteActionFab;
    private Chronometer chronometer;
    private TextView contactNameView;
    private TextView contactHoursView;
    private TextView contactTypeView;
    private TextView contactNameActiveCallView;
    private ConstraintLayout inactiveCallLayout;
    private ConstraintLayout activeCallLayout;
    private FloatingActionButton backActionFab;

    private BottomNavigationView navbar;

    //==== Messages ===//
    private TextView staticMessage;
    private EditText inputMessage;
    private Button sendMessageButton;
    private Message message;
    private String senderUID;

    public interface CommunicationsFragmentProps {
        View.OnClickListener callActionFabClickListener();
        View.OnClickListener hangupActionFabClickListener();
        View.OnClickListener holdActionFabClickListener();
        View.OnClickListener muteActionFabClickListener();
        //View.OnClickListener sendMessageButtonClickListener();
    }
    private CommunicationsFragmentProps props;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        props = (CommunicationsFragmentProps) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_communications, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // setup communications view model
        vm = new ViewModelProvider(requireActivity()).get(CommunicationsViewModel.class);

        callActionFab = view.findViewById(R.id.call_action_fab);
        hangupActionFab = view.findViewById(R.id.hangup_action_fab);
        holdActionFab = view.findViewById(R.id.hold_action_fab);
        muteActionFab = view.findViewById(R.id.mute_action_fab);
        chronometer = view.findViewById(R.id.chronometer);
        contactNameView = view.findViewById(R.id.communications_contact_name);
        contactHoursView = view.findViewById(R.id.communications_contact_hours);
        contactTypeView = view.findViewById(R.id.communications_contact_type);
        contactNameActiveCallView = view.findViewById(R.id.communications_contact_name_active_call);
        inactiveCallLayout = view.findViewById(R.id.communications_no_call_container);
        activeCallLayout = view.findViewById(R.id.communications_with_call_container);
        backActionFab = view.findViewById(R.id.back_action_fab);
        staticMessage = view.findViewById(R.id.staticMessage);
        inputMessage = view.findViewById(R.id.inputMessage);
        //sendMessageButton = view.findViewById(R.id.sendMessageButton);

        // register click event listeners
        callActionFab.setOnClickListener(props.callActionFabClickListener());
        hangupActionFab.setOnClickListener(props.hangupActionFabClickListener());
        holdActionFab.setOnClickListener(props.holdActionFabClickListener());
        muteActionFab.setOnClickListener(props.muteActionFabClickListener());
        backActionFab.setOnClickListener(goBack());
        //sendMessageButton.setOnClickListener( new SendMessageButtonClickListener() );

        // setup the UI
        resetUI();

        // Get current user
        User user = new SessionManager(getContext()).getCurrentUser();

        // get contact uid
        Bundle args = getArguments();
        if (args != null) {
            User activeContact = User.deserialise(args.getString("contact"));
            String activeContactName = activeContact.getName();
            contactNameView.setText(activeContactName);
            contactTypeView.setText(activeContact.getUserType().toString());
            contactNameActiveCallView.setText(activeContactName); // for the active call UI
            vm.setActiveContact(activeContact);
            vm.watchActiveContact();
            vm.getIsOutsideOfficeHours().observe(getViewLifecycleOwner(), new ActiveContactOfficeHoursObserver());
            vm.getActiveContact().observe(getViewLifecycleOwner(), new ActiveContactObserver());
            //sendMessageButton.setOnClickListener( new SendMessageButtonClickListener() );

            // Initialise MessageRepository instance
            messageRepository = new MessageRepository();
            if (user.getUserType() == User.UserType.TEACHER) {
                messageRepository.setChatUID(user.getUid() + "-" + activeContact.getUid());
            }
            else{
                messageRepository.setChatUID(activeContact.getUid() + "-" + user.getUid());
            }

            messageRepository.subscribeToMessageUpdates(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    ArrayList<Message> messages = new ArrayList<Message>();
                    for (DocumentSnapshot doc : value) {
                        messages.add(doc.toObject(Message.class));
                    }
                    LogMessages(messages);
                }
            });


            // send messages

//            class SendMessageButtonClickListener implements View.OnClickListener {
//                @Override
//                public void onClick(View view){
//                    //for user UID
//                    if (user.getUserType() == User.UserType.TEACHER) {
//                        senderUID = user.getUid();
//                    } else {
//                        senderUID = activeContact.getUid();
//                    }
//                    //for message body
//                    String message_string = inputMessage.getText().toString();
//                    // for date/time
//                    Date timeSent = new Date();
//
//                    //instantiation and posting
//                    message = new Message(message_string, senderUID, timeSent);
//                    messageRepository.postMessage(message);
//                }
//            }

            boolean externalAccept = args.getBoolean("externalAccept");
            if (externalAccept) {
                setCallUI();
            }
        }
    }

    // For testing subscriber function
    public void LogMessages(ArrayList<Message> messages){
        for(Message message : messages){
            Log.d(TAG, message.getBody());
        }
    }

    /*
     * The UI state when there is an active call
     */
    public void setCallUI() {
        toggleNavbar(false);
        inactiveCallLayout.setVisibility(View.INVISIBLE);
        activeCallLayout.setVisibility(View.VISIBLE);
        callActionFab.hide();
        hangupActionFab.show();
        holdActionFab.show();
        muteActionFab.show();
        chronometer.setVisibility(View.VISIBLE);
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.start();
    }

    /*
     * Reset UI elements
     */
    public void resetUI() {
        toggleNavbar(false);
        inactiveCallLayout.setVisibility(View.VISIBLE);
        activeCallLayout.setVisibility(View.INVISIBLE);
        callActionFab.show();
        muteActionFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_mic_white_24dp));
        holdActionFab.hide();
        holdActionFab.setBackgroundTintList(ColorStateList
                .valueOf(ContextCompat.getColor(getContext(), R.color.blue_200)));
        muteActionFab.setBackgroundTintList(ColorStateList
                .valueOf(ContextCompat.getColor(getContext(), R.color.blue_200)));
        muteActionFab.hide();
        hangupActionFab.hide();
        chronometer.setVisibility(View.INVISIBLE);
        chronometer.stop();
    }

    public void applyFabState(String buttonWhich, boolean enabled) {
        // Set fab as pressed when call is on hold
        ColorStateList colorStateList = enabled ?
                ColorStateList.valueOf(ContextCompat.getColor(getContext(),
                        R.color.brown_200)) :
                ColorStateList.valueOf(ContextCompat.getColor(getContext(),
                        R.color.blue_200));

        FloatingActionButton button;
        if (buttonWhich.equals("hold")) {
            button = holdActionFab;
        } else if (buttonWhich.equals("mute")) {
            button = muteActionFab;
        } else {
            button = holdActionFab; // default
        }
        button.setBackgroundTintList(colorStateList);
    }

    private View.OnClickListener goBack() {
        return view -> {
            NavDirections directions = new NavDirections() {
                @NonNull
                @Override
                public Bundle getArguments() {
                    return new Bundle();
                }

                @Override
                public int getActionId() {
                    return R.id.navigate_back_contacts_action;
                }
            };
            NavHostFragment.findNavController(CommunicationsFragment.this).navigate(directions);
            toggleNavbar(true);
        };
    }

    @Override
    public void onDestroyView() {
        vm.deregisterListener();
        toggleNavbar(true);
        super.onDestroyView();
    }

    private class ActiveContactOfficeHoursObserver implements Observer<Boolean> {
        @Override
        public void onChanged(Boolean isOutsideOfficeHours) {
            // set icon
            if (isOutsideOfficeHours) {
                callActionFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_call_white_disabled_24));
            } else {
                callActionFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_call_white_24dp));
            }
        }
    }

    private class ActiveContactObserver implements Observer<User> {
        @Override
        public void onChanged(User activeContact) {
            if (activeContact.getUserType() == User.UserType.TEACHER) {
                // set office hours text
                Date startOfficeHours = activeContact.getOfficeStart();
                Date endOfficeHours = activeContact.getOfficeEnd();
                Calendar startCalendar = Calendar.getInstance();
                Calendar endCalendar = Calendar.getInstance();
                if (startOfficeHours != null) {
                    startCalendar.setTime(startOfficeHours);
                }
                if (endOfficeHours != null) {
                    endCalendar.setTime(endOfficeHours);
                }

                String startTiming = TimeFormatter.formatTime(startCalendar);
                String endTiming = TimeFormatter.formatTime(endCalendar);

                Activity activity = requireActivity();
                final String officeHoursText = activity.getString(R.string.contact_office_hours, startTiming, endTiming);
                contactHoursView.setVisibility(View.VISIBLE);
                contactHoursView.setText(officeHoursText);
            }
        }
    }

    private void toggleNavbar(boolean toShow) {
        navbar = requireActivity().findViewById(R.id.bottomNavigationViewTeacher);
        if (navbar == null) {
            navbar = requireActivity().findViewById(R.id.bottomNavigationViewParent);
        }

        if (toShow) {
            navbar.setVisibility(View.VISIBLE);
        } else {
            navbar.setVisibility(View.GONE);
        }
    }

}