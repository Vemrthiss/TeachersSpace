package com.teachersspace.contacts;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teachersspace.R;
import com.teachersspace.models.User;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private static final String TAG = "ContactsAdapter";
    private List<User> contactsList;
    private ContactsFragment contactsFragment;

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
    public ContactsAdapter(List<User> dataSet, ContactsFragment fragment) {
        contactsList = dataSet;
        contactsFragment = fragment;
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
        User user = contactsList.get(position);
        Button singleContactButton = holder.getButtonView();
        singleContactButton.setText(user.getName());
        singleContactButton.setOnClickListener(contactsFragment.contactsIndividualListenerFactory(position));
    }
    /**
     * @return the size of dataset (invoked by the layout manager)
     */
    @Override
    public int getItemCount() {
        return contactsList.size();
    }

    public void updateLocalData(List<User> newData) {
        contactsList.clear();
        contactsList.addAll(newData);
        this.notifyDataSetChanged();
        // TODO: https://stackoverflow.com/questions/68602157/it-will-always-be-more-efficient-to-use-more-specific-change-events-if-you-can
    }
}
