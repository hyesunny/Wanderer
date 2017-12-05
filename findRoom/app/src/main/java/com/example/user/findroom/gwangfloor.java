package com.example.user.findroom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class gwangfloor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gwangfloor);
    }
    public void floorClick(View view){
        Intent emptyroom=null;
        switch(view.getId()){
            case R.id.floor1:
                emptyroom=new Intent(this,slidingpopup.class);
                startActivity(emptyroom);
                break;
            case R.id.floor2:
                break;
            case R.id.floor3:
                break;
            case R.id.floor4:
                break;
            case R.id.floor5:
                break;
            case R.id.floor6:
                break;
            case R.id.floor7:
                break;
            case R.id.floor8:
                break;
            case R.id.floor9:
                break;
            case R.id.floor10:
                break;
            case R.id.floor11:
                emptyroom=new Intent(this,gwang11floor.class);
                startActivity(emptyroom);
                break;
        }
    }
}
