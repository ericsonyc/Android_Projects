package com.android.application.app;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by ericson on 2015/12/17 0017.
 */
public class RecordActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Button backBtn;

    private Button recordBtn;
    private Button stopBtn;
    private Button playBtn;
    private Button cycleBtn;
    private Button deleteBtn;

    private TextView textView;
    private ListView listView;

    private ArrayList<String> recordFiles;//preserve all the record files
    private ArrayAdapter<String> adapter;//the adapter of listView;
    private boolean existSdCard;//judge if the phone exists the sdcard
    private boolean isStopRcd;//judge if we stop the recording
    private boolean isPlayed;
    private boolean isPause;
    private String prefixFile = "record_";//prefix of record file
    private int suffix = 1;//suffix of filename
    private File recordFile;//the current record file
    private File recordDir;//record directory
    private File playFile;//the current play file
    private MediaRecorder mediaRecorder;//MediaRecorder
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
        backBtn = (Button) findViewById(R.id.loadall);
        backBtn.setOnClickListener(this);

        recordBtn = (Button) findViewById(R.id.record);
        stopBtn = (Button) findViewById(R.id.stop);
        playBtn = (Button) findViewById(R.id.play);
        cycleBtn = (Button) findViewById(R.id.pause);
        deleteBtn = (Button) findViewById(R.id.delete);
        recordBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        playBtn.setOnClickListener(this);
        cycleBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.textview);
        listView = (ListView) findViewById(R.id.listview);

        //init status
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        cycleBtn.setEnabled(false);
        deleteBtn.setEnabled(false);

        recordFiles = new ArrayList<String>();

        //judge sdcard exists
        existSdCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (existSdCard) {
            recordDir = Environment.getExternalStorageDirectory();
        }

        adapter = new ArrayAdapter<String>(this, R.layout.record_list_item, recordFiles);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
    }

    private void shutdowncycle() {
        if (isPlayed && mediaPlayer != null) {
            isPlayed = false;
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == backBtn) {
            stopBtn.setEnabled(false);
            playBtn.setEnabled(false);
            cycleBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
            getAllRecords();
            isPause = false;
        } else if (v == recordBtn) {
            isPlayed = false;
            recordingClick();
            isPause = false;
        } else if (v == stopBtn) {
            isPlayed = false;
            stopClick();
            isPause = false;
        } else if (v == playBtn) {
            playClick();
            isPause = false;
        } else if (v == deleteBtn) {
            isPlayed = false;
            deleteClick();
            isPause = false;
        } else if (v == cycleBtn) {
            cycleClick();
            isPause = true;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        shutdowncycle();
        playBtn.setEnabled(true);
        cycleBtn.setEnabled(true);
        deleteBtn.setEnabled(true);
        playFile = new File(recordDir.getAbsoluteFile() + File.separator + adapter.getItem(position));
        textView.setText("You choose the file: " + adapter.getItem(position));
    }

    @Override
    protected void onStop() {
        if (mediaRecorder != null && !isStopRcd) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        }
        shutdowncycle();
        super.onStop();
    }

    private void getAllRecords() {
        getAllRecordFiles();
    }

    private void recordingClick() {
        try {
            if (!existSdCard) {
                Toast.makeText(RecordActivity.this, "Please insert the SD Card!", Toast.LENGTH_LONG).show();
                return;
            }
            shutdowncycle();
            recordFile = File.createTempFile(prefixFile + suffix, ".amr", recordDir);
            suffix++;
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mediaRecorder.setOutputFile(recordFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            textView.setText("Start Recording...");
            stopBtn.setEnabled(true);
            playBtn.setEnabled(false);
            cycleBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
            isStopRcd = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopClick() {
        if (recordFile != null) {
            mediaRecorder.stop();
            adapter.add(recordFile.getName());
            mediaRecorder.release();
            mediaRecorder = null;
            textView.setText("Recording stopped.");
            stopBtn.setEnabled(false);
            isStopRcd = true;
        }
    }

    private void playClick() {
        if (playFile != null && playFile.exists()) {
            openFile(playFile);
        }
    }

    private void deleteClick() {
        if (playFile != null) {
            adapter.remove(playFile.getName());
            if (playFile.exists())
                playFile.delete();
            textView.setText("Delete completed!");
        }
    }

    int position;

    private void openFile(File file) {
        isPlayed = true;
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        textView.setText("Begin play...");
        try {
            if (isPause) {
//                mediaPlayer.seekTo(position);
                mediaPlayer.start();
            } else {
                mediaPlayer.setDataSource(RecordActivity.this, Uri.fromFile(file));
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //call the system player program
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(Intent.ACTION_VIEW);
//        String type = ".amr";
//        intent.setDataAndType(Uri.fromFile(file), type);
//        startActivity(intent);
    }

    private void getAllRecordFiles() {//open all the record files in sdcard
        if (existSdCard) {
            File[] files = recordDir.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].getName().indexOf(".") >= 0) {
                        String temp = files[i].getName().substring(files[i].getName().indexOf("."));
                        if (temp.toLowerCase().equals(".amr") && !recordFiles.contains(files[i].getName()))
                            recordFiles.add(files[i].getName());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void cycleClick() {
        if (playFile != null && playFile.exists()) {
            mediaPlayer.pause();
            position = mediaPlayer.getCurrentPosition();
        }
    }

    private void openFile2(File file) {
        isPlayed = true;
        mediaPlayer = new MediaPlayer();
        textView.setText("Begin play...");
        try {
            mediaPlayer.setDataSource(RecordActivity.this, Uri.fromFile(file));
            mediaPlayer.prepare();
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //call the system player program
//        Intent intent = new Intent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setAction(Intent.ACTION_VIEW);
//        String type = ".amr";
//        intent.setDataAndType(Uri.fromFile(file), type);
//        startActivity(intent);
    }
}
