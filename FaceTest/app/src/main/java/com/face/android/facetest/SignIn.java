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
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class SignIn extends Activity {
	private ImageView signImg;
	private TextView signName;

	private String msg;
	private String name;
	private String id;

	private String resultCheckin;
	private int data;
	private ProgressDialog proDialog;

	private String httpURL = "http://116.255.135.175:3004/api/logins/";
	private String checkinURL = "staff_checkin";
	private String staff_id = "staff_id";

	private Handler updateUI = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				proDialog.dismiss();
				Intent intent = new Intent();
				if (data == 0) {
					intent.setClass(getApplicationContext(), SignSucc.class);
					intent.putExtra("status", 1);
				} else {
					intent.setClass(getApplicationContext(), SignDone.class);
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
		setContentView(R.layout.sign_in);

		Bundle bundle = getIntent().getExtras();
		name = bundle.getString("name");
		id = bundle.getString("id");

		signName = (TextView) findViewById(R.id.sign_name);
		signImg = (ImageView) findViewById(R.id.sign_pic);

		msg = "<font color=\"#CD0000\" size=\"28\">您是:&nbsp;&nbsp;</font><font size=\"24\" color =\"#000000\">"
				+ name + "</font>";
		signName.setText(Html.fromHtml(msg));
		// signName.setTextSize(28f);
		signImg.setImageBitmap(BitmapFactory
				.decodeFile("/sdcard/FaceDetect/faceDone.jpg"));
	}

	public void openActivity(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.sign_break:
			intent.setClass(getApplicationContext(), Sign.class);
			startActivity(intent);
			break;
		case R.id.ok:
			proDialog = new ProgressDialog(SignIn.this);
			proDialog.setMessage("正在签到...");
			proDialog.show();
			Thread loginThread = new Thread(new LoginFailureHandler());
			loginThread.start();
			break;
		case R.id.again:
			intent.setClass(getApplicationContext(), CameraActivity.class);
			intent.putExtra("type", 1);
			startActivityForResult(intent, 200);
			break;
		case R.id.hand:
			intent.setClass(getApplicationContext(), SignLogin.class);
			startActivity(intent);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 200) {
			if (resultCode == RESULT_OK) {
				signImg.setImageBitmap(BitmapFactory
						.decodeFile("/sdcard/FaceDetect/faceDone.jpg"));
				Bundle bundle = data.getExtras();
				name = bundle.getString("name");
				id = bundle.getString("id");

				msg = "<font color=\"#CD0000\" size=\"28\">您是:&nbsp;&nbsp;&nbsp;&nbsp;</font><font size=\"24\" color =\"#000000\">"
						+ name + "</font>";
				signName.setText(Html.fromHtml(msg));
				signName.setTextSize(28f);
			}
		}
	}

	class LoginFailureHandler implements Runnable {

		public void run() {
			List<ParamsPojo> checkLp = new ArrayList<ParamsPojo>();
			checkLp.add(new ParamsPojo(staff_id, id));
			resultCheckin = HttpUtil.getLogin(httpURL + checkinURL, checkLp);
			myJSON4Check(resultCheckin);
			Message message = new Message();
			message.what = 1;
			updateUI.sendMessage(message);
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
}
