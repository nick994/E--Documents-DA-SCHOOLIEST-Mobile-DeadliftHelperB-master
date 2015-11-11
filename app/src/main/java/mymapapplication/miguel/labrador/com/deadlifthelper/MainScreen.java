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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.parse.Parse;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;


public class MainScreen extends AppCompatActivity {

    /*****************************/
    /*****Global Variables******/
    /***************************/

    //Used as a key for extras in intents to identify weight.
    public final static String WEIGHT = "DEADLIFT_HELPER_WEIGHT";

    //used as a key for extras in intents to identify timer
    public final static String TIMER = "DEADLIFT_HELPER_TIMER";

    //used as a key for extras in intents to identify filters on data for ViewLifts.
    public final static String SELECTION = "DEADLIFT_HELPER_SELECTION";

    //used as a key for extras in intents to identify filters on data for ViewLifts.
    public final static String RELATIONS = "RELATIONAL_ARGS";

    //used as a key for extras in intents to identify filters on data for ViewLifts.
    public final static String SELECTIONARGS = "DEADLIFT_HELPER_SELECTIONARGS";

    //DeadliftHelperExternalDatabase objects to utilize our databases.
    //These will be initializes here, and used throughout the application.
    public static DeadliftHelperExternalDatabase full_table_database;
    public static String full_name;


    /*****************************/
    /*****Private Variables******/
    /***************************/

    /***** UI Elements***/
    private EditText weight_EditText;
    private EditText timer_EditText;
    private Button recordDeadliftButton;
    private Button viewLiftsButton;
    private Button timerButton;
    private Context context;
    private TextView name;

    //Facebook stuff.
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private ProfilePictureView profilePicture;
    private String userID;

    //Avoid parse error.
    private static boolean parseInitialized = false;

    //Remember the weight that the user entered.
    private static String weight = "";
    private static String timer = "";

    /*****************************/
    /*Helper & Private Functions*/
    /***************************/

    //Function to check if user is logged in to facebook.
    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    //Helper function to start the Record Deadlift activity.
    //This activity will require the weight that was entered here.
    //Build an intent with this data, and start next activity.
    private void navigateTo_recordDeadlift(View view)
    {
        //Get actual text value from each EditText.
        String timerVal_text = timer_EditText.getText().toString();

        //Ensure that we don't send null values  or erroneous values to other activities.
        if(!timerVal_text.equals("") && !isNumeric(timerVal_text))
        {
            int duration = Toast.LENGTH_SHORT;
            Toast toastMessage = Toast.makeText(context, "Please enter a valid duration (in seconds)", duration);
            toastMessage.show();
            return;
        }

        timer = timerVal_text;

        //Intent used to navigate to Record Deadlift activity.
        Intent intent_recordDeadlift = new Intent(this, RecordDeadlift.class);

        //Get actual text value from each EditText.
        String weight_text = weight_EditText.getText().toString();

        //Make sure that we don't send null data.
        //Ensure that we don't send null values  or erroneous values to other activities.
        if(weight_text.equals("") || !isNumeric(weight_text))
        {
            int duration = Toast.LENGTH_SHORT;
            Toast toastMessage = Toast.makeText(context, "Please enter a valid weight", duration);
            toastMessage.show();
            return;
        }

        weight = weight_text;

        //Give the intent the necessary data.
        //Public string WEIGHT is used as key for weight_text.
        intent_recordDeadlift.putExtra(WEIGHT, weight_text);

        //Finally, start the Record Deadlift activity.
        startActivity(intent_recordDeadlift);
    }

    //Helper function to start the View Lifts activity.
    //This activity will require the username and weight that were entered here.
    //Build an intent with this data, and start next activity.
    private void navigateTo_viewLifts(View view)
    {
        //Get actual text value from each EditText.
        String timerVal_text = timer_EditText.getText().toString();

        //Ensure that we don't send null values  or erroneous values to other activities.
        if(!timerVal_text.equals("") && !isNumeric(timerVal_text))
        {
            int duration = Toast.LENGTH_SHORT;
            Toast toastMessage = Toast.makeText(context, "Please enter a valid duration (in seconds)", duration);
            toastMessage.show();
            return;
        }

        timer = timerVal_text;

        //Get actual text value from each EditText.
        //No need to send an intent here, but we still need to get the value weight_EditText so that we can
        //remember it when the user navigates back to this screen.
        String weight_text = weight_EditText.getText().toString();

        //Make sure that we don't send null data.
        //Ensure that we don't send null values  or erroneous values to other activities.
        if(!isNumeric(weight_text) && !weight_text.equals(""))
        {
            int duration = Toast.LENGTH_SHORT;
            Toast toastMessage = Toast.makeText(context, "Please enter a valid weight", duration);
            toastMessage.show();
            return;
        }

        weight = weight_text;

        //Intent used to navigate to View Lifts activity.
        Intent intent_viewLifts = new Intent(this, ViewLifts.class);

        //Initialize string arrays which carry the filters that we will apply to our data.
        //These are 'base' filters which do not actually filter anything.
        String[] selections = {"weight", "date"};
        String[] relations = {">=", "<="};

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

        String[] selectionArgs = {"0", todayString};

        //Finally, put the extras regarding the filters to put on our data in ViewLifts.
        intent_viewLifts.putExtra(SELECTION, selections);
        intent_viewLifts.putExtra(RELATIONS, relations);
        intent_viewLifts.putExtra(SELECTIONARGS, selectionArgs);

        //Finally, start the View Lifts activity.
        startActivity(intent_viewLifts);
    }

    //Helper function to start the Timer activity.
    //This activity will require the time that was entered here
    //Build an intent with this data, and start next activity.
    private void navigateTo_TimerCount(View view)
    {
        //Get actual text value from each EditText.
        //No need to send an intent here, but we still need to get the value weight_EditText so that we can
        //remember it when the user navigates back to this screen.
        String weight_text = weight_EditText.getText().toString();

        //Make sure that we don't send null data.
        //Ensure that we don't send null values  or erroneous values to other activities.
        if(!isNumeric(weight_text) && !weight_text.equals(""))
        {
            int duration = Toast.LENGTH_SHORT;
            Toast toastMessage = Toast.makeText(context, "Please enter a valid weight", duration);
            toastMessage.show();
            return;
        }

        weight = weight_text;

        //Intent used to navigate to timer activity.
        Intent intent_timerCount = new Intent(this, TimerCount.class);

        //Get actual text value from each EditText.
        String timerVal_text = timer_EditText.getText().toString();

        //Ensure that we don't send null values  or erroneous values to other activities.
        if(timerVal_text.equals("") || !isNumeric(timerVal_text))
        {
            int duration = Toast.LENGTH_SHORT;
            Toast toastMessage = Toast.makeText(context, "Please enter a valid duration (in seconds)", duration);
            toastMessage.show();
            return;
        }

        timer = timerVal_text;

        //Give the intent the necessary data.
        //Public string TIMER used as key for
        //timerVal_text.
        intent_timerCount.putExtra(TIMER, timerVal_text);

        //Finally, start the timer activity.
        startActivity(intent_timerCount);
    }

    //Returns whether or not the string, s, is numeric.
    private boolean isNumeric(String s)
    {
        return s.matches("[-+]?\\d*\\.?\\d+");
    }

    //Function to create a dialogue and ask the user to confirm that they wish to remove all records from the database.
    private void clearRecordsWithConfirmation()
    {
        //Do not allow the user to clear records if they are not logged in.
        if(!isLoggedIn())
        {
            //Logout message.
            int duration = Toast.LENGTH_SHORT;
            Toast toastMessage = Toast.makeText(context, "Please Login to Continue", duration);
            toastMessage.show();
        }
        else {

            new AlertDialog.Builder(this)
                    .setTitle("Delete lifts")
                    .setMessage("Are you sure you want to permanently delete ALL recorded lifts?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            full_table_database.deleteAllRecords(context);

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
    }


    //Function to open the "How To" dialogue.
    private void openHowToDialogue()
    {
        LayoutInflater inflater = this.getLayoutInflater();

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage("")
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
        //Default and required.
        super.onCreate(savedInstanceState);

        /***********Initialize Facebook***********/
        FacebookSdk.sdkInitialize(getApplicationContext());

        //Set the view.
        setContentView(R.layout.activity_main_screen);

        /***********Initialize Parse***********/
        if(!parseInitialized)
        {
            Parse.enableLocalDatastore(this);
            Parse.initialize(this, "5t06Nv8LEZ1mQgRyfLRAXTxYLJafarqpiy29oXEN", "FAas8MlnVp7WxyOCmFwBiliY20JMvtRHQhkgZcdd");

            parseInitialized = true;
        }

        /***********Initialize UI Elements***********/
        weight_EditText = (EditText) this.findViewById(R.id.EnterWeightEditText);
        timer_EditText = (EditText) this.findViewById(R.id.TimerEditText);
        recordDeadliftButton = (Button) this.findViewById(R.id.RecordDeadliftButton);
        viewLiftsButton = (Button) this.findViewById(R.id.ViewPreviousLiftsButton);
        timerButton = (Button) this.findViewById(R.id.StartTimerButton);
        name = (TextView) this.findViewById(R.id.UserNameTextView);

        /***********Add Facebook Buttons/Functionality***********/
        profilePicture = (ProfilePictureView) this.findViewById(R.id.ProfilePicture);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) this.findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        //Define actions for callbacks for the login button.
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //Enable the UI
                weight_EditText.setEnabled(true);
                timer_EditText.setEnabled(true);
                timerButton.setEnabled(true);
                viewLiftsButton.setEnabled(true);
                recordDeadliftButton.setEnabled(true);

                //Get the user's ID.
                AccessToken userToken = loginResult.getAccessToken();
                userID = userToken.getUserId();

                //Set the profile picture.
                profilePicture.setProfileId(userID);

                //Get the user's name. Yes, it really does take this many lines of code to do this simple task.
                //First we need the current session.
                //GraphRequest nameRequest = new GraphRequest();
                GraphRequest nameRequest = GraphRequest.newMeRequest(userToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject JSONresponse, GraphResponse response) {
                        //Need try/catch because it says so.
                        try {
                            full_name = (String) JSONresponse.get("name");
                            name.setText(full_name);
                        } catch (JSONException e) {
                            Log.d("error", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                });

                nameRequest.executeAsync();

                //Show a success message.
                int duration = Toast.LENGTH_SHORT;
                Toast toastMessage = Toast.makeText(context, "Login Successful", duration);
                toastMessage.show();
            }

            @Override
            public void onCancel() {
                //Show cancelled mesage.
                int duration = Toast.LENGTH_SHORT;
                Toast toastMessage = Toast.makeText(context, "Login Cancelled", duration);
                toastMessage.show();
            }

            @Override
            public void onError(FacebookException exception) {
                //Show error message.
                int duration = Toast.LENGTH_LONG;
                Toast toastMessage = Toast.makeText(context, exception.getMessage(), duration);
                toastMessage.show();
            }
        });

        //Finally, define actions for when logout occurs.
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){

                    //Reset UI elements
                    profilePicture.setProfileId(null);
                    name.setText("Name");

                    //Disable UI until user logs in again.
                    weight_EditText.setEnabled(false);
                    timer_EditText.setEnabled(false);
                    timerButton.setEnabled(false);
                    viewLiftsButton.setEnabled(false);
                    recordDeadliftButton.setEnabled(false);
                }
            }
        };

        //Require that user is logged in to use the app: disable most UI elements until user is logged in.
        //In onResume so that isLoggedIn() does not crash app.
        if(!isLoggedIn())
        {
            int duration = Toast.LENGTH_LONG;
            Toast toastMessage = Toast.makeText(context, "Please Login to Continue", duration);
            toastMessage.show();

            weight_EditText.setEnabled(false);
            timer_EditText.setEnabled(false);
            timerButton.setEnabled(false);
            viewLiftsButton.setEnabled(false);
            recordDeadliftButton.setEnabled(false);
        }
        else
        {
            //Setup facebook UI stuff.
            AccessToken userToken = AccessToken.getCurrentAccessToken();
            userID = userToken.getUserId();

            //Set the profile picture.
            profilePicture.setProfileId(userID);

            //Set the profile picture.
            profilePicture.setProfileId(userID);

            //Get the user's name. Yes, it really does take this many lines of code to do this simple task.
            //First we need the current session.
            //GraphRequest nameRequest = new GraphRequest();
            GraphRequest nameRequest = GraphRequest.newMeRequest(userToken, new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject JSONresponse, GraphResponse response) {
                    //Need try/catch because it says so.
                    try {
                        full_name = (String) JSONresponse.get("name");
                        name.setText(full_name);
                    } catch (JSONException e) {
                        Log.d("error", e.getMessage());
                        e.printStackTrace();
                    }
                }
            });

            nameRequest.executeAsync();
        }


        /*******External DB*******/
        full_table_database = new DeadliftHelperExternalDatabase(userID);
        //full_table_database = new DeadliftHelperExternalDatabase();


        /***********Add Navigation Buttons***********/
        //Button to navigate to Record Deadlift screen.
        recordDeadliftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo_recordDeadlift(view);
            }
        });

        //Button to navigate to View Lifts screen.
        viewLiftsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateTo_viewLifts(view);
            }
        });

        //Button to navigate to Timer screen.
        timerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                navigateTo_TimerCount(view);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    //Define actions that must occur when the activity is resumed.
    //Populate the ListView.
    //Ensure that no text is left in textViews.
    @Override
    public void onResume()
    {
        super.onResume();       //Always call super method.

        //Accept intent from timer.
        Intent intent_mainActivity = getIntent();
        if(intent_mainActivity.getStringExtra(TIMER) != null) {
            timer = intent_mainActivity.getStringExtra(TIMER);
        }

        //Set text of eac EditText.
        timer_EditText.setText(timer);
        weight_EditText.setText(weight);

        //Set context for later.
        context = this;
    }

    //Companion for onResume.
    @Override
    public void onPause()
    {
        super.onPause();
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
