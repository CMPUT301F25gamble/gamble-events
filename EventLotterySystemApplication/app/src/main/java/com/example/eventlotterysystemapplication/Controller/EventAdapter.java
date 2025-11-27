package com.example.eventlotterysystemapplication.Controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.eventlotterysystemapplication.Model.EntrantStatus;
import com.example.eventlotterysystemapplication.Model.Event;
import com.example.eventlotterysystemapplication.R;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class EventAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final List<Event> events;

    // OPTIONAL: used for User events history screen
    private List<EntrantStatus> statuses = null;

    public EventAdapter(Context context, List<Event> events) {
        super(context, R.layout.event_list_item, events);
        this.context = context;
        this.events = events;
    }

    // Used ONLY FOR user events history screen
    public EventAdapter(Context context, List<Event> events, List<EntrantStatus> statuses) {
        super(context, R.layout.event_list_item, events);
        this.context = context;
        this.events = events;
        this.statuses = statuses;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        }

        Event event = events.get(position);

        TextView title = view.findViewById(R.id.itemTitle);
        TextView deadline = view.findViewById(R.id.itemDeadline);
        RecyclerView tags = view.findViewById(R.id.itemTags);
        // OPTIONAL: USED FOR User events history screen
        TextView itemStatus = view.findViewById(R.id.itemStatus);


        title.setText(event.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        deadline.setText("Registration End Time: " + event.getRegistrationEndTime().format(formatter));

        // OPTIONAL: USED FOR User events history screen
        if (statuses != null && position < statuses.size()) {
            EntrantStatus status = statuses.get(position);
            itemStatus.setVisibility(View.VISIBLE);
            itemStatus.setText("Status: " + status);

            // TODO: Switch case for different status color
            switch (status) {
                case CHOSEN:
                    break;
                case WAITING:
                    break;
                case CANCELLED:
                    break;
                case FINALIZED:
                    break;
            }

        } else {
            // Hide the status TextView if there is no corresponding status
            itemStatus.setVisibility(View.GONE);
        }

        // Setup tags RecyclerView
        tags.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        EventTagsAdapter tagAdapter = new EventTagsAdapter(event.getEventTags()); // your existing adapter
        tags.setAdapter(tagAdapter);

        return view;
    }
}
