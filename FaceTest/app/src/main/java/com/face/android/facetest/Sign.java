package com.face.android.facetest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import com.face.android.pojo.FacePojo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Sign extends Activity {

    private List<FacePojo> faceList = new ArrayList<FacePojo>();
    private FacePojo fp;
    private boolean flag = false;
    private int index;
    private double value;
    private ProgressDialog proDialog;
    private CascadeClassifier mJavaDetector;
    private String FACE = "/sdcard/FaceDetect/face.jpg";
    private String FACEDONE = "/sdcard/FaceDetect/faceDone.jpg";
    private CameraUtil cu = new CameraUtil();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mJavaDetector = new CascadeClassifier(
                            "/sdcard/FaceDetect/haarcascade_frontalface_alt2.xml");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    private Handler updateUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent();
                    index = cu.Identification(faceList);
                    fp = faceList.get(index);
                    value = cu.CmpPic(fp.getPath()) * 100;
                    // proDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "相似度:" + value, Toast.LENGTH_SHORT)
                            .show();
                    if (value < 70) {
                        intent.setClass(getApplicationContext(), SignFail.class);
                        startActivity(intent);
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", fp.getId());
                        bundle.putString("name", fp.getName());
                        intent.putExtras(bundle);
                        intent.setClass(getApplicationContext(), SignIn.class);
                        startActivity(intent);
                    }
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
        setContentView(R.layout.sign);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_5, this,
                mLoaderCallback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        File[] files = new File("/sdcard/FaceData/").listFiles();
        if (files.length == 2) {
            flag = true;
        } else {
            flag = false;
        }
    }

    public void openActivity(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.sign_bn:
                if (!flag) {
                    if (cu.camera()) {
                        intent.setClass(getApplicationContext(),
                                CameraActivity.class);
                        intent.putExtra("type", 0);
                    } else {
                        Intent intentCamera = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intentCamera, 1);
                    }
                    startActivity(intent);
                } else {
                    intent.setClass(getApplicationContext(), SignFailNull.class);
                    startActivity(intent);
                }
                break;
            case R.id.sign_hand:
                intent.setClass(getApplicationContext(), SignLogin.class);
                startActivity(intent);
                break;
            case R.id.sign_break:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                return;
            }

            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式

            cu.writePhoto(bitmap, bitmap.getWidth(), bitmap.getHeight(), FACE);
            // proDialog = new ProgressDialog(this);
            // proDialog.setMessage("正在识别...");
            // proDialog.show();

            Thread loginThread = new Thread(new LoginFailureHandler());
            loginThread.start();
        }
    }

    class LoginFailureHandler implements Runnable {
        public void run() {
            faceList = cu.LoadFaceData();
            DetectFace();
            Message message = new Message();
            message.what = 1;
            updateUI.sendMessage(message);
        }
    }

    public void DetectFace() {
//		Mat image = Highgui.imread(FACE);
        Mat image = Imgcodecs.imread(FACE);
        MatOfRect faceDetections = new MatOfRect();
        mJavaDetector.detectMultiScale(image, faceDetections);

        int k = 0;

        for (Rect rect : faceDetections.toArray()) {
//            Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
            Imgproc.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
            // 把检测到的人脸重新定义大小后保存成文件
            Mat sub = image.submat(rect);
            Mat mat = new Mat();
            Size size = new Size(100, 100);
            Imgproc.resize(sub, mat, size);
//            Highgui.imwrite(FACEDONE, mat);
            Imgcodecs.imwrite(FACEDONE, mat);
            k++;
        }

        if (k == 0) {
            cu.writePhoto(BitmapFactory.decodeFile(FACE), 100, 100, FACEDONE);
        }
    }

}
