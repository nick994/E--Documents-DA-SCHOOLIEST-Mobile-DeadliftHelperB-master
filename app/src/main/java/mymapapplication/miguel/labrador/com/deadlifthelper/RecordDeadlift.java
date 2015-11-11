package mymapapplication.miguel.labrador.com.deadlifthelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.achartengine.GraphicalView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class RecordDeadlift extends AppCompatActivity
                    implements SensorEventListener {

    /*****************************/
    /*****Private Variables******/
    /***************************/

    /****** Receieved from Intent******/
    private String weight; //Holds weight from intent from Main Activity.

    //This is a private database that will be initialized as internal.
    //It stores temporary data regarding a deadlift set (x, y, z acceleration).
    private DeadliftHelperInternalDatabase temp_accel_DB;

    private boolean recordingData = false;  //Used to determine if we are currently capturing data from the sensor.

    private int sampleCount = 0;    //Keeps track of the number of times that onSensorEventChanges has been called.
    private float stretchingVal;

    /****** Chart Stuff ******/
    private ChartMaker x_values_chart;  //Chart to plot x-values versus sample number.
    private ChartMaker y_values_chart;  //Chart to plot y-values versus sample number.
    private ChartMaker z_values_chart;  //Chart to plot z-values versus sample number.

    //Used to keep track of our context when drawing the chart on this activity.
    //We must set context in onResume rather than using 'this' as our context because
    //the chart is changed in an onClick event for a button.
    Context context;

    //These are the GraphicalViews which contain the charts that have our data.
    private GraphicalView x_graph;
    private GraphicalView y_graph;
    private GraphicalView z_graph;

    //These are the layours into which the above GraphicalViews will be added.
    private LinearLayout x_layout;
    private LinearLayout y_layout;
    private LinearLayout z_layout;

    /***Some UI Elements***/
    Button button_save;

    /*****************************/
    /*Helper & Private Functions*/
    /***************************/

    //Function to commit the internal DB's data to the external DB.
    //These DB's have the same fields.
    //FIXME: THIS MAY BE WHERE I AVERAGE THE DATA FOR WHEN I ADD TEH ABILITY TO DISCERN BETWEEN REPS
    private void copyInternalRecordsToExternal()
    {
        //Get all the records from our temp_accel_DB.
        String[][] tmp = temp_accel_DB.getAllRecords();

        int numCols_Internal = tmp.length;

        //mainCollection to mimic the tmp[][] array. This collection is
        //essentially a table, just like TMP. One additional column, however, for date.
        ArrayList<ArrayList<String>> mainCollection = new ArrayList<ArrayList<String>>();

        //Add each column
        for(int x = 0; x < numCols_Internal + 1; x ++)
        {
            mainCollection.add(new ArrayList<String>());
        }

        //tmp stores data exactly as a table does.
        //The columns of tmp are the columns (fields) of our database.
        //The rows of tmp are the rows of our database.
        //Use a for loop to loop through each row.
        //Copy items from tmp to mainCollection.
        for(int c = 0; c < tmp[0].length; c++)
        {
            //Get values from tmp.
            //FIXME: THE COLUMN NUMBER MAY HAVE TO CHANGE HERE for rep no
            mainCollection.get(0).add(tmp[0][c]);       //Weight is at index 0.
            mainCollection.get(1).add(tmp[1][c]);       //x_Accel.
            mainCollection.get(2).add(tmp[2][c]);       //y_Accel.
            mainCollection.get(3).add(tmp[3][c]);       //z_Accel.

            //Date requires some additional stuff.
            Calendar today = Calendar.getInstance();
            String todayString = (today.get(today.YEAR) + "/");

            //Even formatting is difficult in this thing.
            if((today.get(today.MONTH) + 1) < 10)
                todayString += "0" + (today.get(today.MONTH) + 1) + "/";
            else
                todayString += (today.get(today.MONTH) + 1) + "/";

            if(today.get(today.DATE) < 10)
                todayString += "0" + today.get(today.DATE);
            else
                todayString += today.get(today.DATE);


            mainCollection.get(4).add(todayString);
        }

        //external db is MainScreen.full_table_database.
        MainScreen.full_table_database.addMultipleRecords(mainCollection.get(0),  mainCollection.get(1),  mainCollection.get(2),  mainCollection.get(3),  mainCollection.get(4), context);
    }

    //Function to discard all data regarding this activity's current set of deadlifts.
    //Remove data from chart.
    //Clear the chart itself.
    //Remove charts from their respective views.
    //Delete records from temp database.
    private void discardCurrentData()
    {
        //If the charts are not null, then we will remove their data and clear the charts.
        if(!x_values_chart.isNullChart() && !y_values_chart.isNullChart() && !z_values_chart.isNullChart())
        {
            //First, clear existing data from our charts.
            x_values_chart.removeAllData();
            y_values_chart.removeAllData();
            z_values_chart.removeAllData();

            //Then clear the charts.
            x_values_chart.clearChart();
            y_values_chart.clearChart();
            z_values_chart.clearChart();

            //Remove charts from their views.
            x_layout.removeView(x_graph);
            y_layout.removeView(y_graph);
            z_layout.removeView(z_graph);
        }

        //Remove all data from the database.
        temp_accel_DB.deleteAllRecords();
    }

    //Function to create a dialogue and ask the user to confirm that they wish to remove all records from the database.
    private void clearRecordsWithConfirmation()
    {
        new AlertDialog.Builder(this)
                .setTitle("Delete lifts")
                .setMessage("Are you sure you want to permanently delete ALL recorded lifts?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        MainScreen.full_table_database.deleteAllRecords(context);

                        int duration = Toast.LENGTH_SHORT;
                        Toast toastMessage = Toast.makeText(context, "Clearing records...", duration);
                        toastMessage.show();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        int duration = Toast.LENGTH_SHORT;
                        Toast toastMessage = Toast.makeText(context, "Records NOT cleared", duration);
                        toastMessage.show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    //Function to open the "How To" dialogue.
    private void openHowToDialogue()
    {
        LayoutInflater inflater = this.getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to permanently delete ALL recorded lifts?")
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Do nothing.
                    }
                })
                .setView(inflater.inflate(R.layout.how_to_layout, null));

        builder.create().show();
    }


    /*****************************/
    /*****Lifecycle Functions****/
    /***************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Required default stuff.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_deadlift);

        /***********Set up this Activity's Private Internal Database***********/
        temp_accel_DB = new DeadliftHelperInternalDatabase(this);

        /***********Initialize UI Elements***********/
        final Button button_start_recording = (Button) this.findViewById(R.id.StartLiftButton); //Final because it was barking at me when I change the text below.
        Button button_discard = (Button) this.findViewById(R.id.DiscardButton);
        button_save = (Button) this.findViewById(R.id.SaveLiftButton);

        /*************Set up Charts*******************/
        float smoothingValue = (float)0.1;
        stretchingVal = (float)1.5;

        x_values_chart = new ChartMaker("Sideways Accelaration", smoothingValue);
        y_values_chart = new ChartMaker("Vertical Accelaration", smoothingValue);
        z_values_chart = new ChartMaker("Horizontal Accelaration", smoothingValue);

        /***********Enable UI Functionality***********/
        //Start Recording Button activates the sensor and begins recording its data.
        //The data is stored on an internal database that is private in  this activity.
        button_start_recording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!recordingData) {

                    //First, we need to remove all existing data and charts.
                    discardCurrentData();

                    //Change the text of the Start button to reflect that it is now a stop button.
                    button_start_recording.setText("Stop Recording Lift");

                    //This allows onSensorChanged to collect data.
                    recordingData = true;

                    button_save.setEnabled(false);

                } else {
                    //Stop recording data.
                    recordingData = false;

                    //Change text of button to reflect its new function.
                    button_start_recording.setText("Start Recording Lift");

                    /****Display data to screen****/
                    //Get layouts.
                    x_layout = (LinearLayout) findViewById(R.id.XValuesChart);
                    y_layout = (LinearLayout) findViewById(R.id.YValuesChart);
                    z_layout = (LinearLayout) findViewById(R.id.ZValuesChart);

                    //Get values for our views.
                    x_graph = x_values_chart.getChartView(context);
                    y_graph = y_values_chart.getChartView(context);
                    z_graph = z_values_chart.getChartView(context);

                    //Display data in these layouts.
                    x_layout.addView(x_graph);
                    y_layout.addView(y_graph);
                    z_layout.addView(z_graph);

                    button_save.setEnabled(true);
                }
            }
        });

        //Discard button clears the data from this activity's private internal database.
        //Remove stuff from charts, and make screen blank again.
        button_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Remove all existing data and charts.
                discardCurrentData();
                button_save.setEnabled(false);
            }
        });

        //Save button commits the data from this activity's private internal temp_accel_DB
        //to the application's public external database.
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Tell the user that we are saving the data.
                int duration = Toast.LENGTH_SHORT;
                Toast toastMessage = Toast.makeText(context, "Saving lift...", duration);
                toastMessage.show();

                copyInternalRecordsToExternal();    //Save the data. This function shows a toast when the data is finished saving as well.
                button_save.setEnabled(false);
            }
        });
    }

    //Function to collect data from sensor and commit it to the private internal database.
    public void onSensorChanged(SensorEvent mySensorEvent) {
        //Accel values.
        float x_accel;
        float y_accel;
        float z_accel;


        if (recordingData) {
            //Get accel values.
            x_accel = mySensorEvent.values[0];
            y_accel = mySensorEvent.values[1];
            z_accel = mySensorEvent.values[2];

            //Commit accel values to database.
            temp_accel_DB.addRecord(Integer.parseInt(weight), x_accel, y_accel, z_accel);

            //Add accel values to chart's dataset.
            sampleCount++;
            x_values_chart.addSinglePoint(x_accel, sampleCount * stretchingVal);
            y_values_chart.addSinglePoint(y_accel, sampleCount * stretchingVal);
            z_values_chart.addSinglePoint(z_accel, sampleCount * stretchingVal);
        }
    }



    //This function has three purposes:
    //Register the accelarometer with our sensorManager (must be done each time the activity loads).
    //We set the context, which will be used when creating a chart in this view.
    //We must set context here, rather than using 'this' as our context because
    //the chart is changed in an onClick event for a button.
    //Finally, we receive intents here.
    @Override
    protected void onResume()
    {
        super.onResume();

        /***********Receive Intent***********/
        //Get the intent from Main Screen that brought us here.
        Intent intent_mainActivity = getIntent();

        //Get strings passed from intent_mainAcivity.
        //Parameters are key values associated with these strings from the MainAcivity.
        weight = intent_mainActivity.getStringExtra(MainScreen.WEIGHT);

        /***********Set up this Activity's Accelerometer***********/
        SensorManager mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensorManager.registerListener(this, mySensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        //Get our context.
        context = this;

        //Ensure that our sample number is not messed up when moving between activities.
        sampleCount = 0;

        //User should not be able to save a lift if they haven't recorded any data!
        button_save.setEnabled(false);
    }


    //Here, we impose the 'temporary-ness' of the temp_accel_DB.
    //table can exist in memory, but we must remove all data from it.
    @Override
    protected void onStop()
    {
        //Always call super method first.
        super.onStop();

        //Remove all data from temp_accel_DB.
        temp_accel_DB.deleteAllRecords();
    }

    //Required when implementing SensorEventListener.
    public void onAccuracyChanged(Sensor sensor, int myInt) {}

    //Set up the options menu.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
        return true;
    }

    //Set actions for each item in options menu.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //When the user clicks "clear records".
        if (id == R.id.action_clear_records) {
            clearRecordsWithConfirmation();
        }
        if (id == R.id.action_how_to) {
            openHowToDialogue();
        }
        return super.onOptionsItemSelected(item);
    }
}
