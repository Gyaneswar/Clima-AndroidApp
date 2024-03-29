package com.londonappbrewery.climapm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangeCityController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);

        final EditText editText=findViewById(R.id.queryET);
        ImageButton imageButton=findViewById(R.id.backButton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String cityName=editText.getText().toString();
                Intent t2=new Intent(ChangeCityController.this,WeatherController.class);
                t2.putExtra("cityname",cityName);
                startActivity(t2);
                return false;
            }
        });

    }
}
