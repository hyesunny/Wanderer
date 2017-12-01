package com.example.user.findroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class yulchungfloor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yulchungfloor);
    }
    public void yfloorClick(View view) {
        Intent yemptyroom = null;
        switch (view.getId()) {
            case R.id.yfloor1:
                break;
            case R.id.yfloor2:
                break;
            case R.id.yfloor3:
                break;
            case R.id.yfloor4:
                yemptyroom = new Intent(this, yul4floor.class);
                startActivity(yemptyroom);
                break;
        }
    }
}
