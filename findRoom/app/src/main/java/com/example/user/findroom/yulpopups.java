package com.example.user.findroom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class yulpopups extends Activity {
    public String cin401;
    public String cin404;
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
        setTitle("Yulgok&Chungmu");
        setContentView(R.layout.activity_yulpopups);

        errtext=(TextView)findViewById(R.id.errtext);
        txtProgress = (TextView) findViewById(R.id.txtProgress);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Intent next =getIntent();
        cin401=next.getExtras().getString("cin401");
        cin404=next.getExtras().getString("cin404");
        if(cin401.equals("-1")||cin404.equals("-1")){
            errtext.setTextColor(Color.parseColor(color));
            errtext.setText("Picam error!\nCongestion Rate is not trusted");
        }

        n=(((Double.parseDouble(cin401)+Double.parseDouble(cin404))/(2.0)))*100;
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

    public void yclick(View view) {
        Intent emptyroom = null;
        switch (view.getId()) {
            case R.id.yes:
                emptyroom = new Intent(this,yulchungfloor.class);
                startActivity(emptyroom);
                this.finish();
                break;
            case R.id.no:
                this.finish();
        }
    }




}

