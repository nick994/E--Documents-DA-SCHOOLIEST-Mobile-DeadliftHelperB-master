/*
Class to create an external database to store the full_table. This table contains the x, y, and z accelarometer values.
 */

package mymapapplication.miguel.labrador.com.deadlifthelper;



import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeadliftHelperExternalDatabase {


    /*****************************/
    /*****Global Variables******/
    /***************************/
    public static String full_table_name = "full_table";


    /*****************************/
    /*****Private Variables******/
    /***************************/

    private ParseObject myDatabase;  //The database.

    /*****************************/
    /*Helper & Private Functions*/
    /***************************/

    /*****************************/
    /*****Lifecycle Functions****/
    /***************************/

    //Database is not created here.
    //Constructor for table with base name: full_table.
    DeadliftHelperExternalDatabase()
    {
        myDatabase = new ParseObject(full_table_name);
    }

    //Database is not created here..
    //Constructor for table with name: full_table_name + extraString.
    DeadliftHelperExternalDatabase(String extraString)
    {
        full_table_name = full_table_name + "_" + extraString;
        myDatabase = new ParseObject(full_table_name);
    }


    /*****************************/
    /*****Public Functions****/
    /***************************/

    //Function to delete all records from a table.
    public void deleteAllRecords(final Context context)
    {
        //Just delete the data.
        ParseQuery<ParseObject> query = ParseQuery.getQuery(full_table_name);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> resultList, ParseException e) {

                //If no errors.
                if (e == null) {
                    try {
                        myDatabase.deleteAll(resultList);
                    } catch (ParseException e1) {
                        Log.d("error", "Exception in deleteAllRecords.done where e != null");
                        e1.printStackTrace();
                    }
                }

                //If errors, then indicate what they are.
                else {
                    Log.d("error", "Exception in deleteAllRecords.done where e == null");
                }
            }
        });

        //Save the chagnes.
        myDatabase.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                int duration = Toast.LENGTH_SHORT;
                Toast toastMessage = Toast.makeText(context, "Records deleted!", duration);
                toastMessage.show();
            }
        });  //Must save changes

        myDatabase = new ParseObject(full_table_name);
    }


    //Function to add a record to the full_table or the deadlift_table.
    public void addRecord(String date, String weight, float x_accel, float y_accel, float z_accel, final Context context)
    {
        //Put all the data in the database, yo.
        //Variables here have the same name as the column into which they will be stored.
        myDatabase.put("date", date);
        myDatabase.put("weight", weight);
        myDatabase.put("x_accel", x_accel);
        myDatabase.put("y_accel", y_accel);
        myDatabase.put("z_accel", z_accel);

        myDatabase.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                //If no errors occured.
                if (e == null) {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toastMessage = Toast.makeText(context, "Lift saved!", duration);
                    toastMessage.show();
                }
                //If errors occured.
                else {
                    int duration = Toast.LENGTH_LONG;
                    Toast toastMessage = Toast.makeText(context, "Error: " + e.getMessage(), duration);
                    toastMessage.show();

                    Log.d("error", e.getMessage());
                }
            }
        });  //Must save changes

        myDatabase = new ParseObject(full_table_name);
    }


    //Function to add a record to the full_table or the deadlift_table.
    public void addMultipleRecords(ArrayList<String> weight, ArrayList<String> x_accel, ArrayList<String> y_accel, ArrayList<String> z_accel, ArrayList<String> date, final Context context)
    {
        //Put all the data in the database, yo.
        //Variables here have the same name as the column into which they will be stored.
        myDatabase.addAll("date", date);
        myDatabase.addAll("weight", weight);
        myDatabase.addAll("x_accel", x_accel);
        myDatabase.addAll("y_accel", y_accel);
        myDatabase.addAll("z_accel", z_accel);

        myDatabase.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                //If no errors occured.
                if (e == null) {
                    int duration = Toast.LENGTH_SHORT;
                    Toast toastMessage = Toast.makeText(context, "Lift saved!", duration);
                    toastMessage.show();
                }
                //If errors occured.
                else {
                    int duration = Toast.LENGTH_LONG;
                    Toast toastMessage = Toast.makeText(context, "Error: " + e.getMessage(), duration);
                    toastMessage.show();

                    Log.d("error", e.getMessage());
                }
            }
        });  //Must save changes

        myDatabase = new ParseObject(full_table_name);
    }


    //Function to get all records from the full table.
    //selections holds the fields which we filter.
    //Relations holds the relational operators for those felds.
    //Arguments holds the arguments to which the relations will be applied.
    public float[][] getAllRecords(String[] selections, String[] relations, String[] arguments){
        //If strings are not same length, then error.
        if (selections.length != relations.length || relations.length != arguments.length || selections.length != arguments.length) {
            Log.d("error", "Different lengths of string arrays in getAllRecords.");
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery(full_table_name);   //Create the query object.

        //Returned item. Stores the results like as a table.
        //Has room for 100,000 rows by default. Not sure how to do this dynamically, since it's all in a function below and such.
        //Three columns because we only need x_accel, y_accel, and z_accel.
        final float[][] results = new float[3][10000];

        //Apply filters, if we were passed any.
        if(selections != null)
        {
            //Apply the filters to our query.
            for (int c = 0; c < selections.length; c++) {
                //Choose which function to call based on the relational operator contained in relations[c].
                //Then query with selection[c] as the key and arguments[c] as the value.
                if (relations[c].equals("<="))
                {
                    query.whereLessThanOrEqualTo(selections[c], arguments[c]);
                }
                else if (relations[c].equals("="))
                {
                    query.whereEqualTo(selections[c], arguments[c]);
                }
                else if (relations[c].equals(">="))
                {
                    query.whereGreaterThanOrEqualTo(selections[c], arguments[c]);
                }
                else if(relations[c].equals("between"))
                {
                    //Get arguments.
                    String[] splitArgs = arguments[c].split(":");

                    //Apply filters.
                    query.whereGreaterThanOrEqualTo(selections[c], splitArgs[0]);
                    query.whereLessThanOrEqualTo(selections[c], splitArgs[1]);
                }
            }
        }

        //Check that the query contains at least one record.
        //Gotta do the try/catch because parse.
        try
        {
            if(query.count() == 0)
            {
                float[][] nullflt = new float[1][1];
                nullflt[0][0] = (float)0.0101001;    //Flag value.
                return nullflt;
            }

        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        //Apply the query and store the resulting set of objects in resultList.
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> resultList, ParseException e) {
                //If no errors.
                if (e == null) {
                    /*
                    void done() populates resultString with an average value of each item in each list of the resultList.
                    That is, result list is a table of lists. We must find the average value of each itemin the list.
                    */

                    int recordNum = resultList.size();  //Number of records (lists) that we will examine.
                    int largestItemNum = 0; //Used to keep track of where to put flag value.

                    //Loop through each record in the table to get the sum of the values of each item in each list.
                    //Outer loop loops through records.
                    //Inner loop loops through items in each record's list.
                    //getList("column") finds the column.
                    for(int record = 0; record < recordNum; record++)
                    {
                        for(int item = 0; item < resultList.get(record).getList("x_accel").size(); item++)
                        {
                            results[0][item] += Float.parseFloat((String) resultList.get(record).getList("x_accel").get(item));
                            results[1][item] += Float.parseFloat((String) resultList.get(record).getList("y_accel").get(item));
                            results[2][item] += Float.parseFloat((String) resultList.get(record).getList("z_accel").get(item));

                            if(item > largestItemNum)
                                largestItemNum = item;
                        }
                    }

                    //Finally, calculate the average by dividing each item by the number of records.
                    //Also begin adding items from date and weight to the resultsString.
                    for(int item = 0; item < results[0].length; item++)
                    {
                        results[0][item] /= (float)recordNum;
                        results[1][item] /= (float)recordNum;
                        results[2][item] /= (float)recordNum;
                    }
                    results[0][largestItemNum] = (float) 0.0101001;
                }

                //If errors, then indicate what they are.
                else {
                    Log.d("getAllRecords", "Error: " + e.getMessage());
                }
            }
        });

        return results;
    }
}
