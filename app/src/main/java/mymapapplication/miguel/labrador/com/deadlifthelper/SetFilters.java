package mymapapplication.miguel.labrador.com.deadlifthelper;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

public class SetFilters extends AppCompatActivity {

    /*****************************/
    /*****Private Variables******/
    /***************************/

    /*****UI Elements******/
    private EditText editText_weight;
    private EditText editText_date;
    private EditText editText_date_lower_bound;
    private EditText editText_date_upper_bound;
    private Spinner spinner_weight;
    private Spinner spinner_date;
    private Button button_go;

    private String[] selection;   //Corresponds to selection for SQLitedatabse.query. Recieved from SetFilters.
    private String[] relations;   //Corresponds to <, = or > when filtering daa.
    private String[] selectionArgs; //Corresponds to selectionArgs for SQLitedatabse.query. Recieved from SetFilters.

    //Used for date picker dialogues when selecting dates.
    private int day;
    private int month;
    private int year;

    //Used to hold the characters in the spinners of the UI.
    private String[] arraySpinner = {"<=", "=", ">="};

    Context context;

    /*****************************/
    /*Helper & Private Functions*/
    /***************************/

    //Package all filter information into an intent and
    //resume the ViewLifts activity. This is accomplished via the
    //applyFilters function herein.
    private void applyFilters()
    {
        String[] selections = new String[2];
        String[] relations = new String[2];
        String[] selectionArgs = new String[2];

        //Intent used to navigate to View Lifts activity.
        Intent intent_viewLifts = new Intent(this, ViewLifts.class);

        /****Get Weight Filters***/
        selections[0] = "weight";

        //Base case: all date filters are null.
        //Here, we re-apply base filters.
        if(editText_weight.getText().toString().equals(""))
        {
            relations[0] = ">=";
            selectionArgs[0] = "0";
        }
        //Otherwise, get actual data.
        else
        {
            relations[0] = spinner_weight.getSelectedItem().toString();
            selectionArgs[0] = editText_weight.getText().toString();
        }

        /****Get Date Filters***/
        selections[1] = "date";

        //Base filters if no filters given to us.
        if(editText_date.getText().toString().equals("YYYY/MM/DD") && (editText_date_lower_bound.getText().toString().equals("YYYY/MM/DD") || editText_date_lower_bound.getText().toString().equals("YYYY/MM/DD")))
        {
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

            relations[1] = "<=";
            selectionArgs[1] = todayString;
        }

        else if(editText_date.getText().toString().equals("YYYY/MM/DD"))
        {
            relations[1] = "between";
            selectionArgs[1] = editText_date_lower_bound + ":" + editText_date_upper_bound;
        }

        else
        {
            relations[1] = spinner_date.getSelectedItem().toString();
            selectionArgs[1] = editText_date.getText().toString();
        }

        //Finally, put the extras regarding the filters to put on our data in ViewLifts.
        intent_viewLifts.putExtra(MainScreen.SELECTION, selections);
        intent_viewLifts.putExtra(MainScreen.RELATIONS, relations);
        intent_viewLifts.putExtra(MainScreen.SELECTIONARGS, selectionArgs);

        //Finally, start the View Lifts activity.
        startActivity(intent_viewLifts);
    }


    //Function to apply defauly filters.
    private void applyDefaultFilters()
    {
        //Default filters:
        //Weight >= 0.
        editText_weight.setText("0");
        spinner_weight.setSelection(2);

        //Date <= today's date.
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


        editText_date.setText(todayString);
        spinner_date.setSelection(0);

        //Remove date range stuff.
        editText_date_lower_bound.setText("YYYY/MM/DD");
        editText_date_upper_bound.setText("YYYY/MM/DD");
    }

    //Returns whether or not the string, s, is numeric.
    private boolean isNumeric(String s)
    {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }


    //Function to check that weight input is correct:
    private boolean checkInput_weight()
    {
        //Weight should not have leading 0's. Trim them.
        String weight_text = editText_weight.getText().toString();
        String weight_text_trimmed = weight_text.replaceFirst("^0+(?!$)", "");
        editText_weight.setText(weight_text_trimmed);

        //Weight must be alphanumeric.
        return isNumeric(editText_weight.getText().toString());
    }

    //Function to make sure that date is in the correct format.
    private boolean checkInput_date()
    {
        //Specific format: 0000/00/00.
        //In the case that we will send the 'inclusive' dates.
        if(editText_date.getText().toString().equals("YYYY/MM/DD"))
        {
            //If any condition is violated, return false.
            if(!isNumeric(editText_date_lower_bound.getText().toString().substring(0,4)) ||
                    !editText_date_lower_bound.getText().toString().substring(4,5).equals("/") ||
                    !isNumeric(editText_date_lower_bound.getText().toString().substring(5,7)) ||
                    !editText_date_lower_bound.getText().toString().substring(7,8).equals("/") ||
                    !isNumeric(editText_date_lower_bound.getText().toString().substring(8,10)) ||

                !isNumeric(editText_date_upper_bound.getText().toString().substring(0,4)) ||
                        !editText_date_upper_bound.getText().toString().substring(4,5).equals("/") ||
                        !isNumeric(editText_date_upper_bound.getText().toString().substring(5,7)) ||
                        !editText_date_upper_bound.getText().toString().substring(7,8).equals("/") ||
                    !isNumeric(editText_date_upper_bound.getText().toString().substring(8,10))) {

            return false;
            }
            else
                return true;
        }

        //In the case that we will send the single date filter.
        else
        {
            if(!isNumeric(editText_date.getText().toString().substring(0,4)) ||
                    !editText_date.getText().toString().substring(4,5).equals("/") ||
                    !isNumeric(editText_date.getText().toString().substring(5,7)) ||
                    !editText_date.getText().toString().substring(7,8).equals("/") ||
                    !isNumeric(editText_date.getText().toString().substring(8,10))) {

                return false;
            }

            else
                return true;
        }
    }


    //Function to show toast for incorrect input.
    private void incorrectInputDialogue(boolean date, boolean weight)
    {
        String message = "Error in following fields:";

        if(date)
        {
            message += " date";
        }
        if(weight && date)
        {
            message += ", weight";
        }
        else if (weight)
        {
            message += " weight";
        }

        message += ".";

        int duration = Toast.LENGTH_LONG;
        Toast toastMessage = Toast.makeText(this, message, duration);
        toastMessage.show();
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


    //Function to show a date picket dialogue, and use it to manipulate the values of the given EditText.
    private void showDatePicker(int year, int month, int day, final EditText currentEditText)
    {
        //Show a date picker dialogue so that the user can select a date.
        DatePickerDialog datePicker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        currentEditText.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                    }
                }, year, month,  day);
        datePicker.show();

    }


    /*****************************/
    /*****Lifecycle Functions****/
    /***************************/

    //In onCreate, we initialize any UI element that is not initialized in onResume()
    //Also, add items to the spinners.
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Default and required.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_filters);

        /***********Initialize UI Elements***********/
        /**Put Items into Spinners**/
        //Get weight and date spinners.
        spinner_weight = (Spinner) findViewById(R.id.WeightSpinner);
        spinner_date = (Spinner) findViewById(R.id.DateSpinner);


        //Use an adapter to add elements to the spinners.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);

        //Actually add the elements to the spinners.
        spinner_weight.setAdapter(adapter);
        spinner_date.setAdapter(adapter);

        /**Initialize Buttons and Implement onClick Actions**/
        button_go = (Button) this.findViewById(R.id.ButtonGo);  //Initialize button.

        //Add an onClick for the goButton.
        //When this is clicked, package all filter information into an intent and
        //resume the ViewLifts activity. This is accomplished via the
        //applyFilters function herein.
        button_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean inputCorrect_date = checkInput_date();
                boolean inputCorrect_weight = checkInput_weight();

                if(inputCorrect_date && inputCorrect_weight)
                    applyFilters();
                else
                    incorrectInputDialogue(!inputCorrect_date, !inputCorrect_weight);
            }
        });

        /**Initialize Remaining UI Elements**/
        editText_weight = (EditText) this.findViewById(R.id.WeightEditText);
        editText_date = (EditText) this.findViewById(R.id.DateSingleValEditText);
        editText_date_lower_bound = (EditText) this.findViewById(R.id.DateLowerBound);
        editText_date_upper_bound = (EditText) this.findViewById(R.id.DateUpperBound);
        Button restoreDefaults_button = (Button) this.findViewById(R.id.RestoreDefaultsButton);

        //These edit texts will be modified via a dat pickerp, so they need not be focusable.
        editText_date.setFocusable(false);
        editText_date_lower_bound.setFocusable(false);
        editText_date_upper_bound.setFocusable(false);


        /*****Set up Click Listeners for Each Date Edit Text*****/

        //We need the date in order to set up the listeners.
        Calendar today = Calendar.getInstance();
        year = today.get(today.YEAR);
        month = today.get(today.MONTH);
        day = today.get(today.DATE);

        //Clear the data in the other date editTexts.
        editText_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_date_lower_bound.setText("YYYY/MM/DD");
                editText_date_upper_bound.setText("YYYY/MM/DD");

                showDatePicker(year, month, day, editText_date);
            }
        });

        //Clear the data in the other date editTexts.
        editText_date_lower_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_date.setText("YYYY/MM/DD");

                showDatePicker(year, month, day, editText_date_lower_bound);
            }
        });

        //Clear the data in the other date editTexts.
        editText_date_upper_bound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText_date.setText("YYYY/MM/DD");

                showDatePicker(year, month, day, editText_date_upper_bound);
            }
        });

        //Clear the data in the other date editTexts.
        restoreDefaults_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyDefaultFilters();
            }
        });
    }


    //In onResume, we do 2 things:
    //Receive intents.
    //Initialize UI elements that may change based on actions from other activies.
    @Override
    protected void onResume()
    {
        super.onResume();

        context = this;

        /***********Receive Intent***********/
        //Get the intent from Main Screen that brought us here.
        Intent intent = getIntent();

        //Get strings passed from intent_mainAcivity.
        //Parameters are key values associated with these strings from the MainAcivity.
        selection = intent.getStringArrayExtra(MainScreen.SELECTION);                         //Corresponds to selection for SQLitedatabse.query. Recieved from SetFilters.
        relations = intent.getStringArrayExtra(MainScreen.RELATIONS);                           //Corresponds to <, = or > when filtering daa.
        selectionArgs = intent.getStringArrayExtra(MainScreen.SELECTIONARGS);                       //Corresponds to selectionArgs for SQLitedatabse.query. Recieved from SetFilters.


        /************Give Pertinent UI Elements Their Base Filter Values From Intents****************/
        //Find position of weight and date data in SELECTION.
        int weightPos = -1;
        int datePos = -1;

        //Search the SELECTIONS array.
        for(int c = 0; c < selection.length; c++)
        {
            //If we find "weight" in selecitons.
            if(selection[c].equals("weight"))
                weightPos = c;

            //If we find "date" in selecitons.
            if(selection[c].equals("date"))
                datePos = c;
        }


        //Watch for flag value indicating that weight was not recieved.
        if(weightPos != -1)
        {
            //Set weight values.
            if (relations[weightPos].equals("<="))
                spinner_weight.setSelection(0);

            else if (relations[weightPos].equals("="))
                spinner_weight.setSelection(1);

            else if (relations[weightPos].equals(">="))
                spinner_weight.setSelection(2);

            editText_weight.setText(selectionArgs[weightPos]);
        }
        else
        {
            // >= 0.
            spinner_weight.setSelection(2);
            editText_weight.setText("0");
        }


        //Watch for flag value indicating that date was not recieved.
        if(datePos != -1)
        {
            //Set date values.
            if (relations[datePos].equals("between")) {
                //Separate dates in selectionArgs[datePos]. They are delimited by :
                //First date is lower bound.
                String[] splitSelectionArgs = selectionArgs[datePos].split(":");

                editText_date_lower_bound.setText(splitSelectionArgs[0]);   //First item is lower bound.
                editText_date_upper_bound.setText(splitSelectionArgs[1]);   //Second item is upper bound.

                spinner_date.setSelection(0);
                editText_date.setText("YYYY/MM/DD");
            }

            else if (relations[datePos].equals("<=")) {
                spinner_date.setSelection(0);
                editText_date.setText(selectionArgs[datePos]);

                editText_date_lower_bound.setText("YYYY/MM/DD");
                editText_date_upper_bound.setText("YYYY/MM/DD");
            }

            else if (relations[datePos].equals("=")) {
                spinner_date.setSelection(1);
                editText_date.setText(selectionArgs[datePos]);

                editText_date_lower_bound.setText("YYYY/MM/DD");
                editText_date_upper_bound.setText("YYYY/MM/DD");
            }

            else if (relations[datePos].equals(">=")) {
                spinner_date.setSelection(2);
                editText_date.setText(selectionArgs[datePos]);

                editText_date_lower_bound.setText("YYYY/MM/DD");
                editText_date_upper_bound.setText("YYYY/MM/DD");
            }
        }
        else
        {
            // get today's date.
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

            // <= today's date.
            spinner_date.setSelection(0);
            editText_date.setText(todayString);
        }
    }


    //When the activity is paused, we want to return to the viewLifts activity and
    //send it the filters via intents.
    @Override
    protected void onPause()
    {
        super.onPause();

        boolean inputCorrect_date = checkInput_date();
        boolean inputCorrect_weight = checkInput_weight();

        if(inputCorrect_date && inputCorrect_weight)
            applyFilters();
        else
            applyDefaultFilters();
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
