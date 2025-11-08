package com.example.eventlotterysystemapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * HorizontalRVActivity is used by {@link EventDetailScreenFragment} to get tags to
 * display in a horizontal list view
  */

public class HorizontalRVActivity extends AppCompatActivity {
    RecyclerView rv;
    ArrayList<String> dataSource;
    LinearLayoutManager linearLayoutManager; // RVs need this
    MyRvAdapter myRvAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_event_detail_screen);
        rv = findViewById(R.id.tagsHorizontalRv);

        // set the data source
        dataSource = new ArrayList<>();
        // hardcoded values below
        dataSource.add("Tag 1");
        dataSource.add("Tag 2");
        dataSource.add("Tag 3");
        dataSource.add("Tag 4");
        dataSource.add("Tag 5");
        dataSource.add("Tag 6");
        dataSource.add("Tag 7");
        dataSource.add("Tag 8");
        dataSource.add("Tag 9");

        linearLayoutManager = new LinearLayoutManager(HorizontalRVActivity.this, LinearLayoutManager.HORIZONTAL, false);
        myRvAdapter = new MyRvAdapter(dataSource);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(myRvAdapter);
    }

    class MyRvAdapter extends  RecyclerView.Adapter<MyRvAdapter.MyHolder> {
        ArrayList<String> data;

        public MyRvAdapter(ArrayList<String> data) {
            this.data = data;

        }

        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(HorizontalRVActivity.this).inflate(R.layout.rv_item, parent, false);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder holder, int position) {
            holder.tvTag.setText(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyHolder extends RecyclerView.ViewHolder{
            TextView tvTag;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                tvTag = itemView.findViewById(R.id.tvTag);

            }
        }
    }
}
