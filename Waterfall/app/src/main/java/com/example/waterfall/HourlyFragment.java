package com.example.waterfall;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;

public class HourlyFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.hourly_fragment,container,false);

        GraphView hourlyGraph = (GraphView) view.findViewById(R.id.hourly_graph);
        hourlyGraph.setTitle("Hourly");

        return view;
    }

}