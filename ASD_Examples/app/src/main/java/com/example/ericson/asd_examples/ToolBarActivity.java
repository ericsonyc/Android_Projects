package com.example.ericson.asd_examples;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ToolBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collapse);
        final CollapsingToolbarLayout ctl=(CollapsingToolbarLayout)findViewById(R.id.ctl);

    }
}
