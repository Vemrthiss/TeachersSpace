package com.teachersspace.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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

public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private SessionManager sessionManager;
    private EditText searchBar;

    private SearchViewModel vm;
    private SearchAdapter searchAdapter;
    private List<User> users = new ArrayList<>(); // list of users to show in RecyclerView

    public SearchFragment.SearchIndividualListener searchIndividualListenerFactory(int position) {
        return new SearchFragment.SearchIndividualListener(position);
    }
    private class SearchIndividualListener implements View.OnClickListener {
        private final int itemPosition;

        public SearchIndividualListener(int position) {
            itemPosition = position;
        }

        @Override
        public void onClick(View view) {
            User selectedUser = users.get(itemPosition);
            Log.i(TAG, "testing: " + itemPosition + " " + selectedUser.getUid());
            NavDirections directions = new NavDirections() {
                @Override
                public int getActionId() {
                    return R.id.navigate_single_result_action;
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

    private class SearchObserver implements Observer<List<User>> {
        @Override
        public void onChanged(List<User> updatedResults) {
            // update UI by updating adapter here when user contacts changes
            // (e.g. new sorting order due to change in activity etc.)
            Log.d(TAG, "search list updated with contacts");
            users = updatedResults;
            Log.d(TAG, users.toString());
            searchAdapter.updateLocalData(users);
            Log.d(TAG, "adapter searchList: " + searchAdapter.getSearchList());
            Log.d(TAG, "adapter resultList: " + searchAdapter.getResultList());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.sessionManager = new SessionManager(getContext());
        searchBar = view.findViewById(R.id.search_bar_contact);
        searchBar.addTextChangedListener(new TextChangedListener<EditText>(searchBar) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                users = searchAdapter.filter(s.toString());
                Log.i(TAG, "Query: " + s);
                //searchAdapter.updateLocalData(users);
                Log.d(TAG, "search list updated");
            }
        });

        focusSearchBar();

        User currentUser = this.sessionManager.getCurrentUser();

        // setup viewmodel and callbacks
        vm = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        vm.getUsers(currentUser).observe(getViewLifecycleOwner(), new SearchFragment.SearchObserver());
        //vm.getContacts(currentUser.getUid());
        //vm.getContactsSorted().observe(getViewLifecycleOwner(), new SearchFragment.SearchObserver());

        // setup recyclerview
        RecyclerView searchRecyclerView = view.findViewById(R.id.searchList);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchAdapter = new SearchAdapter(users, this);
        searchRecyclerView.setAdapter(searchAdapter);

    }


    private void focusSearchBar() {
        searchBar.requestFocus();
        Context ctx = getContext();
        if (ctx != null) {
            // shows keyboard
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void unfocusSearchBar() {
        searchBar.clearFocus();
    }

    private void navigate(NavDirections directions) {
        NavHostFragment.findNavController(SearchFragment.this).navigate(directions);
    }
}
