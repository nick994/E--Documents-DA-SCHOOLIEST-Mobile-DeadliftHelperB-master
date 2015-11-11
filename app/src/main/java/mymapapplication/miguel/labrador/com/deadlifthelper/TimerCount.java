package mymapapplication.miguel.labrador.com.deadlifthelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class TimerCount extends AppCompatActivity {

    /*****************************/
    /*****Global Variables******/
    /***************************/

    /*****************************/
    /*****Private Variables******/
    /***************************/

    private TextView timerVal;
    private CountDownTimer mCountDownTimer;
    private Button startStopButton;
    private Button resetButton;
    private int countDownTime;
    private int remainingCountDownTime;
    private boolean isTicking = false;
    Context context;

    /*****************************/
    /*Helper & Private Functions*/
    /***************************/

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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_count);

        /*****Initialize UI Elements****/
        timerVal = (TextView) this.findViewById(R.id.TimeRemaining);
        startStopButton = (Button) this.findViewById(R.id.ButtonStartStop);
        resetButton = (Button) this.findViewById(R.id.ButtonReset);

        /*****Define UI Element Actions****/
        //Button to navigate to Record Deadlift screen.
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If the timer is not ticking, then this is the start button.
                if(!isTicking)
                {
                    //Set the countdown timer & define its actions.
                    mCountDownTimer =
                            //multiply by 1000 to convert milliseconds to seconds
                            new CountDownTimer(remainingCountDownTime * 1000, 1000) {

                                //When the timer ticks, we want to decrement the time displayed by 1.
                                public void onTick(long millisUntilFinished) {
                                    int nextVal = (Integer.parseInt(timerVal.getText().toString()) - 1);
                                    timerVal.setText(Integer.toString(nextVal));
                                    remainingCountDownTime = remainingCountDownTime - 1;
                                }

                                public void onFinish() {
                                    timerVal.setText("0");
                                    resetButton.setEnabled(true);
                                    timerVal.setTextColor(Color.parseColor("#00cc00"));
                                }
                            };

                    isTicking = true;
                    startStopButton.setText("Stop");
                    mCountDownTimer.start();
                    resetButton.setEnabled(false);
                }

                //If the timer is not ticking, then this is the stop button.
                else
                {
                    isTicking = false;
                    startStopButton.setText("Start");
                    mCountDownTimer.cancel();
                    resetButton.setEnabled(true);
                }
            }
        });


        //Button to navigate to Record Deadlift screen.
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerVal.setText(Integer.toString(countDownTime));
                timerVal.setTextColor(Color.parseColor("#000000"));
                remainingCountDownTime = countDownTime;
            }
        });
    }


    //Use onResume to receive intents.
    @Override
    protected void onResume() {
        //Always call super function first.
        super.onResume();

        //Set context for reasons.
        context = this;

        //get the intent from the parent intent
        Intent intent_mainActivity = getIntent();
        countDownTime = Integer.parseInt(intent_mainActivity.getStringExtra(MainScreen.TIMER));
        remainingCountDownTime = countDownTime;
        timerVal.setText(Integer.toString(countDownTime));
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


    //In onPause, we want to send an intent to the MainScreen.
    @Override
    protected void onPause()
    {
        super.onPause();

        //Intent used to navigate to Record Deadlift activity.
        Intent intent_recordDeadlift = new Intent(this, MainScreen.class);

        //Get actual text value from each EditText.
        String time_text = timerVal.getText().toString();

        //Give the intent the necessary data.
        //Public string WEIGHT is used as key for weight_text.
        intent_recordDeadlift.putExtra(MainScreen.TIMER, time_text);

        //Finally, start the Record Deadlift activity.
        startActivity(intent_recordDeadlift);
    }
}
