/*
Class to work with the DeadliftHelperInternalDatabase and DeadliftHelperExternalDatabase
 classes to run queries on them.


package mymapapplication.miguel.labrador.com.deadlifthelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseManager {

    /****************************/
    /*****Global Variables*******/
    /***************************/

    /*****************************/
    /*****Private Variables******/
    /***************************/


/*
    //FIXME: NAMES STUFF SHITTY
    private DeadliftHelperInternalDatabase internalDB;  //Stores internalDB: names_table or record_deadlift_table.

    //FIXME: NAMES STUFF SHITTY
    private String tableName;

    //Our actual database object.
    //This is used to perform queries.
    private SQLiteDatabase myDatabase;

    //These two variables are used to keep track of which kind of database is being utilized.
    boolean is_internal_DB;
    boolean is_names_table;

    /*****************************/
    /*Helper & Private Functions*/
    /***************************/

    /*****************************/
    /*****Lifecycle Functions****/
    /***************************/

    //Constructor to create an instance of either DeadliftHelperInternalDatabase
    //or DeadliftHelperExternalDatabase. Which is chosen depends on value of internal_flag.
    //is_names_table_flag accompanies internal_flag. There are two types of internal tables:
    //one for names only and one that is an internal version of the table in DeadliftHelperExternalDatabase.

/*

    DatabaseManager(Context context, String databaseName)
    {
        //FIXME: NAMES STUFF SHITTY
        internalDB = new DeadliftHelperInternalDatabase(context, databaseName);
        myDatabase = internalDB.getWritableDatabase();
        tableName = DeadliftHelperInternalDatabase.tableName;
    }


    /*****************************/
    /******Public Functions******/
    /***************************/

    //Function to get all names from the names database.
    //List is used to becasue dynamic allocation is necessary.


/*

    public String[][] getAllRecords()
    {

        //Query the database for all names contained therein.
        //FIXME: will have to fix this when i change the fields
        String[] columnNames = {"weight", "x_accel", "y_accel", "z_accel"};
        Cursor myCursor = myDatabase.query(tableName, columnNames, null, null, null, null, null);

        //Create an array with as many elements as necessary to hold all names.
        //contained in our queried database.
        //names mimics the table that we are querying.
        //each filed in the table is column in the array.
        //Each row in the queried table is a row here as well.
        //Five fields = five columns
        String[][] records = new String[columnNames.length][myCursor.getCount()];

        //Add items from cursor to names array in this order:
        //Add all rows for the first column. Then move to the next column and do the same.
        for(int col = 0; col < columnNames.length; col++)
        {
            //Move to row 0 of column col.
            myCursor.moveToFirst();

            //Add each record for the current column.
            for (int row = 0; row < myCursor.getCount(); row++)
            {
                //Add the item to array.
                records[col][row] = myCursor.getString(col);

                //Move to next item.
                myCursor.moveToNext();
            }
        }

        myCursor.close();   //Always close cursors.

        return records;
    }


    //Function to delete all records from a table.
    public void deleteAllRecords()
    {
        //Just delete da records in dat table.
        myDatabase.delete(tableName, null, null);
    }

    //Function to add a record to the full_table or the deadlift_table.
    public void addRecord(int weight, float x_accel, float y_accel, float z_accel)
    {
        //insert requires that we add items to a ContentValues object.
        //FIXME: will have to fix this when i change the fields
        ContentValues myVals = new ContentValues(4);

        //can be thought of as column name, data.
        myVals.put("weight", weight);
        myVals.put("x_accel", x_accel);
        myVals.put("y_accel", y_accel);
        myVals.put("z_accel", z_accel);

        //Insert the data into the table.
        myDatabase.insert(tableName, null, myVals);
    }
}

*/
