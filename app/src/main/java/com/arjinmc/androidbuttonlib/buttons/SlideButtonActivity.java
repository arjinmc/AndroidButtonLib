package com.arjinmc.androidbuttonlib.buttons;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.arjinmc.androidbuttonlib.R;
import com.arjinmc.buttonlib.SlideButton;

public class SlideButtonActivity extends AppCompatActivity {

    private SlideButton slideButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidebutton);

        slideButton = (SlideButton) findViewById(R.id.slideButton);
        slideButton.setmOnSlideListender(new SlideButton.OnSlideListender() {
            @Override
            public void onStatusChange(boolean status) {
                Log.e("check", String.valueOf(status));
            }
        });
    }
}
