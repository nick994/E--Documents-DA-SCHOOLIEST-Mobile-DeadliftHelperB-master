package mymapapplication.miguel.labrador.com.deadlifthelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class ViewLifts extends AppCompatActivity {

    /*****************************/
    /*****Private Variables******/
    /***************************/

    //Contains all of the records which we will show.
    private float[][] allRecords;

    //Used so we know if it's the first time the user clicked the 'View Lifts' button.
    boolean firstClick = true;

    private String[] selection;   //Corresponds to selection for SQLitedatabse.query. Recieved from SetFilters.
    private String[] relations;   //Corresponds to <, = or > when filtering daa.
    private String[] selectionArgs; //Corresponds to selectionArgs for SQLitedatabse.query. Recieved from SetFilters.

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

    //These are the layouts into which the above GraphicalViews will be added.
    private LinearLayout x_layout;
    private LinearLayout y_layout;
    private LinearLayout z_layout;


    /*****************************/
    /*Helper & Private Functions*/
    /***************************/

    //Helper function to start the Set Filters activity.
    private void navigateTo_setFilters(View view)
    {
        //Intent used to navigate to Set Filters activity.
        Intent intent_setFilters = new Intent(this, SetFilters.class);

        //Finally, put the extras regarding the filters to put on our data in ViewLifts.
        intent_setFilters.putExtra(MainScreen.SELECTION, selection);
        intent_setFilters.putExtra(MainScreen.RELATIONS, relations);
        intent_setFilters.putExtra(MainScreen.SELECTIONARGS, selectionArgs);

        //Start the Set Filters activity.
        startActivity(intent_setFilters);
    }

    //Add the acceleration data from the full_table to the respective chart.
    //Display these charts to the screen.
    private boolean displayCharts()
    {
        if(Math.abs(allRecords[0][0] - 0.0101001) < 0.0000001)
        {
            int duration = Toast.LENGTH_SHORT;
            Toast toastMessage = Toast.makeText(this, "No lifts for this user with given filters", duration);
            toastMessage.show();
            return false;
        }

        //Number of rows in our table.
        int numRows = allRecords[0].length;

        //Add each record to the charts.
        for(int c = 0; c < numRows; c++)
        {
            //Exit the loop when we are at the end of the array's rows (flag value)
            //The array has 10000 rows, but not all are populated.
            //Because parse.
            if(Math.abs(allRecords[0][c] - 0.0101001) < 0.0000001)
            {
                break;
            }

            x_values_chart.addSinglePoint(allRecords[0][c], c);   //Column 2 corresponds to x_accel.
            y_values_chart.addSinglePoint(allRecords[1][c], c);   //Column 3 corresponds to y_accel.
            z_values_chart.addSinglePoint(allRecords[1][c], c);   //Column 4 corresponds to z_accel.
        }

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

        return true;
    }

    //Function to remove all data currently on the screen and in our charts.
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

                        //Clear charts
                        discardCurrentData();
                        //Make allRecords null.
                        allRecords[0][0] = (float)0.0101001;


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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_lifts);

        /*************Set up Charts*******************/
        x_values_chart = new ChartMaker("Sideways Accelaration");
        y_values_chart = new ChartMaker("Vertical Accelaration");
        z_values_chart = new ChartMaker("Horizontal Accelaration");

        /*************Initialize Views*******************/
        x_layout = (LinearLayout) this.findViewById(R.id.XValuesChart);
        y_layout = (LinearLayout) this.findViewById(R.id.YValuesChart);
        z_layout = (LinearLayout) this.findViewById(R.id.ZValuesChart);

        /***********Add Navigation Buttons***********/
        //Button to navigate to Record Deadlift screen.
        Button setFilters = (Button) this.findViewById(R.id.SetFiltersButton);
        setFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo_setFilters(view);
            }
        });

        //Button to show all lifts in the current database.
        final Button viewLifts = (Button) this.findViewById(R.id.ViewLiftsButton);    //Final because it was barking at me when I change the text below.
        viewLifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //If it's the first time the user clicked this button after starting the activity,
                //Then we want to change the text.
                if(firstClick) {
                    boolean recordsAvailable = displayCharts();

                    if(recordsAvailable) {
                        firstClick = false;
                        viewLifts.setText("Clear Charts");
                    }
                }
                //Otherwise, we must clear existing data and display the charts.
                else
                {
                    discardCurrentData();
                    firstClick = true;
                    viewLifts.setText("View My Lifts");
                }
            }
        });
    }


    //Receive intents here.
    @Override
    protected void onResume()
    {
        super.onResume();

        /***********Receive Intent***********/
        //Get the intent from Main Screen that brought us here.
        Intent intent = getIntent();

        //Get strings passed from intent_mainAcivity.
        //Parameters are key values associated with these strings from the MainAcivity.
        selection = intent.getStringArrayExtra(MainScreen.SELECTION);
        relations = intent.getStringArrayExtra(MainScreen.RELATIONS);
        selectionArgs = intent.getStringArrayExtra(MainScreen.SELECTIONARGS);

        //Get the cursor that we will use to show our data. This may change each time we receive intents, so get it when we get our intents.
        allRecords = MainScreen.full_table_database.getAllRecords(selection, relations, selectionArgs);

        //Get our context.
        context = this;
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }


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
