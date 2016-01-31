package com.android.androidexercises.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by ericson on 2016/1/24 0024.
 */
public class ButtonActivity extends Activity {
    private Button btnOne;
    private Button btnTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.button1);
        btnOne=(Button)findViewById(R.id.btnOne);
        btnTwo=(Button)findViewById(R.id.btnTwo);
        btnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnTwo.getText().toString().equals("One")){
                    btnOne.setEnabled(false);
                    btnTwo.setText("Two");
                }else{
                    btnOne.setEnabled(true);
                    btnTwo.setText("One");
                }
            }
        });
    }
}
