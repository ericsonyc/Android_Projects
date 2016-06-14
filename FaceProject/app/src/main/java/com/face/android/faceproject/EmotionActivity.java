package com.face.android.faceproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.microsoft.projectoxford.emotion.EmotionServiceClient;
import com.microsoft.projectoxford.emotion.EmotionServiceRestClient;
import com.microsoft.projectoxford.emotion.contract.FaceRectangle;
import com.microsoft.projectoxford.emotion.contract.RecognizeResult;
import com.microsoft.projectoxford.emotion.rest.EmotionServiceException;
import com.microsoft.projectoxford.face.FaceServiceRestClient;
import com.microsoft.projectoxford.face.contract.Face;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by ericson on 2016/6/12 0012.
 */

public class EmotionActivity extends Activity implements View.OnClickListener {

    private Button selectImage;
    private ImageView imageView;
    private Bitmap detectedImage;
    private TextView textView;
    private String filePath;
    private Button doRecognizeBtn;
    private EmotionServiceClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion);

        if (client == null) {
            client = new EmotionServiceRestClient(getString(R.string.subscription_key));
        }

        selectImage = (Button) findViewById(R.id.buttonSelectImage);
        imageView = (ImageView) findViewById(R.id.selectedImage);
        textView = (TextView) findViewById(R.id.editTextResult);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        selectImage.setOnClickListener(this);

        doRecognizeBtn = (Button) findViewById(R.id.dorecognizeBtn);
        doRecognizeBtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        if (v == selectImage) {
            final String items[] = {"from phone", "from camera"};
            AlertDialog.Builder builder = new AlertDialog.Builder(EmotionActivity.this);
            builder.setTitle("choose a picture");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0)//from phone
                    {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        photoPickerIntent.setType("image/*");
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
        } else if (v == doRecognizeBtn) {
            if (detectedImage != null) {
                doRecognize();
            }
        }
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
                    Toast.makeText(EmotionActivity.this, "choose picture cancel", Toast.LENGTH_SHORT).show();
                    return;
                }
                compressBitmap();
            } else if (requestCode == StringUtils.CHOOSE_FROM_CAMERA) {
                String status = Environment.getExternalStorageState();
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    filePath = Environment.getExternalStorageDirectory() + "/temp.jpg";
                } else {
                    Toast.makeText(EmotionActivity.this, "no sdcard", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                compressBitmap();
            }
        }
    }

    public void compressBitmap() {
        if (detectedImage != null)
            detectedImage.recycle();
        //compress the bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        detectedImage = BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max((double) options.outWidth / 1024f, (double) options.outHeight / 1024f)));
        options.inJustDecodeBounds = false;
        detectedImage = BitmapFactory.decodeFile(filePath, options);
        imageView.setImageBitmap(detectedImage);
    }

    public void doRecognize() {
        selectImage.setEnabled(false);

        // Do emotion detection using auto-detected faces.
        try {
            new doRequest(true).execute();
        } catch (Exception e) {
            textView.append("Error encountered. Exception is: " + e.toString());
        }

        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        if (faceSubscriptionKey.equalsIgnoreCase("Please_add_the_face_subscription_key_here")) {
            textView.append("\n\nThere is no face subscription key in res/values/strings.xml. Skip the sample for detecting emotions using face rectangles\n");
        } else {
            // Do emotion detection using face rectangles provided by Face API.
            try {
                new doRequest(true).execute();
            } catch (Exception e) {
                textView.append("Error encountered. Exception is: " + e.toString());
            }
        }
    }

    private class doRequest extends AsyncTask<String, String, List<RecognizeResult>> {
        // Store error message
        private Exception e = null;
        private boolean useFaceRectangles = false;

        public doRequest(boolean useFaceRectangles) {
            this.useFaceRectangles = useFaceRectangles;
        }

        @Override
        protected List<RecognizeResult> doInBackground(String... args) {
            if (this.useFaceRectangles == false) {
                try {
                    return processWithAutoFaceDetection();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            } else {
                try {
                    return processWithFaceRectangles();
                } catch (Exception e) {
                    this.e = e;    // Store error
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<RecognizeResult> result) {
            super.onPostExecute(result);
            // Display based on error existence

            if (this.useFaceRectangles == false) {
                textView.append("\n\nRecognizing emotions with auto-detected face rectangles...\n");
            } else {
                textView.append("\n\nRecognizing emotions with existing face rectangles from Face API...\n");
            }
            if (e != null) {
                textView.setText("Error: " + e.getMessage());
                this.e = null;
            } else {
                if (result.size() == 0) {
                } else {
                    Integer count = 0;
                    // Covert bitmap to a mutable bitmap by copying it
                    Bitmap bitmapCopy = detectedImage.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas faceCanvas = new Canvas(bitmapCopy);
                    faceCanvas.drawBitmap(detectedImage, 0, 0, null);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(5);
                    paint.setColor(Color.RED);

                    for (RecognizeResult r : result) {
                        textView.append(String.format("\nFace #%1$d \n", count));
                        textView.append(String.format("\t anger: %1$.5f\n", r.scores.anger));
                        textView.append(String.format("\t contempt: %1$.5f\n", r.scores.contempt));
                        textView.append(String.format("\t disgust: %1$.5f\n", r.scores.disgust));
                        textView.append(String.format("\t fear: %1$.5f\n", r.scores.fear));
                        textView.append(String.format("\t happiness: %1$.5f\n", r.scores.happiness));
                        textView.append(String.format("\t neutral: %1$.5f\n", r.scores.neutral));
                        textView.append(String.format("\t sadness: %1$.5f\n", r.scores.sadness));
                        textView.append(String.format("\t surprise: %1$.5f\n", r.scores.surprise));
                        textView.append(String.format("\t face rectangle: %d, %d, %d, %d", r.faceRectangle.left, r.faceRectangle.top, r.faceRectangle.width, r.faceRectangle.height));
                        faceCanvas.drawRect(r.faceRectangle.left,
                                r.faceRectangle.top,
                                r.faceRectangle.left + r.faceRectangle.width,
                                r.faceRectangle.top + r.faceRectangle.height,
                                paint);
                        count++;
                    }
//                    ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
//                    imageView.setImageDrawable(new BitmapDrawable(getResources(), detectedImage));
                    imageView.setImageBitmap(bitmapCopy);
                }
            }
            selectImage.setEnabled(true);
        }
    }

    private List<RecognizeResult> processWithAutoFaceDetection() throws EmotionServiceException, IOException {
        Log.d("emotion", "Start emotion detection with auto-face detection");

        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        detectedImage.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long startTime = System.currentTimeMillis();
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE STARTS HERE
        // -----------------------------------------------------------------------

        List<RecognizeResult> result = null;
        //
        // Detect emotion by auto-detecting faces in the image.
        //
        result = this.client.recognizeImage(inputStream);

        String json = gson.toJson(result);
        Log.d("result", json);

        Log.d("emotion", String.format("Detection done. Elapsed time: %d ms", (System.currentTimeMillis() - startTime)));
        // -----------------------------------------------------------------------
        // KEY SAMPLE CODE ENDS HERE
        // -----------------------------------------------------------------------
        return result;
    }

    private List<RecognizeResult> processWithFaceRectangles() throws EmotionServiceException, com.microsoft.projectoxford.face.rest.ClientException, IOException {
        Log.d("emotion", "Do emotion detection with known face rectangles");
        Gson gson = new Gson();

        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        detectedImage.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        long timeMark = System.currentTimeMillis();
        Log.d("emotion", "Start face detection using Face API");
        FaceRectangle[] faceRectangles = null;
        String faceSubscriptionKey = getString(R.string.faceSubscription_key);
        FaceServiceRestClient faceClient = new FaceServiceRestClient(faceSubscriptionKey);
        Face faces[] = faceClient.detect(inputStream, false, false, null);
        Log.d("emotion", String.format("Face detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));

        if (faces != null) {
            faceRectangles = new FaceRectangle[faces.length];

            for (int i = 0; i < faceRectangles.length; i++) {
                // Face API and Emotion API have different FaceRectangle definition. Do the conversion.
                com.microsoft.projectoxford.face.contract.FaceRectangle rect = faces[i].faceRectangle;
                faceRectangles[i] = new com.microsoft.projectoxford.emotion.contract.FaceRectangle(rect.left, rect.top, rect.width, rect.height);
            }
        }

        List<RecognizeResult> result = null;
        if (faceRectangles != null) {
            inputStream.reset();

            timeMark = System.currentTimeMillis();
            Log.d("emotion", "Start emotion detection using Emotion API");
            // -----------------------------------------------------------------------
            // KEY SAMPLE CODE STARTS HERE
            // -----------------------------------------------------------------------
            result = this.client.recognizeImage(inputStream, faceRectangles);

            String json = gson.toJson(result);
            Log.d("result", json);
            // -----------------------------------------------------------------------
            // KEY SAMPLE CODE ENDS HERE
            // -----------------------------------------------------------------------
            Log.d("emotion", String.format("Emotion detection is done. Elapsed time: %d ms", (System.currentTimeMillis() - timeMark)));
        }
        return result;
    }
}
