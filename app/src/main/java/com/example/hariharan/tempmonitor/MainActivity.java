package com.example.hariharan.tempmonitor;

import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final String PARSE_APP_ID = "LsrJX7KIwS54yaVaPxQk9KgkLWmlfug0ZAdIQIN6";
    final String PARSE_CLIENT_KEY = "xgavPnf4nCCOakOSHF6xh5OjVIkO0Bqd0On177SB";
    FloatingActionButton fab;
    GraphView graph;
    View view;
    DataPoint tDataPoints[];
    DataPoint hDataPoints[];
    LineGraphSeries<DataPoint> tempSeries, humidSeries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        view = findViewById(R.id.coordinator_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(Color.DKGRAY));
        fab.setOnClickListener(this);
        initParse();
        initGraphView();


    }

    protected void initParse() {
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, PARSE_APP_ID, PARSE_CLIENT_KEY);
    }

    protected void initGraphView() {
        graph = (GraphView) findViewById(R.id.graph);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(10);
        graph.getViewport().setMaxY(70);
        tempSeries = new LineGraphSeries<DataPoint>();
        graph.addSeries(tempSeries);
        humidSeries = new LineGraphSeries<DataPoint>();
        graph.addSeries(humidSeries);
        tempSeries.setColor(Color.RED);
        humidSeries.setColor(Color.CYAN);
        tempSeries.setTitle("Temp °C");
        humidSeries.setTitle("Humidity %");
        graph.setTitle("Temperature and Humidity");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.fab) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("RpiData");
            query.orderByDescending("createdAt");
            query.setLimit(10);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        Log.d("Parse", "Retrieved " + list.size() + " Objects");
                        setDataToGraph(list);
                    } else {
                        Log.d("Parse", "Error: " + e.getMessage());
                    }
                }
            });

            Snackbar.make(view, "Getting Data", Snackbar.LENGTH_SHORT).setAction("Hide", this).show();

        }
    }

    public void setDataToGraph(List<ParseObject> list) {
        tDataPoints = new DataPoint[10];
        hDataPoints = new DataPoint[10];
        DataPoint buf1, buf2;
        graph.setTitle("Updated at " + list.get(0).getCreatedAt());
        for (int i = 0; i < 10; i++) {
            buf1 = new DataPoint(i, list.get(9 - i).getInt("temp"));
            buf2 = new DataPoint(i, list.get(9 - i).getInt("humid"));
            tDataPoints[i] = buf1;
            hDataPoints[i] = buf2;

        }
        tempSeries.resetData(tDataPoints);
        humidSeries.resetData(hDataPoints);
        Snackbar.make(view, "Latest: Temp:" + list.get(0).getInt("temp") + "°C  Humidity:" + list.get(0).getInt("humid") + "%", Snackbar.LENGTH_LONG).setAction("Hide", this).show();
    }
}
