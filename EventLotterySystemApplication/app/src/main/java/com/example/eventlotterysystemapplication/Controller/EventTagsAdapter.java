package com.example.eventlotterysystemapplication.Controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventlotterysystemapplication.R;
import com.example.eventlotterysystemapplication.View.EventDetailScreenFragment;

import java.util.List;

/**
 * EventTagsAdapter is used by {@link EventDetailScreenFragment} to fetch strings to display in
 * the {@link HorizontalRVActivity}
 */

public class EventTagsAdapter extends RecyclerView.Adapter<EventTagsAdapter.TagViewHolder> {

    private final List<String> tags;

    /**
     * Constructor
     * @param tags The list of tags we want to display
     */
    public EventTagsAdapter(List<String> tags) {
        this.tags = tags;
    }

    // Called for each tag
    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_item, parent, false);
        return new TagViewHolder(view);
    }

    // Called by Recycler view to display data
    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        holder.tagText.setText(tags.get(position));
    }

    // Tells Recycler View how many tags there are
    @Override
    public int getItemCount() {
        return tags.size();
    }

    /**
     * For holder reusability, instead of inflating each time, each TagViewHolder holds references
     * to the views inside one Tag
     */
    static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagText;
        TagViewHolder(View itemView) {
            super(itemView);
            tagText = itemView.findViewById(R.id.tvTag);
        }
    }
}
