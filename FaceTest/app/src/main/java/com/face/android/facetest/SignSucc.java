package com.face.android.facetest;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class SignSucc extends Activity {

	private int status;
	private String name;
	private String id;
	private LinearLayout upload;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 设置横屏模式以及全屏模式
		setContentView(R.layout.sign_succ);

		upload = (LinearLayout) findViewById(R.id.upload);

		Intent intent = getIntent();
		status = intent.getIntExtra("status", 0);

		if (status == 1) {
			upload.setVisibility(View.GONE);
		} else {
			id = intent.getStringExtra("id");
			name = intent.getStringExtra("name");
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	public void openActivity(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.upload:
			intent.setClass(getApplicationContext(), SignUpload.class);
			intent.putExtra("id", id);
			intent.putExtra("name", name);
			startActivity(intent);
			break;
		case R.id.succ_ok:
			intent.setClass(getApplicationContext(), Sign.class);
			startActivity(intent);
			break;
		case R.id.sign_break:
			finish();
			break;
		}
	}
}
