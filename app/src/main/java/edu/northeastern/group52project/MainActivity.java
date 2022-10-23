package edu.northeastern.group52project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button weatherBttn = findViewById(R.id.button);
        Intent weatherIntent = new Intent(this, A7Activity.class);
        weatherBttn.setOnClickListener((View v) -> startActivity(weatherIntent));
    }
}