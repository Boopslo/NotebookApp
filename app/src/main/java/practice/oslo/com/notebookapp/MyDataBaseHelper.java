package practice.oslo.com.notebookapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Created by Oslo on 6/14/15.
 */
public class MyDataBaseHelper extends SQLiteOpenHelper{

    //the name of the database
    public static final String DATABASE_NAME = "mydata.db";
    // version of sqlite, will change every time if updated
    /*
        because we add the alarm datetime, so have to update the version to 2
     */
    public static final int DB_VERSION = 4;
    // an object of sqlite
    private static SQLiteDatabase database;

    // no need to change under normal condition
    public MyDataBaseHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // create the needed table
        sqLiteDatabase.execSQL(ItemDB.CREATE_TABLE);
    }

    /*
        everytime calls this method, delete the old one and create a new one
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // delete the old table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemDB.TABLE_NAME);
        // create a new table
        onCreate(sqLiteDatabase);
    }

    public static SQLiteDatabase getDatabase(Context context){
        if( (database == null) || (!database.isOpen()) ){
            database = new MyDataBaseHelper(context, DATABASE_NAME, null, DB_VERSION).getWritableDatabase();
        }

        return database;
    }

}
