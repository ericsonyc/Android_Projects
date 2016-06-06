package com.face.android.facetest;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class SignUpload extends Activity {
	private ImageView signImg;
	private String name;
	private String id;
	private Bitmap bmp;
	private String succ = "照片上传成功!";
	private String fail = "照片上传失败!";
	private File file;
	private String result;
	private int data = 2;

	private String path = "/sdcard/FaceData/";
	private String httpURL = "http://116.255.135.175:3004/api/logins/upload_img";
	private String staff_id = "staff_id";
	private String photo = "login_photo";
	private String store_id = "store_id";

	private ProgressDialog proDialog;
	private Handler updateUI = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				proDialog.dismiss();
				if (data == 0) {
					Toast.makeText(getApplicationContext(), name + succ, 0)
							.show();
				} else {
					Toast.makeText(getApplicationContext(), name + fail, 0)
							.show();
				}
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), Sign.class);
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
		setContentView(R.layout.sign_upload);

		Intent intent = getIntent();
		id = intent.getStringExtra("id");
		name = intent.getStringExtra("name");

		signImg = (ImageView) findViewById(R.id.sign_upload_pic);
		bmp = BitmapFactory.decodeFile("/sdcard/FaceDetect/faceDone.jpg");
		signImg.setImageBitmap(bmp);

	}

	public void openActivity(View v) {
		switch (v.getId()) {
		case R.id.sign_upload_ok:
			proDialog = new ProgressDialog(SignUpload.this);
			proDialog.setMessage("正在上传...");
			proDialog.show();
			Thread loginThread = new Thread(new LoginFailureHandler());
			loginThread.start();
			break;
		case R.id.sign_upload_again:
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), CameraActivity.class);
			intent.putExtra("type", 2);
			startActivityForResult(intent, 200);
			break;
		case R.id.sign_break:
			finish();
			break;
		}
	}

	class LoginFailureHandler implements Runnable {

		public void run() {
			try {
				writePhoto();
				file = new File("/sdcard/FaceDetect/faceDone.jpg");
				Map<String, String> map = new HashMap<String, String>();
				AppParams ap = (AppParams) getApplicationContext();
				map.put(staff_id, id);
				map.put(store_id, ap.getStore_id());
				UploadUtil ut = UploadUtil.getInstance();
				result = ut.toUploadFile(file, photo, httpURL, map);
				myJSON(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Message message = new Message();
			message.what = 1;
			updateUI.sendMessage(message);
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
				bmp = BitmapFactory
						.decodeFile("/sdcard/FaceDetect/faceDone.jpg");
				signImg.setImageBitmap(BitmapFactory
						.decodeFile("/sdcard/FaceDetect/faceDone.jpg"));
			}
		}
	}

	public void myJSON(String str) {
		try {
			JSONObject jb = new JSONObject(str);
			data = jb.getInt("data");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public void writePhoto() {
		File file = new File(path + id + "_" + name + ".jpg");
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

}
