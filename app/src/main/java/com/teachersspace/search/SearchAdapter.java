package com.teachersspace.search;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teachersspace.R;
import com.teachersspace.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private static final String TAG = "SearchAdapter";
    private List<User> searchList;
    private ArrayList<User> resultList;
    private SearchFragment searchFragment;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final Button buttonView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            buttonView = (Button) view.findViewById(R.id.contactItemButton);
        }

        public Button getButtonView() {
            return buttonView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     * @param dataSet List<User> containing the data to populate views to be used by RecyclerView.
     */
    public SearchAdapter(List<User> dataSet, SearchFragment fragment) {
        searchList = dataSet;
        searchFragment = fragment;
        resultList = (ArrayList) dataSet;
    }

    /**
     * Create new views (invoked by the layout manager)
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_item, parent, false);

        return new ViewHolder(view);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        User user = resultList.get(position);
        Button singleContactButton = holder.getButtonView();
        singleContactButton.setText(user.getName());
        singleContactButton.setOnClickListener(searchFragment.contactsIndividualListenerFactory(position));
    }
    /**
     * @return the size of dataset (invoked by the layout manager)
     */
    @Override
    public int getItemCount() {
        return searchList.size();
    }

    public void updateLocalData(List<User> newData) {
        resultList.clear();
        resultList.addAll(newData);
        this.notifyDataSetChanged();
        // TODO: https://stackoverflow.com/questions/68602157/it-will-always-be-more-efficient-to-use-more-specific-change-events-if-you-can
    }

    /**
     * Filter contacts by query string
     * @param query
     */
    public void filter(String query) {
        query = query.toLowerCase(Locale.getDefault());
        resultList.clear();
        if (query.length() == 0) {
            Log.i(TAG, "Query length 0");
            resultList.addAll(searchList);
        } else {
            for (User user: searchList) {
                if (user.getName().toLowerCase(Locale.getDefault()).contains(query)) {
                    resultList.add(user);
                }
            }
            Log.i(TAG, "Filtered by query");
        }
        this.notifyDataSetChanged();
    }
}
