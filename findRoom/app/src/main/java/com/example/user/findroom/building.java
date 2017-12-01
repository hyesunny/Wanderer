package com.example.user.findroom;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class building extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
    }
    public void DialClick(View view){
        switch(view.getId()) {
            case R.id.gwang:
                final LinearLayout linear = (LinearLayout)
                        View.inflate(this, R.layout.popup, null);
                new AlertDialog.Builder(this)
                        .setView(linear)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent floor = new Intent(building.this, gwangfloor.class);
                                startActivity(floor);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                break;
            case R.id.yulchung:
                final LinearLayout ylinear = (LinearLayout)
                        View.inflate(this, R.layout.yulpopup, null);
                new AlertDialog.Builder(this)
                        .setView(ylinear)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent floor = new Intent(building.this, yulchungfloor.class);
                                startActivity(floor);
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
                break;

        }
    }
}
