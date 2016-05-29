package com.example.ericson.opencv_example;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private ImageView img;

    private Bitmap srcBitmap;
    private Bitmap grayBitmap;
    private static boolean flag = true;
    private static boolean isFirst = true;
    private static final String TAG = "gao_chun";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img = (ImageView) findViewById(R.id.img);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new ProcessClickListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0,getApplicationContext(),mLoaderCallback);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG,"成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG,"加载失败");
                    break;
            }
        }
    };

    public void procSrc2Gray() {
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();
        srcBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);
        Utils.matToBitmap(grayMat, grayBitmap);
        Log.i(TAG, "procSrc2Gray sucess");
    }

    public class ProcessClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isFirst) {
                procSrc2Gray();
                isFirst = false;
            }
            if (flag) {
                img.setImageBitmap(grayBitmap);
                btn.setText("查看原图");
                flag = false;
            } else {
                img.setImageBitmap(srcBitmap);
                btn.setText("灰度化");
                flag = true;
            }
        }
    }
}
