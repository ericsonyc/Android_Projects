package com.face.android.faceproject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;


public class DetectActivity extends Activity {

    private final static String TAG = "DetectActivity";
    private final static int CHOOSE_FROM_PHONE = 1;
    private final static int CHOOSE_FROM_CAMERA = 2;

    private TextView faceNumberTextView;
    private Button detectButton;
    private ImageView imageView;

    private Bitmap detecedImage;
    ProgressDialog progressDialog;

    private String xmlPath = "sdcard/faceProject/haarcascade_frontalface_alt2.xml";
    private File xmlFile;
    private CascadeClassifier cascadeClassifier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        faceNumberTextView = (TextView) findViewById(R.id.faceNumber);
        imageView = (ImageView) this.findViewById(R.id.imageView1);
        detectButton = (Button) findViewById(R.id.detectButton);
        detectButton.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                //choose picture from where
                final String items[] = {"from phone", "from camer"};
                AlertDialog.Builder builder = new AlertDialog.Builder(DetectActivity.this);
                builder.setTitle("choose a picture");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)//from phone
                        {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, CHOOSE_FROM_PHONE);
                        } else//take a photo from camera now
                        {
                            Intent CameraPickerintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            CameraPickerintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                                    .getExternalStorageDirectory(), "temp.jpg")));
                            startActivityForResult(CameraPickerintent, CHOOSE_FROM_CAMERA);

                        }
                        dialog.dismiss();

                    }
                });
                builder.create().show();

            }
        });


    }


    @Override
    public void onResume() {
        super.onResume();
        //init opencv from opencv mananger
        Log.d(TAG, "onresume");
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            String filePath = "";
            if (requestCode == CHOOSE_FROM_PHONE) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();
//                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
//                    cursor.moveToFirst();
//                    int pictureId = cursor.getColumnIndex(ImageColumns.DATA);
//                    filePath = cursor.getString(pictureId);
                } else {
                    Toast.makeText(DetectActivity.this, "choose picture cancel", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == CHOOSE_FROM_CAMERA) {
                String status = Environment.getExternalStorageState();
                Log.d(TAG, "CHOOSE_FROM_CAMERA" + status);
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    filePath = Environment.getExternalStorageDirectory() + "/temp.jpg";
                } else {
                    Toast.makeText(DetectActivity.this, "no sdcard", Toast.LENGTH_SHORT).show();
                }
            }

//            detecedImage=BitmapFactory.decodeFile(filePath);
//            imageView.setImageBitmap(detecedImage);

            //compress the bitmap
            Options options = new Options();
            options.inJustDecodeBounds = true;
            detecedImage = BitmapFactory.decodeFile(filePath, options);
            options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
            options.inJustDecodeBounds = false;
            detecedImage = BitmapFactory.decodeFile(filePath, options);
            imageView.setImageBitmap(detecedImage);
            //begin  detect
            progressDialog = ProgressDialog.show(DetectActivity.this, "face detecing...", "Please wait...", true, false);
            DetectThread detectThread = new DetectThread();
            detectThread.start();

        }
    }

    Handler updateUiHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            progressDialog.dismiss();
            int faceNumber = msg.arg1;
            faceNumberTextView.setText("Face number : " + faceNumber);
            imageView.setImageBitmap((Bitmap) msg.obj);
            Toast.makeText(DetectActivity.this, "detect Face number is " + faceNumber, Toast.LENGTH_LONG).show();
            super.handleMessage(msg);
        }

    };

    class DetectThread extends Thread {

        @Override
        public void run() {
            if (detecedImage == null) {
                Toast.makeText(DetectActivity.this, "you must have a picture first", Toast.LENGTH_SHORT).show();
            } else {

//                String xmlfilePath = "/sdcard/FaceProject/haarcascade_frontalface_alt2.xml";

//                CascadeClassifier faceDetector = new CascadeClassifier();

                // Bitmap bmptest = BitmapFactory.decodeResource(getResources(),
                // R.drawable.lena);
                Mat testMat = new Mat();
                Utils.bitmapToMat(detecedImage, testMat);

                // Detect faces in the image.
                // MatOfRect is a special container class for Rect.
                MatOfRect faceDetections = new MatOfRect();
                cascadeClassifier.detectMultiScale(testMat, faceDetections);

                Log.i(String.format("Detected %s faces", faceDetections.toArray().length), "");

                int facenum = 0;
                // Draw a bounding box around each face.
                for (Rect rect : faceDetections.toArray()) {
                    Imgproc.rectangle(testMat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
//                    Core.rectangle(testMat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0));
                    ++facenum;
                }

                // Save the visualized detection.
                // Bitmap bmpdone = Bitmap.createBitmap(bmptest.getWidth(),
                // bmptest.getHeight(), Config.RGB_565);
                Utils.matToBitmap(testMat, detecedImage);

                Message message = updateUiHandler.obtainMessage();
                message.arg1 = facenum;
                message.obj = detecedImage;
                message.sendToTarget();


            }
        }
    }


    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(this) {
        public void onManagerConnected(int status) {

            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("face", "hello");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    public void load_cascade(){
        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "output.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            cascadeClassifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
            cascadeClassifier.load(cascadeFile.getAbsolutePath());
            if (cascadeClassifier.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                cascadeClassifier = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());

            cascadeFile.delete();
            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    load_cascade();

                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };
}
