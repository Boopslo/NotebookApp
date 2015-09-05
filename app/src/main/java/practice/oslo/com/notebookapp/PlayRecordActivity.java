package practice.oslo.com.notebookapp;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 * Created by Oslo on 6/16/15.
 */
public class PlayRecordActivity extends Activity{

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_record);

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("fileName");
        // create the designated source MediaPlayer object
        Uri uri = Uri.parse(fileName);
        mediaPlayer = MediaPlayer.create(this, uri);

    }

    @Override
    protected void onStop(){
        if(mediaPlayer.isPlaying()){
            // stop playing the record
            mediaPlayer.stop();

        }
        // clean the mediaplayer object
        mediaPlayer.release();
        super.onStop();
    }

    public void onSubmit(View view){
        finish();
    }

    public void clickToPlay(View view){
        mediaPlayer.start();
    }

    public void clickPause(View view){
        mediaPlayer.pause();
    }

    public void clickStop(View view){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        // go back to beginning
        mediaPlayer.seekTo(0);
    }


}
