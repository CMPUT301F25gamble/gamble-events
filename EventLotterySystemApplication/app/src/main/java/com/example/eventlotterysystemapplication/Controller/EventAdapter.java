package com.example.eventlotterysystemapplication.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Switch;
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

/**
 * This class is the ArrayAdapter that is used to display the lists of events in a well organized
 * and visually appealing format
 */
public class EventAdapter extends ArrayAdapter<Event> {

    private final Context context;
    private final List<Event> events;

    // OPTIONAL: used for User events history screen
    private List<EntrantStatus> statuses = null;

    /**
     * The constructor for the EventAdapter
     * @param context A context object representing the current state of the system
     * @param events The List containing all of the events that we want to display
     */
    public EventAdapter(Context context, List<Event> events) {
        super(context, R.layout.event_list_item, events);
        this.context = context;
        this.events = events;
    }

    /**
     * This constructor is used ONLY FOR user events history screen
     * @param context A context object representing the current state of the system
     * @param events The List containing all of the events that we want to display
     * @param statuses The list containing all of the user statuses of the entrants
     */
    public EventAdapter(Context context, List<Event> events, List<EntrantStatus> statuses) {
        super(context, R.layout.event_list_item, events);
        this.context = context;
        this.events = events;
        this.statuses = statuses;
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.event_list_item, parent, false);
        }
        final View rowView = view; // used for getting the event when user clicks on one of its tags

        Event event = events.get(position);

        TextView title = view.findViewById(R.id.itemTitle);
        TextView deadline = view.findViewById(R.id.itemDeadline);
        RecyclerView tags = view.findViewById(R.id.itemTags);
        // OPTIONAL: USED FOR User events history screen
        TextView itemStatus = view.findViewById(R.id.itemStatus);


        title.setText(event.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        deadline.setText("Registration End Time: " + event.getRegistrationEndTime().format(formatter));

        tags.setOnTouchListener(new View.OnTouchListener() {
            boolean isScrolling = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isScrolling = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        isScrolling = true;
                        break;

                    case MotionEvent.ACTION_UP:
                        if (!isScrolling) {
                            Log.d("Tags", "item clicked");
                            ((AdapterView<?>) parent).performItemClick(
                                    rowView,
                                    position,
                                    getItemId(position)
                            );
                        }
                        break;
                }
                return false;
            }
        });

        // OPTIONAL: USED FOR User events history screen
        if (statuses != null && position < statuses.size()) {
            EntrantStatus status = statuses.get(position);
            itemStatus.setVisibility(View.VISIBLE);

            // Full String to display
            String label = "Status: ";
            String statusText = status.name();
            String fullText = label + statusText;

            SpannableString span = new SpannableString(fullText);

            // Find start & end of the status
            int start = fullText.indexOf(statusText);
            int end = start + statusText.length();

            // Apply bold to "Status: "
            span.setSpan(new StyleSpan(Typeface.BOLD),
                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Apply color to "[STATUS]"
            span.setSpan(new ForegroundColorSpan(getStatusColor(status)),
                    start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Set final styles
            itemStatus.setText(span);

        } else {
            // Hide the status TextView if there is no corresponding status
            itemStatus.setVisibility(View.GONE);
        }

        // Setup tags RecyclerView + fix no tags bug
        List<String> eventTags = event.getEventTags();

        if (eventTags == null || eventTags.isEmpty()) {
            // Hide RecyclerView completely when no tags
            tags.setVisibility(View.GONE);
        } else {
            // Show and populate tags
            tags.setVisibility(View.VISIBLE);
            tags.setLayoutManager(new LinearLayoutManager(
                    context,
                    LinearLayoutManager.HORIZONTAL,
                    false));
            EventTagsAdapter tagAdapter = new EventTagsAdapter(eventTags);
            tags.setAdapter(tagAdapter);
        }

        return view;
    }

    /**
     * Helper function s.t.given an EntrantStatus, return the corresponding color associated with it
     * @param status Either WAITING, CHOSEN, CANCELLED, or FINALIZED
     * @return default color (black)
     */
    private int getStatusColor(EntrantStatus status) {
        switch (status) {
            case CHOSEN:
                return Color.parseColor("#4CAF50"); // Green
            case WAITING:
                return Color.parseColor("#FF9800"); // Orange/Yellow
            case CANCELLED:
                return Color.parseColor("#F44336"); // Red
            case FINALIZED:
                return Color.parseColor("#4A4A4A"); // Dark Grey
            default:
                return Color.parseColor("#000000"); // Black
        }
    }
}
