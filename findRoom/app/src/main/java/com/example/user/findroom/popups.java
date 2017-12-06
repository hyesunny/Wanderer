package com.example.user.findroom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class popups extends Activity {
    public String cin1119;
    public String cin1120;
    public String color="#ff0300";
    public TextView errtext;
    public double n;
    public int dtoi;
    private TextView txtProgress;
    private ProgressBar progressBar;
    private int pStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Gwanggaeto");
        setContentView(R.layout.activity_popups);

        errtext=(TextView)findViewById(R.id.errtext);
        txtProgress = (TextView) findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Intent next =getIntent();
        cin1119=next.getExtras().getString("cin1119");
        cin1120=next.getExtras().getString("cin1120");
        if(cin1119.equals("-1")||cin1120.equals("-1")){
            errtext.setTextColor(Color.parseColor(color));
            errtext.setText("Picam error!\nCongestion Rate is not trusted");
        }

        n=(((Double.parseDouble(cin1119)+Double.parseDouble(cin1120))/(2.0)))*100;
        dtoi=(int)n;
        if(dtoi<0)
            dtoi=0;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (pStatus <= dtoi) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(pStatus);
                                    txtProgress.setText(pStatus + "%");
                                }
                            });
                            try {
                                Thread.sleep(25);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            pStatus++;
                        }
                    }
                }).start();
            }
        }, 300);
    }

    public void fclick(View view) {
        Intent emptyroom = null;
        switch (view.getId()) {
            case R.id.yes:
                emptyroom = new Intent(this, gwangfloor.class);
                startActivity(emptyroom);
                this.finish();
                break;
            case R.id.no:
                this.finish();
        }
    }




}
