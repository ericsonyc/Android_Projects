package com.face.android.facetest;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.face.android.pojo.ParamsPojo;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class SignLogin extends Activity {

	private EditText sign_user;
	private EditText sign_pwd;
	private ProgressDialog proDialog;
	private String resultLogin;
	private String resultCheckin;

	private int data = 3;
	private String type;
	private String name;
	private String id;
	private String storeId;

	private String httpURL = "http://116.255.135.175:3004/api/logins/";
	private String loginURL = "staff_login";
	private String checkinURL = "staff_checkin";
	private String user = "login_name";
	private String pwd = "login_password";
	private String store_id = "store_id";
	private String staff_id = "staff_id";

	private List<ParamsPojo> lp = new ArrayList<ParamsPojo>();

	private Handler updateUI = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				proDialog.dismiss();
				Intent intent = new Intent();
				if (data == 0) {
					intent.setClass(getApplicationContext(), SignSucc.class);
					intent.putExtra("id", id);
					intent.putExtra("name", name);
					intent.putExtra("status", 0);
				} else if (data == 1) {
					intent.setClass(getApplicationContext(), SignDone.class);
				} else {
					Toast.makeText(getApplicationContext(), "用户名或密码错误!", Toast.LENGTH_SHORT).show();
					intent.setClass(getApplicationContext(), SignFail.class);
				}
				startActivity(intent);
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
		setContentView(R.layout.sign_login);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedSqlLiteObjects()
				.penaltyLog().penaltyDeath().build());

		sign_user = (EditText) findViewById(R.id.sign_user);
		sign_pwd = (EditText) findViewById(R.id.sign_pwd);

		AppParams ap = (AppParams) getApplication();
		storeId = ap.getStore_id();
	}

	public void openActivity(View v) {
		switch (v.getId()) {
		case R.id.sign_login:
			proDialog = new ProgressDialog(SignLogin.this);
			proDialog.setMessage("正在签到...");
			proDialog.show();
			Thread loginThread = new Thread(new LoginFailureHandler());
			loginThread.start();
			break;
		case R.id.sign_break:
			finish();
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	public void myJSON4Login(String str) {
		try {
			JSONObject json = new JSONObject(str);
			type = json.getString("data");
			id = json.getString("login_staff");
			name = json.getString("login_name");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void myJSON4Check(String str) {

		try {
			JSONObject json = new JSONObject(str);
			data = json.getInt("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	class LoginFailureHandler implements Runnable {

		public void run() {
			lp.add(new ParamsPojo(user, sign_user.getText().toString()));
			lp.add(new ParamsPojo(pwd, sign_pwd.getText().toString()));
			lp.add(new ParamsPojo(store_id, storeId));
			resultLogin = HttpUtil.getLogin(httpURL + loginURL, lp);
			myJSON4Login(resultLogin);
			if (type.equals("0")) {
				List<ParamsPojo> checkLp = new ArrayList<ParamsPojo>();
				checkLp.add(new ParamsPojo(staff_id, id));
				resultCheckin = HttpUtil
						.getLogin(httpURL + checkinURL, checkLp);
				myJSON4Check(resultCheckin);
			} else {
				data = 2;
			}
			Message message = new Message();
			message.what = 1;
			updateUI.sendMessage(message);
		}
	}

}
