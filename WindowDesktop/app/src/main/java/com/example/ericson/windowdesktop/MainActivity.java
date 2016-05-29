package com.example.ericson.windowdesktop;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyView view = (MyView) findViewById(R.id.windows_layout);
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.ll_bottom);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Hello World!",Toast.LENGTH_LONG).show();
            }
        });

    }


}
