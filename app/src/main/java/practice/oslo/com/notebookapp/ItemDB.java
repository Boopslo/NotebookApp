package practice.oslo.com.notebookapp;

/**
 * Created by Oslo on 6/14/15.
 *
 * this class deals with all the item-related database operations
 * so to make the program simpler and clearly
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;

public class ItemDB {
    // name od the table
    public static final String TABLE_NAME = "item";
    // the unchangeable column
    public static final String KEY_ID = "_id";

    // other columns in the table
    public static final String DATETIME_COLUMN = "datetime";
    public static final String COLOR_COLUMN = "color";
    public static final String TITLE_COLUMN = "title";
    public static final String CONTENT_COLUMN = "content";
    public static final String FILENAME_COLUMN = "filename";
    public static final String LATITUDE_COLUMN = "latitude";
    public static final String LONGITUDE_COLUMN = "longitude";
    public static final String LASTMODIFIED_COLUMN = "lastmodified";
    public static final String ALARMDATETIME_COLUMN= "alarmdatetime";

    // create a table with the columns
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DATETIME_COLUMN + " INTEGER NOT NULL, " +
                    COLOR_COLUMN + " INTEGER NOT NULL, " +
                    TITLE_COLUMN + " TEXT NOT NULL, " +
                    CONTENT_COLUMN + " TEXT NOT NULL, " +
                    FILENAME_COLUMN + " TEXT, " +
                    LATITUDE_COLUMN + " REAL, " +
                    LONGITUDE_COLUMN + " REAL, " +
                    LASTMODIFIED_COLUMN + " INTEGER, "+
                    ALARMDATETIME_COLUMN + " INTEGER)";

    private SQLiteDatabase database;

    // constructor of the database
    public ItemDB(Context context){
        database = MyDataBaseHelper.getDatabase(context);
    }

    // close the database
    public void closeDB(){
        database.close();
    }

    // method for inserting a new item into the database
    public Item insert(Item item){
        // contentvalues is a set of key-value pairs,
        // where the key represents the column for the table and the value
        // is the value to be inserted in that column.
        ContentValues cv = new ContentValues();

        // so put some contents into the table
        cv.put(DATETIME_COLUMN, item.getDateTime());
        cv.put(COLOR_COLUMN, item.getColor().parseColorCode());
        cv.put(TITLE_COLUMN, item.getTitle());
        cv.put(CONTENT_COLUMN, item.getContent());
        cv.put(FILENAME_COLUMN, item.getFileName());
        cv.put(LATITUDE_COLUMN, item.getLatitude());
        cv.put(LONGITUDE_COLUMN, item.getLongitude());
        cv.put(LASTMODIFIED_COLUMN, item.getLastModified());
        // set date and time for alarm
        cv.put(ALARMDATETIME_COLUMN, item.getAlarmDateTime());

        // insert method will insert a new data into db then return the id of that data
        long id = database.insert(TABLE_NAME, null, cv);
        item.setId(id);
        return item;

    }

    // revise the designated item in the table and return whether updated correct or not
    public boolean update(Item item){
        ContentValues cv = new ContentValues();

        // so put some contents into the table
        cv.put(DATETIME_COLUMN, item.getDateTime());
        cv.put(COLOR_COLUMN, item.getColor().parseColorCode());
        cv.put(TITLE_COLUMN, item.getTitle());
        cv.put(CONTENT_COLUMN, item.getContent());
        cv.put(FILENAME_COLUMN, item.getFileName());
        cv.put(LATITUDE_COLUMN, item.getLatitude());
        cv.put(LONGITUDE_COLUMN, item.getLongitude());
        cv.put(LASTMODIFIED_COLUMN, item.getLastModified());
        // set time for alarm
        cv.put(ALARMDATETIME_COLUMN, item.getAlarmDateTime());

        // update the item by telling the id of that item
        String where = KEY_ID + "=" + item.getId();
        return database.update(TABLE_NAME, cv, where, null) > 0;
    }

    // delete the item of the id
    public boolean delete(long id){
        String where = KEY_ID + "=" + id;
        return database.delete(TABLE_NAME, where, null) > 0;
    }

    // read all the data of the table and return a list of items
    public List<Item> getAll(){
        List<Item> result = new ArrayList<>();
        Cursor cursor = database.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while(cursor.moveToNext()){
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }

    // gets the item object of that id
    public Item getItem(long id){
        // item for storing the results and return
        Item item = null;
        // search by id
        String where = KEY_ID + "=" + id;
        // start searching
        Cursor result = database.query(TABLE_NAME, null, where, null, null, null, null, null);
        // if there is a result
        if(result.moveToFirst()){
            item = getRecord(result);
        }

        result.close();
        return item;

    }

    // encapsulate the current cursor data into item object and return back
    public Item getRecord(Cursor cursor){
        Item result = new Item();

        result.setId(cursor.getLong(0));
        result.setDateTime(cursor.getLong(1));
        result.setColor(ItemActivity.getColors(cursor.getInt(2)));
        result.setTitle(cursor.getString(3));
        result.setContent(cursor.getString(4));
        result.setFileName(cursor.getString(5));
        result.setLatitude(cursor.getDouble(6));
        result.setLongitude(cursor.getDouble(7));
        result.setLastModified(cursor.getLong(8));
        // set alarm time
        result.setAlarmDateTime(cursor.getLong(9));

        return result;
    }

    // get how many data is in the table
    public int getCount(){
        int result = 0;
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if(cursor.moveToNext()){
            result = cursor.getInt(0);
        }

        return result;
    }

    public void sample(){
        Item item1 = new Item(0, new Date().getTime(), Colors.RED, "What's up?", "How are you doing?", "", 0, 0, 0);
        Item item2 = new Item(0, new Date().getTime(), Colors.GREEN, "Working", "and studying", "", 25.04719, 121.516981, 0);
        Item item3 = new Item(0, new Date().getTime(), Colors.BLUE, "Dum dum", "give me gumgum", "", 0, 0, 0);
        Item item4 = new Item(0, new Date().getTime(), Colors.ORANGE, "Hahaha.", "Hahaha", "", 0, 0, 0);

        insert(item1);
        insert(item2);
        insert(item3);
        insert(item4);
    }


}

