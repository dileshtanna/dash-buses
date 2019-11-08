package com.example.dashbuses;

import android.content.Intent;
import android.net.Uri;
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

public class BusRoute extends AppCompatActivity {
    private RecyclerView mBusStopList;
    private DatabaseReference mBusStopDatabase;
    private String mBusTimingId;
    private  String mBusStopSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_route);

        mBusStopList = (RecyclerView) findViewById(R.id.bus_stop_list);

        mBusTimingId = getIntent().getStringExtra("bus_timing_id");
        mBusStopSelected = getIntent().getStringExtra("bus_stop_selected");
        Log.d("Bus Timing extra", mBusTimingId);
        Log.d("Bus selected extra", mBusStopSelected);

        mBusStopDatabase = FirebaseDatabase.getInstance().getReference().child(mBusStopSelected).child("timings").child(mBusTimingId).child("route");
        mBusStopList.setHasFixedSize(true);
        LinearLayoutManager linearVertical = new LinearLayoutManager(BusRoute.this, RecyclerView.VERTICAL, false);
        mBusStopList.setLayoutManager(linearVertical);
    }

    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Routes, BusRoute.BusStopViewHolder> stopsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Routes, BusStopViewHolder>(
                Routes.class,
                R.layout.bus_route_single_layout,
                BusStopViewHolder.class,
                mBusStopDatabase) {
            @Override
            protected void populateViewHolder(final BusStopViewHolder busStopViewHolder, Routes routes, int i) {
                busStopViewHolder.setDate(routes.getDate());

                final String list_stops_id = getRef(i).getKey();

                mBusStopDatabase.child(list_stops_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String busStopName = dataSnapshot.child("stop_name").getValue().toString();
                        final String mapSearchValue = dataSnapshot.child("map_search").getValue().toString();
                        Log.d("Stop Name", busStopName);

                        busStopViewHolder.setBusStopName(busStopName);
                        busStopViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q="+mapSearchValue));
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

        mBusStopList.setAdapter(stopsRecyclerViewAdapter);
    }



    public static class BusStopViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public BusStopViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setBusStopName(String busstopname) {
            TextView userNameView = (TextView) mView.findViewById(R.id.bus_stop_name);
            userNameView.setText(busstopname);
        }

        public  void setDate (String date) {

        }
    }
}
