package practice.oslo.com.notebookapp;


import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteCantOpenDatabaseException;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.io.FileDescriptor;
import java.io.IOException;

/**
 * Created by Oslo on 6/15/15.
 */
public class RecordActivity extends Activity{

    private ImageButton recordButton;
    private boolean isRecording = false;
    private ProgressBar recordVolume;
    private MyRecorder myRecorder;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        processView();
        // create a file name
        Intent intent = getIntent();
        fileName = intent.getStringExtra("fileName");
    }

    public void onSubmit(View view){
        if(isRecording){
            myRecorder.stop();
        }

        // if confirm ok to press the record button , then start recording
        if(view.getId() == R.id.record_confirm){
            Intent intent = getIntent();
            setResult(Activity.RESULT_OK, intent);
        }

        finish();

    }


    private void processView(){
        recordButton = (ImageButton)findViewById(R.id.record_button);
        recordVolume = (ProgressBar)findViewById(R.id.record_volumn);
        // make progressbar invisible
        setProgressBarIndeterminateVisibility(false);
    }

    public void clickRecord(View view){
        // switch status of recording
        isRecording = !isRecording;
        // start recording
        if(isRecording){
            // if it is recording, set the recording picture to the red one
            // means we are now recording
            recordButton.setImageResource(R.drawable.record_red_icon);
            // create the recording object to start recording
            myRecorder = new MyRecorder(fileName);
            // start recording
            myRecorder.start();
            // execute the microphone volume object
            new MicLevelTask().execute();
        } else {

            recordButton.setImageResource(R.drawable.record_dark_icon);
            // set the volume to 0
            recordVolume.setProgress(0);
            myRecorder.stop();
        }
    }

    /*
        this class make the microphone volume displays during recording
     */
    private class MicLevelTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... args) {
            while (isRecording){
                publishProgress();
                try{
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Log.d("RecordActivity", e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values){
            recordVolume.setProgress((int)myRecorder.getAmplitudeEMA());
        }

    }


    private class MyRecorder {

        private static final double EMA_FILTER = 0.6;
        private MediaRecorder recorder = null;
        private double mEMA = 0.0;
        private String output;

        MyRecorder(String output){
            this.output = output;
        }

        public void start(){
            if(recorder == null){
                // new an object if it is null
                recorder = new MediaRecorder();
                /*
                    the order of these method calling is important, cannot change
                 */
                // set the source as microphone
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                // set the output format as 3GP
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                // set the encoder
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                // set the output file
                recorder.setOutputFile(output);

                try {
                    // prepare for recording, has to be after all the above methods called
                    recorder.prepare();
                } catch (IOException e){
                    Log.d("RecordActivity", e.toString());
                }

                recorder.start();
                mEMA = 0.0;

            }
        }

        // stop recording
        public void stop(){
            if(recorder != null){
                recorder.stop();
                // clean the record resources
                recorder.release();
                recorder = null;
            }
        }


        public double getAmplitude(){
            if(recorder != null){
                return (recorder.getMaxAmplitude()/2700.0);
            } else {
                return 0;
            }
        }

        // get the volume of microphone
        public double getAmplitudeEMA(){
            double amp = getAmplitude();
            mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
            return mEMA;
        }

    }



}


