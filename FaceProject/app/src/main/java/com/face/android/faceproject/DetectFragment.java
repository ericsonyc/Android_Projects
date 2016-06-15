package com.face.android.faceproject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.special.ResideMenu.ResideMenu;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bytedeco.javacpp.presets.opencv_core;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ericson on 2016/6/14 0014.
 */

public class DetectFragment extends Fragment implements View.OnClickListener {
    private View parentView;

    private final static String TAG = "DetectActivity";


    private TextView faceNumberTextView;
    private Button detectButton;
    private Button saveButton;
    private ImageView imageView;

    private Bitmap detecedImage;
    private Bitmap writeImage;
    ProgressDialog progressDialog;
    private static boolean saveFlag = true;

    private String xmlPath = "/sdcard/faceProject/haarcascade_frontalface_alt2.xml";
    private File xmlFile;
    private CascadeClassifier cascadeClassifier;
    private List<String> names = new ArrayList<String>();
    private List<Integer> counts = new ArrayList<Integer>();
    //    private static int photoCount = 1;
    private Map<String, Integer> maps = new HashMap<String, Integer>();
    private static boolean firstStart = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.activity_detect, container, false);
        setUpViews();
        return parentView;
    }

    private void setUpViews() {
        faceNumberTextView = (TextView) parentView.findViewById(R.id.faceNumber);
        imageView = (ImageView) parentView.findViewById(R.id.imageView1);
        detectButton = (Button) parentView.findViewById(R.id.detectButton);
        detectButton.setOnClickListener(this);
        saveButton = (Button) parentView.findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == detectButton) {
            //choose picture from where
            final String items[] = {"from phone", "from camer"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("choose a picture");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)//from phone
                    {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, ImageUtils.CHOOSE_FROM_PHONE);
                    } else//take a photo from camera now
                    {
                        Intent CameraPickerintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        CameraPickerintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "temp.jpg")));
                        startActivityForResult(CameraPickerintent, ImageUtils.CHOOSE_FROM_CAMERA);

                    }
                    dialog.dismiss();

                }
            });
            builder.create().show();
        } else if (v == saveButton) {
            if (!saveFlag) {
                File data = new File(ImageUtils.savePath);
//                int count = 0;
                if (!data.exists())
                    data.mkdirs();
                else {
//                    count = new File(ImageUtils.savePath).listFiles().length;
//                        File[] files = new File(ImageUtils.recognizePath).listFiles();
//                        int number = files.length;
//                        for (int i = number - 1; i >= 0; i--) {
//                            files[i].delete();
//                        }
                    if (firstStart) {
                        ImageUtils.deleteFiles(data);
                        data.mkdirs();
                        firstStart = false;
                    }
                }
                createAlertDialog();
//                writeBmp(writeImage, detecedImage.getWidth(), detecedImage.getHeight(), ImageUtils.recognizePath + "/" + (count + 1) + ".jpg");
//                System.out.println("-------------width:" + detecedImage.getWidth() + "   height:" + detecedImage.getHeight());

            } else {
                Toast.makeText(getActivity(), "Saved before", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createAlertDialog() {
        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(getContext());
        View promptsView = li.inflate(R.layout.alert, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (maps.containsKey(userInput.getText().toString())) {
                                    int cc = (maps.get(userInput.getText().toString()) + 1);
//                                    writeBmp(writeImage, writeImage.getWidth(), writeImage.getHeight(), ImageUtils.savePath + "/" + userInput.getText() + "-" + cc + "m.jpg");
                                    writeBmp(writeImage, ImageUtils.savePath, userInput.getText() + "-" + cc + "m.jpg");
                                    maps.put(userInput.getText().toString(), cc);
                                    System.out.println("*********width:" + writeImage.getWidth() + "height:" + writeImage.getHeight());
                                } else {
                                    maps.put(userInput.getText().toString(), 1);
//                                    writeBmp(writeImage, writeImage.getWidth(), writeImage.getHeight(), ImageUtils.savePath + "/" + userInput.getText() + "-1m.jpg");
                                    writeBmp(writeImage, ImageUtils.savePath, userInput.getText() + "-1m.jpg");
                                    System.out.println("*********width:" + writeImage.getWidth() + "height:" + writeImage.getHeight());
                                }

                                Toast.makeText(getActivity(), "Saved successed", Toast.LENGTH_SHORT).show();
                                saveFlag = true;
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public void writeBmp(Bitmap bmp, String path, String fileName) {
//        MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bmp, title, description);
        try {
//            MediaStore.Images.Media.insertImage(getContext().getContentResolver(), path, path.substring(path.lastIndexOf("/") + 1), description);
            saveImageToGallery(getContext(), bmp, path, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveImageToGallery(Context context, Bitmap bmp, String path, String fileName) {
        // 首先保存图片
        File appDir = new File(path);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
    }

    public void writeBmp(Bitmap bmp, int width, int height, String path) {

        File file = new File(path);
        try {
            Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, width, height);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            if (bm.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
                bos.flush();
                bos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, getContext(), mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            String filePath = "";
            if (requestCode == ImageUtils.CHOOSE_FROM_PHONE) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();
//                    Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
//                    cursor.moveToFirst();
//                    int pictureId = cursor.getColumnIndex(ImageColumns.DATA);
//                    filePath = cursor.getString(pictureId);
                } else {
                    Toast.makeText(getActivity(), "choose picture cancel", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == ImageUtils.CHOOSE_FROM_CAMERA) {
                String status = Environment.getExternalStorageState();
                Log.d(TAG, "CHOOSE_FROM_CAMERA" + status);
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    filePath = Environment.getExternalStorageDirectory() + "/temp.jpg";
                } else {
                    Toast.makeText(getActivity(), "no sdcard", Toast.LENGTH_SHORT).show();
                }
            }

//            detecedImage=BitmapFactory.decodeFile(filePath);
//            imageView.setImageBitmap(detecedImage);

            //compress the bitmap
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            detecedImage = BitmapFactory.decodeFile(filePath, options);
//            options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
//            options.inJustDecodeBounds = false;
//            detecedImage = BitmapFactory.decodeFile(filePath, options);
            detecedImage = ImageUtils.getImage(filePath);
            writeImage = ImageUtils.getImage(filePath);
            imageView.setImageBitmap(detecedImage);
//            writeImage = Bitmap.createBitmap(detecedImage, 0, 0, detecedImage.getWidth(), detecedImage.getHeight());

            saveFlag = false;
            //begin  detect
            progressDialog = ProgressDialog.show(getContext(), "face detecing...", "Please wait...", true, false);
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
            Toast.makeText(getContext(), "detect Face number is " + faceNumber, Toast.LENGTH_LONG).show();
            super.handleMessage(msg);
        }

    };

    class DetectThread extends Thread {

        @Override
        public void run() {
            if (detecedImage == null) {
                Toast.makeText(getContext(), "you must have a picture first", Toast.LENGTH_SHORT).show();
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


    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(getContext()) {
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

    public void load_cascade() {
        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt2);
            File cascadeDir = getContext().getDir("cascade", Context.MODE_PRIVATE);
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

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext()) {

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
