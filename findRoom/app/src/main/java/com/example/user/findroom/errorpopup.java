package com.example.user.findroom;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class errorpopup extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Error");
        setContentView(R.layout.activity_errorpopup);
    }
    public void erclick(View view){
        this.finish();
    }

}
