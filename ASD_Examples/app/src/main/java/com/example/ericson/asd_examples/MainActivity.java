package com.example.ericson.asd_examples;

import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TabLayout tabLayout=(TabLayout)findViewById(R.id.tl);
        for (int i=0;i<20;i++)
            tabLayout.addTab(tabLayout.newTab().setText("TAB"+i));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(MainActivity.this,tab.getText(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public void btnClick(View view){
        Snackbar.make(view,"真要点我吗？",Snackbar.LENGTH_LONG).setAction("真的！",new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"你真的点我了！",Toast.LENGTH_SHORT).show();
            }
        }).show();
    }

    public void imageClick(View view){
        Snackbar.make(view,"真要点我吗？",Snackbar.LENGTH_LONG).setAction("真的！",new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"你真的点我了！",Toast.LENGTH_SHORT).show();
            }
        }).show();
    }
}
