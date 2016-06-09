package com.face.android.faceproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_face.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;

/**
 * Created by ericson on 2016/6/5 0005.
 */

public class RecognitionActivity extends Activity implements View.OnClickListener {

    private final static String TAG = "RecognitionActivity";

    private List<String> faceList = new ArrayList<>();
    private Button getPic;
    private Button getLibs;
    private Button recogBtn;
    private ImageView recoImgView;
    private Bitmap detecedImage = null;
    private String filePath = "";
    private String directory = "";

    private static final int REQUEST_CODE = 123;
    private ArrayList<String> mResults = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize);
        getPic = (Button) findViewById(R.id.get_picture);
        getLibs = (Button) findViewById(R.id.get_libs);
        recogBtn = (Button) findViewById(R.id.recognizeBtn);
        recoImgView = (ImageView) findViewById(R.id.imageRecognize);
        getPic.setOnClickListener(this);
        getLibs.setOnClickListener(this);
        recogBtn.setOnClickListener(this);


        System.out.println("---------" + getApplicationContext().getFilesDir().getAbsolutePath());
        directory = getApplicationContext().getFilesDir().getAbsolutePath() + "/photos/";

        Fresco.initialize(getApplicationContext());
    }

    @Override
    public void onClick(View v) {
        if (v == getPic) {
            final String items[] = {"from phone", "from camera"};
            AlertDialog.Builder builder = new AlertDialog.Builder(RecognitionActivity.this);
            builder.setTitle("choose a picture");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)//from phone
                    {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, StringUtils.CHOOSE_FROM_PHONE);
                    } else//take a photo from camera now
                    {
                        Intent CameraPickerintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        CameraPickerintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "temp.jpg")));
                        startActivityForResult(CameraPickerintent, StringUtils.CHOOSE_FROM_CAMERA);

                    }
                    dialog.dismiss();

                }
            });
            builder.create().show();
        }
        if (v == recogBtn) {
            if (detecedImage != null) {
                int result = Identification(detecedImage);
                System.out.println("--------------------------" + result);
                Toast.makeText(RecognitionActivity.this, "result: " + result, Toast.LENGTH_SHORT).show();
            }
        }
        if (v == getLibs) {
//            final String items[] = {"from phone"};
//            AlertDialog.Builder builder = new AlertDialog.Builder(RecognitionActivity.this);
//            builder.setTitle("choose a picture");
//            builder.setItems(items, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (which == 0)//from phone
//                    {
//                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        photoPickerIntent.setType("image/*");
//                        startActivityForResult(photoPickerIntent, StringUtils.CHOOSE_FROM_PHONE_ALL);
//                    }
//                    dialog.dismiss();
//
//                }
//            });
//            builder.create().show();
            // start multiple photos selector
            Intent intent = new Intent(RecognitionActivity.this, ImagesSelectorActivity.class);
// max number of images to be selected
            intent.putExtra(SelectorSettings.SELECTOR_MAX_IMAGE_NUMBER, 500);
// min size of image which will be shown; to filter tiny images (mainly icons)
            intent.putExtra(SelectorSettings.SELECTOR_MIN_IMAGE_SIZE, 100000);
// show camera or not
            intent.putExtra(SelectorSettings.SELECTOR_SHOW_CAMERA, true);
// pass current selected images as the initial value
            intent.putStringArrayListExtra(SelectorSettings.SELECTOR_INITIAL_SELECTED_LIST, mResults);
// start the selector
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == StringUtils.CHOOSE_FROM_PHONE) {
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();
                } else {
                    Toast.makeText(RecognitionActivity.this, "choose picture cancel", Toast.LENGTH_SHORT).show();
                    return;
                }
                compressBitmap();
            } else if (requestCode == StringUtils.CHOOSE_FROM_CAMERA) {
                String status = Environment.getExternalStorageState();
                Log.d(TAG, "CHOOSE_FROM_CAMERA" + status);
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    filePath = Environment.getExternalStorageDirectory() + "/temp.jpg";
                } else {
                    Toast.makeText(RecognitionActivity.this, "no sdcard", Toast.LENGTH_SHORT).show();
                    return;
                }
                compressBitmap();
            }
//              else if (requestCode == StringUtils.CHOOSE_FROM_PHONE_ALL) {
//                if (data != null) {
//                    Uri selectedImage = data.getData();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                    File datafile = new File(StringUtils.recognizePath);
//                    if (!datafile.exists()) {
//                        datafile.mkdirs();
//                    }
//                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//                    cursor.moveToFirst();
//                    while (!cursor.isLast()) {
//                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                        filePath = cursor.getString(columnIndex);
//                        cursor.moveToNext();
//                        writeLibsToExternal(filePath);
//                    }
//                    cursor.close();
//
//
//                }
//            }
            else if (requestCode == REQUEST_CODE) {
                File datafile = new File(StringUtils.recognizePath);
                if (!datafile.exists()) {
                    datafile.mkdirs();
                } else {
                    deleteFiles(datafile);
                    datafile.mkdirs();
                }
                mResults = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);
                assert mResults != null;

                // show results
//                StringBuffer sb = new StringBuffer();
//                sb.append(String.format("Totally %d images selected:", mResults.size())).append("\n");
//                for (String result : mResults) {
//                    sb.append(result).append("\n");
//                }
//                Toast.makeText(RecognitionActivity.this, sb.toString(), Toast.LENGTH_LONG).show();

                for (String result : mResults) {
                    writeLibsToExternal(result);
                }

            }
        }
    }

    public void writeLibsToExternal(String path) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        detecedImage = BitmapFactory.decodeFile(path, options);
        options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
        options.inJustDecodeBounds = false;
        detecedImage = BitmapFactory.decodeFile(path, options);
        System.out.println("---------width:" + detecedImage.getWidth() + "  ,height:" + detecedImage.getHeight());
        writeBmp(detecedImage, detecedImage.getWidth(), detecedImage.getHeight(), StringUtils.recognizePath + "/" + path.substring(path.lastIndexOf("/") + 1));
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

    public void deleteFiles(File file) {
        if (!file.exists()) {
            return;
        } else {
            if (file.isFile()) {
                file.delete();
                return;
            }
            if (file.isDirectory()) {
                File[] childFiles = file.listFiles();
                if (childFiles == null || childFiles.length == 0) {
                    file.delete();
                    return;
                }
                for (File f : childFiles) {
                    deleteFiles(f);
                }
                file.delete();
            }
        }
    }

    public void compressBitmap() {
        if (detecedImage != null)
            detecedImage.recycle();
        //compress the bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        detecedImage = BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
        options.inJustDecodeBounds = false;
        detecedImage = BitmapFactory.decodeFile(filePath, options);
        recoImgView.setImageBitmap(detecedImage);
//        System.out.println("*************width:" + detecedImage.getWidth() + "      height:" + detecedImage.getHeight());
    }

    public int Identification(Bitmap bitmap) {
        File root = new File(StringUtils.recognizePath);
//        FilenameFilter imgFilter = new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                name = name.toLowerCase();
//                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
//            }
//        };
        File[] imageFiles = root.listFiles();
        if (imageFiles == null || imageFiles.length == 0) {
            Toast.makeText(RecognitionActivity.this, "has no faces for training", Toast.LENGTH_LONG).show();
            return -1;
        }
        MatVector images = new MatVector(imageFiles.length);
        Mat labels = new Mat(imageFiles.length, 1, CV_32SC1);
        IntBuffer labelsBuf = labels.getIntBuffer();
        int counter = 0;
        for (File image : imageFiles) {
            Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);
            int label = Integer.parseInt(image.getName().substring(0, image.getName().indexOf("-")));
            images.put(counter, img);
            labelsBuf.put(counter, label);
            counter++;
        }
        FaceRecognizer faceRecognizer = createFisherFaceRecognizer();
        faceRecognizer.train(images, labels);
        Mat testImage = imread(filePath, CV_LOAD_IMAGE_GRAYSCALE);
        int predictedLabel = faceRecognizer.predict(testImage);
        return predictedLabel;
    }

//    public int Identification(Bitmap bitmap) {
//        File root = new File(StringUtils.recognizePath);
////        FilenameFilter imgFilter = new FilenameFilter() {
////            public boolean accept(File dir, String name) {
////                name = name.toLowerCase();
////                return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
////            }
////        };
//        File[] imageFiles = root.listFiles();
//        if (imageFiles == null || imageFiles.length == 0) {
//            Toast.makeText(RecognitionActivity.this, "has no faces for training", Toast.LENGTH_LONG).show();
//            return -1;
//        }
//        FaceRecognizer fr = createFisherFaceRecognizer();
//        MatVector mv = new MatVector(faceList.size());
//        CvMat cvMat = CvMat.create(faceList.size(), 1, CV_32SC1);
//        for (int i = 0; i < faceList.size(); i++) {
//            IplImage img = cvLoadImage(faceList.get(i),
//                    CV_LOAD_IMAGE_GRAYSCALE);
//            mv.put(i, img);
//            cvMat.put(i, 0, i);
//        }
//        fr.train(mv, cvMat);
//
////        IplImage testImage = cvLoadImage(Environment.getExternalStorageDirectory() + "/FaceDetect/faceDone.jpg", CV_LOAD_IMAGE_GRAYSCALE);
//        return fr.predict(bitmapToIplImage(bitmap));
//
//    }
//
//    public IplImage bitmapToIplImage(Bitmap bitmap) {
//        IplImage iplImage;
//
//        iplImage = IplImage.create(bitmap.getWidth(), bitmap.getHeight(),
//                IPL_DEPTH_8U, 4);
//        bitmap.copyPixelsToBuffer(iplImage.getByteBuffer());
//        return iplImage;
//    }
}
