package com.android.pictures.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.*;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button cameraBtn;
    private Button rotateBtn;
    private Button deleteBtn;
    private Button scanBtn;
    private ListView listview;
    private ImageView imageView;

    private boolean existCamera;

    private boolean existSdCard;
    private String prefix = "img_";
    private int suffix = 1;
    private File imageFile;
    private File imageDir;
    private ArrayList<String> images;
    private ArrayAdapter<String> adapter;
    private boolean hasImage;
    private Bitmap currentBitmap;
    private RelativeLayout relative;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relative=(RelativeLayout)findViewById(R.id.relative);
        cameraBtn = (Button) findViewById(R.id.camera);
        rotateBtn = (Button) findViewById(R.id.rotate);
        deleteBtn = (Button) findViewById(R.id.delete);
        scanBtn = (Button) findViewById(R.id.scan);
        listview = (ListView) findViewById(R.id.listview);
        imageView = (ImageView) findViewById(R.id.imageview);

        cameraBtn.setOnClickListener(this);
        rotateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        scanBtn.setOnClickListener(this);

        existCamera = checkCamera(this);

        existSdCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (existSdCard) {
            imageDir = Environment.getExternalStorageDirectory();
        }

        images = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, R.layout.image_list_item, images);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == cameraBtn) {
            if (!existSdCard) {
                Toast.makeText(this, "Please insert the SD Card!", Toast.LENGTH_LONG).show();
                return;
            }
            if (existCamera) {
                cameraClick();
            }
        } else if (v == rotateBtn) {
            if (hasImage) {
                rotateClick();
            }
        } else if (v == deleteBtn) {
            deleteClick();
        } else if (v == scanBtn) {
            scanClick();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        imageFile = new File(imageDir, adapter.getItem(position));
        if (imageFile.exists()) {
            currentBitmap = getScaleBitmap(this, imageFile.getPath());
            imageView.setImageBitmap(currentBitmap);
            listview.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            rotateBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
            hasImage = true;
        }
    }

    private boolean checkCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }

    private void cameraClick() {
        rotateBtn.setEnabled(true);
        deleteBtn.setEnabled(true);
        relative.removeView(textView);
        imageFile = new File(imageDir, prefix + suffix + ".jpg");
        suffix++;
        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                imageFile.delete();
                imageFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Uri uri = Uri.fromFile(imageFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            listview.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            currentBitmap = getScaleBitmap(this, imageFile.getPath());
            imageView.setImageBitmap(currentBitmap);
            hasImage = true;
            adapter.add(imageFile.getName());
            adapter.notifyDataSetChanged();
        }
    }

    private Bitmap getScaleBitmap(Context cx, String filePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, opt);

        int bmpWidth = opt.outWidth;
        int bmpHeght = opt.outHeight;

//        WindowManager windowManager = (WindowManager) cx.getSystemService(Context.WINDOW_SERVICE);
//        Display display = windowManager.getDefaultDisplay();
        imageView.measure(0, 0);
        int screenWidth = imageView.getWidth();
        int screenHeight = imageView.getHeight();

        opt.inSampleSize = 1;
        if (bmpWidth > bmpHeght) {
            if (bmpWidth > screenWidth)
                opt.inSampleSize = bmpWidth / screenWidth;
        } else {
            if (bmpHeght > screenHeight)
                opt.inSampleSize = bmpHeght / screenHeight;
        }
        opt.inJustDecodeBounds = false;

        bmp = BitmapFactory.decodeFile(filePath, opt);
        return bmp;
    }

    private void scanClick() {
        hasImage = false;
        imageView.setVisibility(View.GONE);
        listview.setVisibility(View.VISIBLE);
        rotateBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        if(adapter.isEmpty()){
            textView=new TextView(this);
            textView.setText("has no image~~");
            RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(relative.getWidth(),relative.getHeight());
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            relative.addView(textView,layoutParams);
        }else{
            relative.removeView(textView);
        }
    }

    private void deleteClick() {
        if (imageFile != null) {
            hasImage = false;
            adapter.remove(imageFile.getName());
            imageFile.delete();
            imageView.setImageBitmap(null);
        }
    }

    private void rotateClick() {
        if (imageFile != null && currentBitmap != null) {
            Matrix m = new Matrix();
            m.setRotate(90);
            currentBitmap = Bitmap.createBitmap(currentBitmap, 0, 0, currentBitmap.getWidth(), currentBitmap.getHeight(), m, true);
            imageView.setImageBitmap(currentBitmap);
        }
    }
}
