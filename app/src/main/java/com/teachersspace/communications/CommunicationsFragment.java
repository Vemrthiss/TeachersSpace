package com.teachersspace.communications;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.teachersspace.R;
import com.teachersspace.models.User;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link CommunicationsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class CommunicationsFragment extends Fragment {
    private static final String TAG = "CommunicationsFragment";

    private CommunicationsViewModel communicationsViewModel;

    private FloatingActionButton callActionFab;
    private FloatingActionButton hangupActionFab;
    private FloatingActionButton holdActionFab;
    private FloatingActionButton muteActionFab;
    private Chronometer chronometer;
    private TextView contactNameView;

    public interface CommunicationsFragmentProps {
        View.OnClickListener callActionFabClickListener();
        View.OnClickListener hangupActionFabClickListener();
        View.OnClickListener holdActionFabClickListener();
        View.OnClickListener muteActionFabClickListener();
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
        communicationsViewModel = new ViewModelProvider(requireActivity()).get(CommunicationsViewModel.class);

        callActionFab = view.findViewById(R.id.call_action_fab);
        hangupActionFab = view.findViewById(R.id.hangup_action_fab);
        holdActionFab = view.findViewById(R.id.hold_action_fab);
        muteActionFab = view.findViewById(R.id.mute_action_fab);
        chronometer = view.findViewById(R.id.chronometer);
        contactNameView = view.findViewById(R.id.communications_contact_name);

        // register click event listeners
        callActionFab.setOnClickListener(props.callActionFabClickListener());
        hangupActionFab.setOnClickListener(props.hangupActionFabClickListener());
        holdActionFab.setOnClickListener(props.holdActionFabClickListener());
        muteActionFab.setOnClickListener(props.muteActionFabClickListener());

        // setup the UI
        resetUI();

        // get contact uid
        Bundle args = getArguments();
        if (args != null) {
            User activeContact = User.deserialise(args.getString("contact"));
            Log.i(TAG, "contact name: " + activeContact.getName());
            contactNameView.setText(activeContact.getName());
            communicationsViewModel.setActiveContact(activeContact);

            boolean externalAccept = args.getBoolean("externalAccept");
            if (externalAccept) {
                setCallUI();
            }
        }
    }

    /*
     * The UI state when there is an active call
     */
    public void setCallUI() {
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
        callActionFab.show();
        muteActionFab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_mic_white_24dp));
        holdActionFab.hide();
        holdActionFab.setBackgroundTintList(ColorStateList
                .valueOf(ContextCompat.getColor(getContext(), R.color.colorAccent)));
        muteActionFab.hide();
        hangupActionFab.hide();
        chronometer.setVisibility(View.INVISIBLE);
        chronometer.stop();
    }

    public void applyFabState(String buttonWhich, boolean enabled) {
        // Set fab as pressed when call is on hold
        ColorStateList colorStateList = enabled ?
                ColorStateList.valueOf(ContextCompat.getColor(getContext(),
                        R.color.colorPrimaryDark)) :
                ColorStateList.valueOf(ContextCompat.getColor(getContext(),
                        R.color.colorAccent));

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

    //    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    public CommunicationsFragment() {
//        // Required empty public constructor
//    }
//
//    // twilio access token
//    private String getStoredTwilioToken() {
//        if (tokenManager == null) {
//            tokenManager = new TwilioTokenManager(getContext(), SessionManager.getUserUid());
//        }
//        return tokenManager.getTwilioAccessToken();
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment CommunicationsFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static CommunicationsFragment newInstance(String param1, String param2) {
//        CommunicationsFragment fragment = new CommunicationsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }
}