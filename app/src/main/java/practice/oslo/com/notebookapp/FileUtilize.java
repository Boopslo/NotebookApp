package practice.oslo.com.notebookapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Oslo on 6/14/15.
 *
 * this class is responsible for storing picture files and video files
 *
 */


public class FileUtilize {
    // the directory to store the files
    public static final String APP_DIR = "androidpractice";

    // if the external storage can be written
    public static boolean isExternalStorageWritable(){
        // get the status of the external storage
        String status = Environment.getExternalStorageState();
        // if the status if writable
        if(Environment.MEDIA_MOUNTED.equals(status)){
            return true;
        }
        return false;

    }

    // if the external storage can be read
    public static boolean isExternalStorageReadable(){
        String status = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(status) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(status)){
            return true;
        }
        return false;
    }

    // create and return the specific path under public album
    public static File getPublicAlbumStorageDir(String albumName){
        // get the public album path
        File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // create a designated path under the same one as pictures
        File file = new File(pictures, albumName);
        // if create failed
        if(!file.mkdirs()){
            Log.e("getAlbumStorageDir", "Directory not created");
        }
        return file;

    }

    public static File getAlbumStorageDir(Context context, String albumName){
        // get the app-only picture path
        File pictures = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // create a new path
        File file = new File(pictures, albumName);
        if(!file.mkdirs()){
            Log.e("getAlbumStorageDir", "Directory not created");
        }

        return file;
    }

    // create and return a specified directory
    public static File getExternalStorageDir(String dir){

        File result = new File(Environment.getExternalStorageDirectory(), dir);
        if(!isExternalStorageWritable()){
            return null;
        }

        if(!result.exists() && !result.mkdirs()){
            return null;
        }

        return result;
    }


    // read the file and put it into imageView
    public static void fileToImageView(String fileName, ImageView imageView){
        if(new File(fileName).exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(fileName);
            imageView.setImageBitmap(bitmap);
        } else {
            Log.e("fileToImageView", fileName + " not found.");
        }
    }

    // generate the unique file name
    public static String getUniqueFileName(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyy_HHmmss");
        return simpleDateFormat.format(new Date());
    }


}
