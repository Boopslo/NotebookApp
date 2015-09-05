package practice.oslo.com.notebookapp;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.io.File;
import java.util.Calendar;
import java.util.Date;


public class ItemActivity extends ActionBarActivity {

    private EditText title_text, content_text;
    // new variables: numbers for starting different functions
    private static final int START_CAMERA = 0;
    private static final int START_LOCATE = 1;
    private static final int START_ALARM = 2;
    private static final int START_MIC = 3;
    private static final int START_COLOR = 4;

    // Item variable: a single note item
    private Item item;
    // the file name
    private String fileName;
    // the object to put image
    private ImageView picture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        processView();

        /*
            to get the intent so can check if want to edit or create item
         */
        Intent intent = getIntent();
        // the action got from the intent
        String action = intent.getAction();
        // if it is to edit the item
        if(action.equals("android.intent.action.EDIT_ITEM")){
            item = (Item)intent.getExtras().getSerializable("practice.newnotebook.Item");
            //the new version doesn't need the String
            //String titleText = intent.getStringExtra("titleText");
            title_text.setText(item.getTitle());
            content_text.setText(item.getContent());
        } else {
            item = new Item();
        }

    }

    private void processView(){
        title_text = (EditText)findViewById(R.id.title_text);
        content_text = (EditText)findViewById(R.id.content_text);
        // receive the onject of the image view
        picture = (ImageView)findViewById(R.id.picture);
    }


    public void notebookFunction(View view){
        int function = view.getId();
        switch(function){
            case R.id.take_picture:
                // exec the intent so can get picture by using cameras
                Intent useCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // the name of the picture
                File getPicture = configFileName("P", ".jpg");
                Uri uri = Uri.fromFile(getPicture);

                // set the name of the file
                useCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // start the activity
                startActivityForResult(useCamera, START_CAMERA);
                break;
            case R.id.set_location:
                Intent intentMap = new Intent(this, MapsActivity.class);

                // store the values into the Intent
                intentMap.putExtra("lat", item.getLatitude());
                intentMap.putExtra("lng", item.getLongitude());
                intentMap.putExtra("title", item.getTitle());
                intentMap.putExtra("datetime", item.getLocaleDateTime());
                // start the activity and get the result

                startActivityForResult(intentMap, START_LOCATE);

                //startActivity(intentMap);
                break;
            case R.id.set_alarm_clock:
                // call the method to set the alarm
                processAlarm();
                break;
            case R.id.microphone:
                // create the recording file
                final File recordFile = configFileName("R", ".mp3");
                // if the file exists, then ask to play or record again
                if(recordFile.exists()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.title_record).setCancelable(false);
                    builder.setPositiveButton(R.string.record_play, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // to play the record
                            // link with the PlayRecordActivity to play the record
                            Intent playIntent = new Intent(ItemActivity.this, PlayRecordActivity.class);
                            playIntent.putExtra("fileName", recordFile.getAbsolutePath());
                            startActivity(playIntent);
                        }
                    });

                    builder.setNeutralButton(R.string.record_new, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            goToRecord(recordFile);
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, null);
                    // pop out the dialog
                    builder.show();
                } else {
                    // if there is no record file, start the record component
                    goToRecord(recordFile);
                }

                break;
            case R.id.select_color:
                startActivityForResult(new Intent(this, ColorActivity.class), START_COLOR);
                break;

        }
    }

    /*
        this method works when press button to ubmit or cancel creating a new note
     */
    public void onSubmit(View view){
        // when press ok button
        // need to store the value of title and content then return back
        if(view.getId() == R.id.ok_button){
            String titleText = title_text.getText().toString();
            String contentText = content_text.getText().toString();
            /*
                because you pressed ok button, so you set the title as the title
                you inserted, so does the content
             */
            item.setTitle(titleText);
            item.setContent(contentText);

            // if you are not adding a new item but trying to edit one
            if(getIntent().getAction().equals("android.intent.action.EDIT_ITEM")){
                item.setLastModified(new Date().getTime());
            } else {
                // when you are adding a new item
                item.setDateTime(new Date().getTime());
                // create SharePreferences object to get the color information
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                // then can use integer variable to read the default color
                int color = sharedPreferences.getInt("DEFAULT_COLOR", -1);
                // then set the default color as we update it
                item.setColor(getColors(color));
            }

            // Intent object is used to catch the value
            Intent result = getIntent();
            /*
            result.putExtra("titleText", titleText);
            result.putExtra("contentText", contentText);
            */
            // besides setting title and content in the old way
            // now just set it in a new item
            result.putExtra("practice.newnotebook.Item", item);
            // set the replied result as OK
            // so that when return data back to the calling activity, the result can
            // be successfully returned
            setResult(Activity.RESULT_OK, result);

        }

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case START_CAMERA:
                    // after using the camera
                    // set the fileName of the picture
                    item.setFileName(fileName);
                    break;
                case START_LOCATE:
                    /*
                        concept: before calling the MapsActivity to get the location data
                        initialize our variables;
                       after getting the result from MapsActivity, set our variables as the same data
                        of the results that we received
                     */
                    // read the latitude and longitude from the result of second activity
                    // and set the result to the item's data

                    double lat = data.getDoubleExtra("lat", 0.0);
                    double lng = data.getDoubleExtra("lng", 0.0);
                    item.setLatitude(lat);
                    item.setLongitude(lng);

                    break;
                case START_ALARM:
                    break;
                case START_MIC:
                    // set the name of the record file
                    item.setFileName(fileName);
                    break;
                case START_COLOR:
                    int colorId = data.getIntExtra("colorId", Colors.LIGHTGRAY.parseColorCode());
                    item.setColor(getColors(colorId));
                    break;
            }
        }

    }


    @Override
    protected void onResume(){
        super.onResume();

        if(item.getFileName() != null && item.getFileName().length() > 0){
            File file = configFileName("P", ".jpg");
            if(file.exists()){
                picture.setVisibility(View.VISIBLE);
                FileUtilize.fileToImageView(file.getAbsolutePath(), picture);
            }

        }
    }


    public static Colors getColors(int color){
        Colors result = Colors.LIGHTGRAY;
        if(color == Colors.BLUE.parseColorCode()){
            result = Colors.BLUE;
        } else if(color == Colors.GREEN.parseColorCode()){
            result = Colors.GREEN;
        } else if(color == Colors.RED.parseColorCode()){
            result = Colors.RED;
        } else if(color == Colors.ORANGE.parseColorCode()){
            result = Colors.ORANGE;
        } else if(color == Colors.PURPLE.parseColorCode()){
            result = Colors.PURPLE;
        }

        return result;
    }

    private File configFileName(String prefix, String fileType){
        // if already has the file
        if(item.getFileName() != null && item.getFileName().length() > 0){
            fileName = item.getFileName();
        } else {
            // generate a new filename
            fileName = FileUtilize.getUniqueFileName();
        }

        return new File(FileUtilize.getExternalStorageDir(FileUtilize.APP_DIR),
                prefix + fileName + fileType);
    }


    private void goToRecord( File recordFile){
        Intent recordIntent = new Intent(this, RecordActivity.class);
        recordIntent.putExtra("fileName", recordFile.getAbsolutePath());
        startActivityForResult(recordIntent, START_MIC);
    }

    // set the alarm date and time
    private void processAlarm(){
        Calendar calendar = Calendar.getInstance();
        // set the alarm time to the one that is already stored
        if(item.getAlarmDateTime() != 0){
            calendar.setTimeInMillis(item.getAlarmDateTime());
        }

        // read the time values
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // variable to set the alarm
        final Calendar alarm = Calendar.getInstance();
        // set the alert time
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener(){

            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                alarm.set(Calendar.HOUR_OF_DAY, hour);
                alarm.set(Calendar.MINUTE, minute);

                item.setAlarmDateTime(alarm.getTimeInMillis());
            }
        };

        // variable for dialog of setting the time for alarm
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, timeSetListener, hour, minute, true);
        //set alert date
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener(){

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                alarm.set(Calendar.MONTH, month);
                alarm.set(Calendar.DAY_OF_MONTH, day);
                alarm.set(Calendar.YEAR, year);

                timePickerDialog.show();
            }
        };

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, dateSetListener, year, month, day);
        datePickerDialog.show();

    }




}
