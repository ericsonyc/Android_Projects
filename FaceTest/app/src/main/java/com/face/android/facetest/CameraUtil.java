package com.face.android.facetest;

import static com.googlecode.javacv.cpp.opencv_contrib.createFisherFaceRecognizer;
import static com.googlecode.javacv.cpp.opencv_core.CV_32SC1;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_LOAD_IMAGE_GRAYSCALE;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_COMP_CORREL;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_HIST_ARRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCalcHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCompareHist;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvNormalizeHist;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Environment;
import com.face.android.pojo.FacePojo;
import com.googlecode.javacv.cpp.opencv_contrib.FaceRecognizer;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_core.MatVector;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;

public class CameraUtil {

	private List<FacePojo> faceList = new ArrayList<FacePojo>();
	private int cameraCount;
	private String FACE = "/sdcard/FaceDetect/face.jpg";
	private String FACEDONE = "/sdcard/FaceDetect/faceDone.jpg";

	public boolean camera() {
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras(); // get cameras number

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				try {
					return true;
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}

	public int Identification(List<FacePojo> fp) {
		FaceRecognizer fr = createFisherFaceRecognizer();
		MatVector mv = new MatVector(fp.size());
		CvMat cvMat = CvMat.create(fp.size(), 1, CV_32SC1);

		for (int i = 0; i < fp.size(); i++) {
			IplImage img = cvLoadImage(fp.get(i).getPath(),
					CV_LOAD_IMAGE_GRAYSCALE);
			mv.put(i, img);
			cvMat.put(i, 0, i);
		}
		fr.train(mv, cvMat);

		IplImage testImage = cvLoadImage(FACEDONE, CV_LOAD_IMAGE_GRAYSCALE);
		return fr.predict(testImage);
	}

	public List<FacePojo> LoadFaceData() {
		File[] files = new File("/sdcard/FaceData/").listFiles();
		File f;
		String id;
		String name;
		faceList.clear();
		for (int i = 0; i < files.length; i++) {
			f = files[i];
			if (!f.canRead()) {
				return null;
			}
			if (f.isFile()) {
				id = f.getName().split("_")[0];
				name = f.getName().substring(f.getName().indexOf("_") + 1,
						f.getName().length() - 4);
				faceList.add(new FacePojo(id, name, Environment
						.getExternalStorageDirectory()
						+ "/FaceData/"
						+ f.getName()));
			}
		}
		return faceList;
	}

	public double CmpPic(String path) {
		int l_bins = 20;
		int hist_size[] = { l_bins };

		float v_ranges[] = { 0, 100 };
		float ranges[][] = { v_ranges };

		IplImage Image1 = cvLoadImage(FACE, CV_LOAD_IMAGE_GRAYSCALE);
		IplImage Image2 = cvLoadImage(path, CV_LOAD_IMAGE_GRAYSCALE);

		IplImage imageArr1[] = { Image1 };
		IplImage imageArr2[] = { Image2 };

		CvHistogram Histogram1 = CvHistogram.create(1, hist_size,
				CV_HIST_ARRAY, ranges, 1);
		CvHistogram Histogram2 = CvHistogram.create(1, hist_size,
				CV_HIST_ARRAY, ranges, 1);

		cvCalcHist(imageArr1, Histogram1, 0, null);
		cvCalcHist(imageArr2, Histogram2, 0, null);

		cvNormalizeHist(Histogram1, 100.0);
		cvNormalizeHist(Histogram2, 100.0);

		return cvCompareHist(Histogram1, Histogram2, CV_COMP_CORREL);
	}

	public void writePhoto(Bitmap bmp, int width, int height, String path) {
		File file = new File(path);
		try {
			Bitmap bm = Bitmap.createBitmap(bmp, 0, 0, width, height);
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(file));
			if (bm.compress(Bitmap.CompressFormat.JPEG, 100, bos)) {
				bos.flush();
				bos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
