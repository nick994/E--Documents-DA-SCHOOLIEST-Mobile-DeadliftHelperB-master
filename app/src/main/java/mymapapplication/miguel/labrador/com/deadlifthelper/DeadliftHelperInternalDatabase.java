/*
Class to create an internal database to store the  names_weight_table. This table contains a list of all username, weight combinations
that the user has used so far.
 */

package mymapapplication.miguel.labrador.com.deadlifthelper;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DeadliftHelperInternalDatabase extends SQLiteOpenHelper {


    /*****************************/
    /*****Global Variables******/
    /***************************/


    /*****************************/
    /*****Private Variables******/
    /***************************/

    private SQLiteDatabase myDatabase;  //Stores an instance of this class when we call this.getWritableDatabase().

    private static String tableName = "record_deadlift_table";   //name of the table that we will create.

    //FIXME: MAY NEED REP NUMBER STILL
    private String CREATE_DEADLIFT_TABLE = "CREATE TABLE IF NOT EXISTS " + tableName + "(" +
            "_id integer primary key autoincrement, " +
            "weight integer, " +
            "x_accel real, " +
            "y_accel real, " +
            "z_accel real)";

    /*****************************/
    /*Helper & Private Functions*/
    /***************************/

    /*****************************/
    /*****Lifecycle Functions****/
    /***************************/

    //Database is not created here. It is created in onCreate() below.
    //This class can create a table for names exclusively, or an internal version of the table created in
    //DeadliftHelperExternalDatabase. is_names_table_flag determines this.
    DeadliftHelperInternalDatabase(Context context)
    {
        super(context, "record_deadlift_database", null, 1);
        myDatabase = this.getWritableDatabase();
    }


    //This function is called when getWritableDatabase or getReadableDatabase is called.
    //This is where the database is actually created.
    //Create either the names table or a deadlift table, depending on the
    //value of is_names_table.
    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_DEADLIFT_TABLE);
    }

    //Called when the database is updated. Most likely not necessary to implement anything here.
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion){}


    /*****************************/
    /******Public Functions******/
    /***************************/

    //Function to get all names from the names database.
    //List is used to becasue dynamic allocation is necessary.
    public String[][] getAllRecords()
    {
        //Query the database for all names contained therein.
        //FIXME: will have to fix this when i change the fields to have rep no.
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
        //FIXME: will have to fix this when i change the fields to add rep no.
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
