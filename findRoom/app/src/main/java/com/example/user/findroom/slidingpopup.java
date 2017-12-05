package com.example.user.findroom;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class slidingpopup extends FragmentActivity {
    int MAX_PAGE=2;
    Fragment cur_fragment=new Fragment();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_slidingpopup);

        ViewPager viewPager=(ViewPager)findViewById(R.id.viewpager);
        viewPager.setAdapter(new adapter(getSupportFragmentManager()));

    }
    private class adapter extends FragmentPagerAdapter{
        public adapter(FragmentManager fm){
            super(fm);
        }
        public Fragment getItem(int position){
            if(position<0 || MAX_PAGE<=position)
                return null;
            switch (position) {
                case 0:
                    cur_fragment=new page_1();
                    break;
                case 1:
                    cur_fragment=new page_2();
                    break;
            }
            return cur_fragment;
        }
        public int getCount(){
            return MAX_PAGE;
        }
    }
}
