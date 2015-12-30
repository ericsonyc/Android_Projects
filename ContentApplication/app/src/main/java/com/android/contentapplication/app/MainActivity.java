package com.android.contentapplication.app;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

public class MainActivity extends Activity {

    private EditText nameET;
    private EditText numberET;
    private Button insertBtn;
    private Button deleteBtn;
    private Button queryBtn;
    private ListView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameET=(EditText)findViewById(R.id.edit_name);
        numberET=(EditText)findViewById(R.id.edit_phone);
        insertBtn=(Button)findViewById(R.id.insert_btn);
        deleteBtn=(Button)findViewById(R.id.delete_btn);
        queryBtn=(Button)findViewById(R.id.query_btn);
        contentView=(ListView)findViewById(R.id.listview);
        insertBtn.setOnClickListener(new OperateOnClickListener());
    }
    class OperateOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            String name=nameET.getText().toString();
            String number=numberET.getText().toString();
            Person p=new Person(name,number);

        }
    }

}
