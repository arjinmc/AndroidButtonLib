package com.arjinmc.androidbuttonlib.buttons;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.arjinmc.androidbuttonlib.R;
import com.arjinmc.buttonlib.DownloadButton;

public class DownloadButtonActivity extends AppCompatActivity {

    private DownloadButton downloadButton;
    private Button btnReset;

    private CountDownTimer timer = new CountDownTimer(50000, 500) {
        @Override
        public void onTick(long millisUntilFinished) {
            //virtual progress
            if (downloadButton != null)
                downloadButton.setProgress(100 - (int) (millisUntilFinished / 500));
//            Log.e("downloadButton","progress:"+downloadButton.getmProgress());
        }

        @Override
        public void onFinish() {
            downloadButton.setProgress(100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadbutton);

        downloadButton = (DownloadButton) findViewById(R.id.btn_download);
        downloadButton.setOnDownloadListener(new DownloadButton.OnDownloadListener() {
            @Override
            public void onReady() {
                //call request on this method
                timer.start();
            }

            @Override
            public void onDone() {
                //finish the done animation will callback this method
                Log.e("onDone", "done");
            }
        });

        btnReset = (Button) findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadButton.reset();
                timer.cancel();
            }
        });

    }
}
