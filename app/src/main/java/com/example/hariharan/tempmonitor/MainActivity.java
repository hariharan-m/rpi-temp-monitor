package com.example.hariharan.tempmonitor;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public String curTemp;
    FloatingActionButton fab;
    GraphView graph;
    View view;
    DataPoint tDataPoints[];
    DataPoint hDataPoints[];
    LineGraphSeries<DataPoint> tempSeries,humidSeries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        view = findViewById(R.id.coordinator_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "LsrJX7KIwS54yaVaPxQk9KgkLWmlfug0ZAdIQIN6", "xgavPnf4nCCOakOSHF6xh5OjVIkO0Bqd0On177SB");

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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.fab){
            ParseQuery<ParseObject> query = ParseQuery.getQuery("RpiData");
            query.orderByDescending("createdAt");
            query.setLimit(10);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> list, ParseException e) {
                    if (e == null) {
                        Log.d("score", "Retrieved " + list.size() + " scores");
                        setDataToGraph(list);
                    } else {
                        Log.d("score", "Error: " + e.getMessage());
                    }
                }
            });

            Snackbar.make(view, "Getting Data", Snackbar.LENGTH_SHORT).setAction("Hide", this).show();

        }
    }

    public void setDataToGraph(List<ParseObject> list)
    {
        tDataPoints = new DataPoint[10];
        hDataPoints= new DataPoint[10];
        DataPoint buf1,buf2;
        graph.setTitle("Updated at "+list.get(0).getCreatedAt());
        for(int i=0;i<10;i++)
        {
            buf1=new DataPoint(i,list.get(9-i).getInt("temp"));
            buf2=new DataPoint(i,list.get(9-i).getInt("humid"));
            tDataPoints[i]=buf1;
            hDataPoints[i]=buf2;

        }
        tempSeries.resetData(tDataPoints);
        humidSeries.resetData(hDataPoints);
        Snackbar.make(view, "Latest: Temp:"+list.get(0).getInt("temp")+"°C  Humidity:"+list.get(0).getInt("humid")+"%", Snackbar.LENGTH_LONG).setAction("Hide", this).show();
    }
}
