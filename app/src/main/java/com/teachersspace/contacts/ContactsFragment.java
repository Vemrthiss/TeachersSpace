package com.teachersspace.contacts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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

    private ContactsViewModel vm;
    private ContactsAdapter contactsAdapter;
    private List<User> contacts = new ArrayList<>();

    private class ContactsObserver implements Observer<List<User>> {
        @Override
        public void onChanged(List<User> updatedContacts) {
            // update UI by updating adapter here when user contacts changes
            // (e.g. new sorting order due to change in activity etc.)
            Log.d(TAG, "contacts list updated");
            Log.d(TAG, contacts.toString());
            contactsAdapter.updateLocalData(updatedContacts);
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

        // setup viewmodel and callbacks
        vm = new ViewModelProvider(requireActivity()).get(ContactsViewModel.class);
        vm.getContacts(this.sessionManager.getCurrentUser().getUid());
        vm.getContactsSorted().observe(getViewLifecycleOwner(), new ContactsObserver());
        // setup recyclerview
        RecyclerView contactsRecyclerView = view.findViewById(R.id.contactsList);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactsAdapter = new ContactsAdapter(contacts);
        contactsRecyclerView.setAdapter(contactsAdapter);
    }
}
