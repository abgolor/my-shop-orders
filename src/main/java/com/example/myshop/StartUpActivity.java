package com.example.myshop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StartUpActivity extends AppCompatActivity {

    private Button next;
    private EditText link;
    public static String sheetUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        loadData();

        Log.i("SHEET_URL", sheetUrl);

        next = findViewById(R.id.setupBtn_next);
        link = findViewById(R.id.setupEnteredLink);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    //What happens when this intent is resumed
    @Override
    protected void onResume() {
        super.onResume();
        if (!sheetUrl.trim().equals("")){ //User has already inputted some values
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    //The next() is called when the user clicks on the next button
    private void next(){
        if(!link.getText().toString().trim().equals("")){
            sheetUrl = link.getText().toString();
            saveData(); //Save the entered spreadsheet url
            startActivity(new Intent(getApplicationContext(), MainActivity.class)); //Take the user to the main activity
        } else {
            openDialog("Invalid Spreadsheet Link!!", "Your 'My Shop' spreadsheet link cannot be empty.");
        }
    }

    //To show error messages
    private void openDialog(String title, String message) {

        DialogPopup.setTitle = title;
        DialogPopup.setMessage = message;
        DialogPopup dialogPopup = new DialogPopup();
        dialogPopup.show(getSupportFragmentManager(), "information-dialog");

    }

    /*Setting the shared preference to control how this activity is shown to the user */

    //Save the user spreadsheet link so that they won't have to enter it again at app startup
    public void saveData() {
        SharedPreferences.Editor sharedPref = getSharedPreferences("setup-info", Context.MODE_PRIVATE).edit();
        sharedPref.putString("spreadsheet-link", sheetUrl);
        sharedPref.apply();
    }

    //Load the spreadsheet link.
    private void loadData(){
        SharedPreferences sharedPref = getSharedPreferences("setup-info", Context.MODE_PRIVATE);

        String spreadsheet_link = sharedPref.getString("spreadsheet-link", "");

        if(!spreadsheet_link.isEmpty()){
            sheetUrl = spreadsheet_link;
        }
    }

    //Leave the app when the user clicks on the back button
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Important!")
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton("LEAVE NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Nothing: The user rejected just close the dialog
                    }
                });
        alertBuilder.show();
    }
}