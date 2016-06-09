package com.face.android.facetest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.face.android.pojo.FacePojo;
import com.face.android.pojo.ParamsPojo;
import com.face.android.pojo.StaffPojo;

// ----------------------------------------------------------------------

public class Login extends Activity {

    private EditText user;
    private EditText pwd;
    private Button bn;
    private ProgressDialog proDialog;

    private String httpURL = "http://116.255.135.175:3004/api/logins/check_staff";
    private List<ParamsPojo> lp = new ArrayList<ParamsPojo>();
    private String user_key = "staff_name";
    private String pwd_key = "staff_password";
    private HttpPost httpRequest = null;
    private List<NameValuePair> params = null;
    private HttpResponse httpResponse;
    private String resultStr;

    private String message;
    private int type = 2;
    private String storeId;
    private List<StaffPojo> staffList = new ArrayList<StaffPojo>();
    private JSONArray staffArr;
    private File mCascadeFile;
    private CameraUtil cu = new CameraUtil();

    private Handler updateUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (type == 0) {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), Sign.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                    proDialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置横屏模式以及全屏模式
        setContentView(R.layout.login);

        //性能调优，开启严格模式
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedSqlLiteObjects()
                .penaltyLog().penaltyDeath().build());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
//		Toast.makeText(getApplicationContext(),
//				"屏幕分辨率为:" + dm.widthPixels + " * " + dm.heightPixels, 1).show();

        user = (EditText) findViewById(R.id.user);
        pwd = (EditText) findViewById(R.id.pwd);
        bn = (Button) findViewById(R.id.login);

        // DisplayMetrics dm = new DisplayMetrics();
        // getWindowManager().getDefaultDisplay().getMetrics(dm);

        Init();

        bn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                proDialog = new ProgressDialog(Login.this);
                proDialog.setMessage("正在登录...");
                proDialog.show();
                Thread loginThread = new Thread(new LoginFailureHandler());
                loginThread.start();
            }
        });
    }

    public void Init() {
        File data = new File("/sdcard/FaceData");
        if (!data.exists()) {
            data.mkdirs();
        }
        File Detect = new File("/sdcard/FaceDetect");
        if (!Detect.exists()) {
            Detect.mkdirs();
        }
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.one);
        cu.writePhoto(bmp, 100, 100, "/sdcard/FaceData/0_识别失败.jpg");
        cu.writePhoto(bmp, 100, 100, "/sdcard/FaceData/-1_识别失败.jpg");
        writeXML();
    }

    public void writeXML() {
        InputStream is = getResources().openRawResource(
                R.raw.haarcascade_frontalface_alt2);
        File cascadeDir = new File("/sdcard/FaceDetect");
        mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt2.xml");

        try {
            FileOutputStream os = new FileOutputStream(mCascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePhoto(Bitmap bmp) {
        File file = new File("/sdcard/FaceData/0_识别失败.jpg");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            if (bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                bos.flush();
                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

    }

    class LoginFailureHandler implements Runnable {

        public void run() {
            lp.add(new ParamsPojo(user_key, user.getText().toString()));
            lp.add(new ParamsPojo(pwd_key, pwd.getText().toString()));
            resultStr = HttpUtil.getLogin(httpURL, lp);
            myJSON(resultStr);
            Message message = new Message();
            message.what = 1;
            updateUI.sendMessage(message);
        }
    }

    public String getLogin() {
        httpRequest = new HttpPost(httpURL);
        params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("staff_name", user.getText()
                .toString()));
        params.add(new BasicNameValuePair("staff_password", pwd.getText()
                .toString()));

        try {
            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            httpResponse = new DefaultHttpClient().execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(httpResponse.getEntity());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void myJSON(String str) {
        try {
            JSONObject json = new JSONObject(str);
            message = json.getString("msg");
            type = json.getInt("d_type");
            storeId = json.getString("store_id");
            AppParams ap = (AppParams) getApplicationContext();
            ap.setStore_id(storeId);
            staffArr = json.getJSONArray("staffs");
            JSONObject jb;
            for (int i = 0; i < staffArr.length(); i++) {
                jb = (JSONObject) staffArr.opt(i);
                staffList.add(new StaffPojo(jb.getInt("id"), jb
                        .getString("name"), jb.getString("photo")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
