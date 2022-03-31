package com.teachersspace.communications;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.teachersspace.R;
import com.teachersspace.models.Message;
import com.teachersspace.models.User;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static final String TAG = "MessageAdapter";
    private ArrayList<Message> messages; // all messages in chat
    private final CommunicationsFragment communicationsFragment;
    private final String senderUid;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView sentMessage;
        private final TextView receivedMessage;

        public ViewHolder(View view) {
            super(view);
            receivedMessage = view.findViewById(R.id.message_item_other);
            sentMessage = view.findViewById(R.id.message_item_self);
        }

        public TextView getReceivedMessage() {
            return receivedMessage;
        }
        public TextView getSentMessage() {
            return sentMessage;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     * @param dataSet ArrayList<Message> containing the data to populate views to be used by RecyclerView.
     */
    public MessageAdapter(String uid, ArrayList<Message> dataSet, CommunicationsFragment fragment) {
        Log.d(TAG, "dataset: " + dataSet);
        senderUid = uid;
        messages = dataSet;
        communicationsFragment = fragment;
    }

    /**
     * Create new views (invoked by the layout manager)
     * @param parent Parent ViewGroup
     * @param viewType The view type of the new View.
     * @return viewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);

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
        TextView sentMessage = holder.getSentMessage();
        TextView receivedMessage = holder.getReceivedMessage();
        Message message = messages.get(position);
        if (senderUid.equals(message.getSenderUID())) {
            sentMessage.setVisibility(View.VISIBLE);
            receivedMessage.setVisibility(View.GONE);
            sentMessage.setText(message.getBody());
        } else {
            sentMessage.setVisibility(View.GONE);
            receivedMessage.setVisibility(View.VISIBLE);
            receivedMessage.setText(message.getBody());
        }

    }
    /**
     * @return the size of dataset (invoked by the layout manager)
     */
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void updateLocalData(ArrayList<Message> newData) {
        messages.clear();
        messages.addAll(newData);
        this.notifyDataSetChanged();
        // TODO: https://stackoverflow.com/questions/68602157/it-will-always-be-more-efficient-to-use-more-specific-change-events-if-you-can
    }
}
