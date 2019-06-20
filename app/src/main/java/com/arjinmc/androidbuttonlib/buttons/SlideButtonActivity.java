package com.arjinmc.androidbuttonlib.buttons;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.arjinmc.androidbuttonlib.R;
import com.arjinmc.buttonlib.SlideButton;

public class SlideButtonActivity extends AppCompatActivity {

    private SlideButton slideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidebutton);

        slideButton = findViewById(R.id.slideButton);
        slideButton.setOnSlideListender(new SlideButton.OnSlideListender() {
            @Override
            public void onStatusChange(boolean status) {
                Log.e("check", String.valueOf(status));
            }
        });

    }
}
