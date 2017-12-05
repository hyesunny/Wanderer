package com.example.user.findroom;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
/**
 * Created by user on 2017-12-05.
 */

public class page_1 extends android.support.v4.app.Fragment {
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState){
        LinearLayout linearLayout=(LinearLayout)inflater.inflate(R.layout.page,container,false);
        LinearLayout background=(LinearLayout)linearLayout.findViewById(R.id.background);
        TextView page_num=(TextView)linearLayout.findViewById(R.id.page_num);
        page_num.setText(String.valueOf(1));

        return linearLayout;
    }
}
