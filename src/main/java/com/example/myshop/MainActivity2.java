package com.example.myshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.myshop.StartUpActivity.sheetUrl;

public class MainActivity2 extends AppCompatActivity {

    private EditText link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ImageView back = findViewById(R.id.back);
        link = findViewById(R.id.enteredLink);
        Button next = findViewById(R.id.btn_next);

        next.setOnClickListener(v -> next());

        back.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MainActivity.class)));
    }

    private void next(){
        String enteredLink = link.getText().toString();

        if(!enteredLink.trim().equals("")){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            sheetUrl = enteredLink;
            saveData();
            startActivity(intent);
        } else {
            openDialog("Error!", "Your My Shop spreadsheet cannot be empty");
        }
    }

    private void openDialog(String title, String message) {

        DialogPopup.setTitle = title;
        DialogPopup.setMessage = message;
        DialogPopup dialogPopup = new DialogPopup();
        dialogPopup.show(getSupportFragmentManager(), "information-dialog");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void saveData() {
        SharedPreferences.Editor sharedPref = getSharedPreferences("setup-info", Context.MODE_PRIVATE).edit();
        sharedPref.putString("spreadsheet-link", sheetUrl);
        sharedPref.apply();
    }
}