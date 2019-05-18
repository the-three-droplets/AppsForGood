// GraphView Class used from from jjoe64

package com.example.waterfall;

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

public class WeeklyFragment extends Fragment {

    // Data
    private ArrayList<Long> xVal = new ArrayList<Long>();
    private ArrayList<Double> yVal = new ArrayList<Double>();

    private double x,y;
    private LineGraphSeries<DataPoint> series;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Weight");
    private ValueEventListener listener;

    // Overrides the OnCreateView method
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view3 = inflater.inflate(R.layout.weekly_fragment,container,false);

        // Adds a listener for changes in Firebase and Overrides the onDataChange method
        listener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){

                    // Pulls data from Firebase
                    Log.d("MainActivityValue",child.getValue().toString());
                    Log.d("MainActivityValue",child.getKey());

                    GraphView weeklyGraph = (GraphView) view3.findViewById(R.id.weekly_graph);
                    weeklyGraph.setTitle("Weekly");

                    xVal.add(Long.parseLong(child.getKey()));
                    yVal.add(Double.parseDouble(child.getValue().toString()));

                    // Graphs data
                    y = 0;
                    series = new LineGraphSeries<DataPoint>();
                    series.appendData(new DataPoint(0, 0), true, xVal.size() + 1);
                    for(int i = 0; i < xVal.size(); i++) {
                        if(xVal.get(i)/(3600.0 * 24.0 * 7.0) >= xVal.get(xVal.size() - 1)/(3600.0 * 24.0 * 7.0) - 5) {
                            x = xVal.get(i) / (3600.0 * 24.0 * 7.0) - xVal.get(xVal.size() - 1)/(3600.0 * 24.0 * 7.0) + 5;
                            y += yVal.get(i);
                            series.appendData(new DataPoint(x, y), true, xVal.size() + 1);
                        }
                    }

                    // Sets bounds on data
                    weeklyGraph.getViewport().setXAxisBoundsManual(true);
                    weeklyGraph.getViewport().setMinX(0);
                    weeklyGraph.getViewport().setMaxX(5.2);

                    weeklyGraph.removeAllSeries();
                    weeklyGraph.addSeries(series);
                }
            }

            // Overrides the onCancelled method
            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("MainActivity", "Failed to read value.", error.toException());
            }
        });

        Log.d("MainActivityValue",xVal.toString());
        Log.d("MainActivityValue",yVal.toString());

        return view3;
    }

    // Overrides the onPause method
    @Override
    public void onPause() {
        super.onPause();
        myRef.removeEventListener(listener);
        getActivity().finish();
    }

}
