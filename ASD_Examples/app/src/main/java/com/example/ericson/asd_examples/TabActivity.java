package com.example.ericson.asd_examples;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ericson on 2016/5/31 0031.
 */

public class TabActivity extends AppCompatActivity {

    private String[] mTitle = new String[20];
    private String[] mData = new String[20];

    {
        for (int i = 0; i < 20; i++) {
            mTitle[i] = "title" + i;
            mData[i] = "data" + i;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tablayout);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tl);
        final ViewPager pager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        PagerAdapter mAdapter = new PagerAdapter() {
            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle[position];
            }

            @Override
            public int getCount() {
                return mData.length;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                TextView tv = new TextView(TabActivity.this);
                tv.setTextSize(30.f);
                tv.setText(mData[position]);
                ((ViewPager) container).addView(tv);
                return tv;
            }

//            @Override
//            public Object instantiateItem(View container, int position) {
//                TextView tv = new TextView(TabActivity.this);
//                tv.setTextSize(30.f);
//                tv.setText(mData[position]);
//                ((ViewPager) container).addView(tv);
//                return tv;
//            }
        };
//        tabLayout.setTabsFromPagerAdapter(mAdapter);
        pager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(pager);
    }
}
