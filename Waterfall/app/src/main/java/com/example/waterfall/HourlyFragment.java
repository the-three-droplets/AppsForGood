package com.example.waterfall;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


import java.util.ArrayList;

public class HourlyFragment extends Fragment {

    private ArrayList<Long> xVal = new ArrayList<Long>();
    private ArrayList<Double> yVal = new ArrayList<Double>();

    private double x,y;
    private LineGraphSeries<DataPoint> series;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Weight");
    private ValueEventListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view1 = inflater.inflate(R.layout.hourly_fragment,container,false);


        listener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    Log.d("MainActivityValue",child.getValue().toString());
                    Log.d("MainActivityValue",child.getKey());

                    GraphView hourlyGraph = (GraphView) view1.findViewById(R.id.hourly_graph);
                    hourlyGraph.setTitle("Hourly");

                    xVal.add(Long.parseLong(child.getKey()));
                    yVal.add(Double.parseDouble(child.getValue().toString()));
                    y = 0;
                    series = new LineGraphSeries<DataPoint>();
                    series.appendData(new DataPoint(0, 0), true, xVal.size() + 1);
                    for(int i = 0; i < xVal.size(); i++) {
                        if(xVal.get(i)/3600.0 >= xVal.get(xVal.size() - 1)/3600.0 - 24) {
                            x = xVal.get(i) / 3600.0 - xVal.get(xVal.size() - 1)/3600.0 + 24;
                            y += yVal.get(i);
                            try {
                                series.appendData(new DataPoint(x, y), true, xVal.size() + 1);
                            } catch(Exception e){
//                                Intent openHome = new Intent(getActivity(), MainActivity.class);
//                                startActivity(openHome);
                                getActivity().finish();
                            }
                        }
                    }

                    hourlyGraph.getViewport().setXAxisBoundsManual(true);


                    hourlyGraph.getViewport().setMinX(0);
                    hourlyGraph.getViewport().setMaxX(25);

//                    GridLabelRenderer xAxis = hourlyGraph.getGridLabelRenderer();
//                    xAxis.setHorizontalAxisTitle("Hours");
//                    GridLabelRenderer yAxis = hourlyGraph.getGridLabelRenderer();
//                    yAxis.setVerticalAxisTitle("Ounces Drank");
                    hourlyGraph.removeAllSeries();
                    hourlyGraph.addSeries(series);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("MainActivity", "Failed to read value.", error.toException());
            }
        });




        Log.d("MainActivityValue",xVal.toString());
        Log.d("MainActivityValue",yVal.toString());




        return view1;
    }

    @Override
    public void onPause() {
        super.onPause();
        myRef.removeEventListener(listener);
    }

}
