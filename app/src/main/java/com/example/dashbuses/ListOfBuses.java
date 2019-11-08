package com.example.dashbuses;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ListOfBuses extends AppCompatActivity {
    private RecyclerView mBusTimingList;
    private DatabaseReference mBusDatabase;
    private String mBusStopDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_buses);
        mBusTimingList = (RecyclerView) findViewById(R.id.bus_timings);
        mBusStopDetail = getIntent().getStringExtra("bus_stop_detail");
        Log.d("Bus Stop Selected", mBusStopDetail);

        mBusDatabase = FirebaseDatabase.getInstance().getReference().child(mBusStopDetail).child("timings");
        mBusTimingList.setHasFixedSize(true);
        LinearLayoutManager linearVertical = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mBusTimingList.setLayoutManager(linearVertical);


    }
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Buses, ListOfBuses.BusTimingViewHolder> friendsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Buses, BusTimingViewHolder>(
                Buses.class,
                R.layout.time_of_bus_single_layout,
                ListOfBuses.BusTimingViewHolder.class,
                mBusDatabase) {
            @Override
            protected void populateViewHolder(final BusTimingViewHolder busTimingViewHolder, Buses buses, int i) {
                busTimingViewHolder.setDate(buses.getDate());

                final String list_buses_id = getRef(i).getKey();

                mBusDatabase.child(list_buses_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String busesTiming = dataSnapshot.child("time").getValue().toString();
                        Log.d("Bus Time", busesTiming);

                        busTimingViewHolder.setBusTime(busesTiming);

                        busTimingViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(ListOfBuses.this, com.example.dashbuses.BusRoute.class);
                                intent.putExtra("bus_timing_id", list_buses_id);
                                intent.putExtra("bus_stop_selected", mBusStopDetail);
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        mBusTimingList.setAdapter(friendsRecyclerViewAdapter);
    }

    public static class BusTimingViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public BusTimingViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setBusTime(String bustime) {
            TextView userNameView = (TextView) mView.findViewById(R.id.bus_time);
            userNameView.setText(bustime);
        }

        public  void setDate (String date) {

        }
    }
}
