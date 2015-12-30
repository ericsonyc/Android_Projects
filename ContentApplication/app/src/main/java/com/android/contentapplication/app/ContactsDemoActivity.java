package com.android.contentapplication.app;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by ericson on 2015/12/24 0024.
 */
public class ContactsDemoActivity extends Activity {
    private Button button1;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.text);
        button1 = (Button) findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = getContacts();
                text.setText(sb.toString());
            }
        });
    }

    private StringBuilder getContacts() {
        StringBuilder sbLog = new StringBuilder();
        ContentResolver cr = this.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (!cursor.moveToFirst()) {
            sbLog.append("获取内容为空！");
            return sbLog;
        }
        if (cursor.moveToFirst()) {
            int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String name = cursor.getString(nameIndex);
            sbLog.append("name=" + name + ";");
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phoneNumbers = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null);
            while(phoneNumbers.moveToNext()){
                String strPhoneNumber=phoneNumbers.getString(phoneNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                sbLog.append("Phone="+strPhoneNumber+";");
            }
            phoneNumbers.close();
            Cursor email=cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID+"="+contactId,null,null);
            while(email.moveToNext()){
                String strEmail=email.getString(email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                sbLog.append("Email="+strEmail+";");
            }
            email.close();
        }
        cursor.close();
        return sbLog;
    }
}
