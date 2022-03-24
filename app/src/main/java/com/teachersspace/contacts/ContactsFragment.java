package com.teachersspace.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.teachersspace.R;
import com.teachersspace.auth.SessionManager;
import com.teachersspace.models.User;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {
    private static final String TAG = "ContactsFragment";
    private SessionManager sessionManager;
    private EditText searchBar;

    private ContactsViewModel vm;
    private ContactsAdapter contactsAdapter;
    private List<User> contacts = new ArrayList<>();

    public ContactsIndividualListener contactsIndividualListenerFactory(int position) {
        return new ContactsIndividualListener(position);
    }
    private class ContactsIndividualListener implements View.OnClickListener {
        private final int itemPosition;

        public ContactsIndividualListener(int position) {
            itemPosition = position;
        }

        @Override
        public void onClick(View view) {
            User selectedUser = contacts.get(itemPosition);
            Log.i(TAG, "testing: " + itemPosition + " " + selectedUser.getUid());
            NavDirections directions = new NavDirections() {
                @Override
                public int getActionId() {
                    return R.id.navigate_single_contact_action;
                }

                @NonNull
                @Override
                public Bundle getArguments() {
                    Bundle args = new Bundle();
                    args.putString("contact", selectedUser.serialise());
                    return args;
                }
            };
            navigate(directions);
        }
    }

    private class ContactsObserver implements Observer<List<User>> {
        @Override
        public void onChanged(List<User> updatedContacts) {
            // update UI by updating adapter here when user contacts changes
            // (e.g. new sorting order due to change in activity etc.)
            Log.d(TAG, "contacts list updated");
            contacts = updatedContacts;
            Log.d(TAG, contacts.toString());
            contactsAdapter.updateLocalData(contacts);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.contacts_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.sessionManager = new SessionManager(getContext());
        searchBar = view.findViewById(R.id.search_bar);
        searchBar.setOnFocusChangeListener(navigateToSearch());

        // setup viewmodel and callbacks
        vm = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);
        vm.getContacts(this.sessionManager.getCurrentUser().getUid());
        vm.getContactsSorted().observe(getViewLifecycleOwner(), new ContactsObserver());
        // setup recyclerview
        RecyclerView contactsRecyclerView = view.findViewById(R.id.contactsList);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsAdapter = new ContactsAdapter(contacts, this);
        contactsRecyclerView.setAdapter(contactsAdapter);

        // TODO: remove the below after navbar is ready
        Button settingsButtonTest = view.findViewById(R.id.settings_test);
        settingsButtonTest.setOnClickListener(navigateToSettings());
    }

    private View.OnFocusChangeListener navigateToSearch() {
        return (view, hasFocus) -> {
            if (hasFocus) {
                NavDirections directions = new NavDirections() {
                    @NonNull
                    @Override
                    public Bundle getArguments() {
                        return new Bundle();
                    }

                    @Override
                    public int getActionId() {
                        return R.id.navigate_search_action;
                    }
                };
                Log.i(TAG, "nav from contacts to search");
                navigate(directions);
            }
        };
    }

    // TODO: remove this, interim method before navbar is ready
    private View.OnClickListener navigateToSettings() {
        return view -> {
            NavDirections directions = new NavDirections() {
                @Override
                public int getActionId() {
                    return R.id.navigate_settings_action;
                }

                @NonNull
                @Override
                public Bundle getArguments() {
                    return new Bundle();
                }
            };
            Log.i(TAG, "nav from contacts to settings");
            navigate(directions);
        };
    }

    private void navigate(NavDirections directions) {
        NavHostFragment.findNavController(ContactsFragment.this).navigate(directions);
    }
}
