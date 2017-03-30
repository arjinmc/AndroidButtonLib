package com.arjinmc.androidbuttonlib.buttons;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.arjinmc.androidbuttonlib.R;
import com.arjinmc.buttonlib.SubmitButton;

/**
 * Created by Eminem Lu on 27/3/17.
 * Email arjinmc@hotmail.com
 */

public class SubmitButtonActivity extends AppCompatActivity {

    private SubmitButton submitButton;
    private Button btnForceError;
    private CountDownTimer countDownTimer = new CountDownTimer(6000,500) {
        @Override
        public void onTick(long millisUntilFinished) {

            submitButton.setProgress((int)((6000-millisUntilFinished)/500)*10);
        }

        @Override
        public void onFinish() {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitbutton);

        submitButton = (SubmitButton) findViewById(R.id.btn_submit);
        submitButton.setOnSubmitListener(new SubmitButton.OnSubmitListener() {
            @Override
            public void onReady() {
                //here to call request
                countDownTimer.start();
            }

            @Override
            public void onSignalFinsh() {

            }
        });

        btnForceError = (Button) findViewById(R.id.btn_force_error);
        btnForceError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                submitButton.setErrorStatus();
            }
        });

    }
}
