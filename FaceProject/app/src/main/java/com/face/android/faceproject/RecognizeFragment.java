package com.face.android.faceproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.OpenCVLoader;
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static org.bytedeco.javacpp.opencv_face.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;


/**
 * Created by ericson on 2016/6/14 0014.
 */

public class RecognizeFragment extends Fragment implements View.OnClickListener {
    private View parentView;

    private final static String TAG = "RecognitionActivity";

    private List<String> faceList = new ArrayList<>();
    private Button getPic;
    private Button getLibs;
    private Button recogBtn;
    private ImageView recoImgView;
    private Bitmap detecedImage = null;
    private String filePath = "";
    private String directory = "";
    private TextView faceTextView;

    private static boolean flagFace = false;//只有当选择训练图片后才能进行识别
    private static final int REQUEST_CODE = 123;//人脸识别的代码
    private ArrayList<String> mResults = new ArrayList<String>();//用来保存图片中选择的多张图片
    private List<String> names = new ArrayList<String>();//保存图片的名字
    private List<Integer> counts = new ArrayList<Integer>();//保存同种训练图片的图片个数

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.activity_recognize, container, false);
        setUpView();
        return parentView;
    }

    private void setUpView() {//初始化控件，并设置点击事件
        faceTextView = (TextView) parentView.findViewById(R.id.facetextView);
        getPic = (Button) parentView.findViewById(R.id.get_picture);
        getLibs = (Button) parentView.findViewById(R.id.get_libs);
        recogBtn = (Button) parentView.findViewById(R.id.recognizeBtn);
        recogBtn.setEnabled(false);
        recoImgView = (ImageView) parentView.findViewById(R.id.imageRecognize);
        getPic.setOnClickListener(this);
        getLibs.setOnClickListener(this);
        recogBtn.setOnClickListener(this);


        System.out.println("---------" + getContext().getFilesDir().getAbsolutePath());
        directory = getContext().getFilesDir().getAbsolutePath() + "/photos/";

        Fresco.initialize(getContext());//初始化选择多张图片的类，通过该类可以调用选择多张图片的API
    }

    @Override
    public void onClick(View v) {
        if (v == getPic) {
            if (!flagFace)
                return;
            final String items[] = {"from phone", "from camera"};
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("choose a picture");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)//from phone
                    {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                        photoPickerIntent.setType("image/*");
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
        }
        if (v == recogBtn) {
            if (detecedImage != null) {
                Bitmap temp = ImageUtils.resize(detecedImage);//把测试图片定义成和训练图片一样的大小用于测试
                writeBmp(temp, temp.getWidth(), temp.getHeight(), ImageUtils.tempPath);//保存图片带tempPath
                String result = Identification();//人脸检测
                File file = new File(ImageUtils.tempPath);
                file.delete();//删除临时文件
//                System.out.println("--------------------------" + result);
                Toast.makeText(getContext(), "result: " + result, Toast.LENGTH_SHORT).show();
                faceTextView.setText("face: " + result);//输出检测结果
            }
        }
        if (v == getLibs) {

            flagFace = true;

            //调用ImagesSelectorActivity API
            // start multiple photos selector
            Intent intent = new Intent(getContext(), ImagesSelectorActivity.class);
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

    //OpenCV回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getContext()) {

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
    public void onResume() {
        super.onResume();
        //加载OpenCV
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, getContext(), mLoaderCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == ImageUtils.CHOOSE_FROM_PHONE) {
                if (data != null) {// get picture from the gallery
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);
                    cursor.close();
                } else {
                    Toast.makeText(getContext(), "choose picture cancel", Toast.LENGTH_SHORT).show();
                    return;
                }
                compressBitmap();// compress bitmap
            } else if (requestCode == ImageUtils.CHOOSE_FROM_CAMERA) {//get picture from camera
                String status = Environment.getExternalStorageState();
                Log.d(TAG, "CHOOSE_FROM_CAMERA" + status);
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    filePath = Environment.getExternalStorageDirectory() + "/temp.jpg";
                } else {
                    Toast.makeText(getContext(), "no sdcard", Toast.LENGTH_SHORT).show();
                    return;
                }
                compressBitmap();// compress bitmap
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
                File datafile = new File(ImageUtils.recognizePath);
                if (!datafile.exists()) {
                    datafile.mkdirs();
                } else {
                    ImageUtils.deleteFiles(datafile);//每次重新选择训练图片，删除该路径，重新导入图片
                    datafile.mkdirs();
                }
                mResults = data.getStringArrayListExtra(SelectorSettings.SELECTOR_RESULTS);//获取选择的多张图片
//                assert mResults != null;

                // show results
//                StringBuffer sb = new StringBuffer();
//                sb.append(String.format("Totally %d images selected:", mResults.size())).append("\n");
//                for (String result : mResults) {
//                    sb.append(result).append("\n");
//                }
//                Toast.makeText(RecognitionActivity.this, sb.toString(), Toast.LENGTH_LONG).show();

                analyseLibs(mResults);//根据图片名进行重新写入Recognize文件夹

                for (String result : mResults) {
                    writeLibsToExternal(result);//save picture
                }
                if (mResults.size() > 0)
                    recogBtn.setEnabled(true);

            }
        }
    }

    private void analyseLibs(ArrayList<String> results) {//根据选择的图片，填充names和counts数组
        for (String res : results) {
            String temp;
            temp = res.substring(res.lastIndexOf("/") + 1);
            temp = temp.substring(0, temp.indexOf("-"));
            if (names.contains(temp)) {
                int index = names.indexOf(temp);
                counts.set(index, counts.get(index) + 1);
//                Bitmap bitmap = ImageUtils.getImage(res);
//                String writePath = res.substring(0, res.lastIndexOf("-")) + "-" + counts.get(index) + res.substring(res.indexOf("."));
//                writeBmp(bitmap, bitmap.getWidth(), bitmap.getHeight(), writePath);
//                File file = new File(res);
//                file.delete();
            } else {
                names.add(temp);
                counts.add(1);
            }
        }
    }

    public void writeLibsToExternal(String path) {

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        detecedImage = BitmapFactory.decodeFile(path, options);
//        options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
//        options.inJustDecodeBounds = false;
//        detecedImage = BitmapFactory.decodeFile(path, options);
        detecedImage = ImageUtils.getImage(path);
        Bitmap temp = ImageUtils.resize(detecedImage);//resize picture
        System.out.println("---------width:" + temp.getWidth() + "  ,height:" + temp.getHeight());
        path = path.substring(path.lastIndexOf("/") + 1);
        String name = path.substring(0, path.indexOf("-"));
        String tail = path.substring(path.indexOf("-"));
        //write bitmap to file
        writeBmp(temp, temp.getWidth(), temp.getHeight(), ImageUtils.recognizePath + "/" + names.indexOf(name) + tail);

    }

    public void writeBmp(Bitmap bmp, int width, int height, String path) {//save bitmap to file
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


    public void compressBitmap() {
        if (detecedImage != null)
            detecedImage.recycle();
        //compress the bitmap
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        detecedImage = BitmapFactory.decodeFile(filePath, options);
//        options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
//        options.inJustDecodeBounds = false;
//        detecedImage = BitmapFactory.decodeFile(filePath, options);

        detecedImage = ImageUtils.getImage(filePath);//compress the bitmap
        recoImgView.setImageBitmap(detecedImage);
//        System.out.println("*************width:" + detecedImage.getWidth() + "      height:" + detecedImage.getHeight());
    }

    public String Identification() {//identify human face
        File root = new File(ImageUtils.recognizePath);
        File[] imageFiles = root.listFiles();
        if (imageFiles == null || imageFiles.length == 0) {
            Toast.makeText(getContext(), "has no faces for training", Toast.LENGTH_SHORT).show();
            return "";
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
        faceRecognizer.train(images, labels);//train images
        Mat testImage = imread(ImageUtils.tempPath, CV_LOAD_IMAGE_GRAYSCALE);
        int predictedLabel = faceRecognizer.predict(testImage);//predict image, return the label
        return names.get(predictedLabel);//label就是names图片名对应的索引
    }
}
