package practice.oslo.com.notebookapp;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

/**
 * The configuration screen for the {@link ItemAppWidget ItemAppWidget} AppWidget.
 */
public class ItemAppWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    //EditText mAppWidgetText;
    private static final String PREFS_NAME = "practice.newnotebook.ItemAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private ListView itemList;
    private ItemAdapter itemAdapter;
    private List<Item> items;
    private ItemDB itemDB;

    public ItemAppWidgetConfigureActivity() {
        super();
    }
    // the objects that needs for displaying widgets of notes


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_main);

        itemList = (ListView)findViewById(R.id.item_list);
        itemDB = new ItemDB(getApplicationContext());
        items = itemDB.getAll();
        itemAdapter = new ItemAdapter(this, R.layout.single_item, items);
        itemList.setAdapter(itemAdapter);
        itemList.setOnItemClickListener(itemClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }


    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
            final Context context = ItemAppWidgetConfigureActivity.this;
            // read and save the chosen note's note object
            Item item = itemAdapter.getItem(pos);
            saveItemPref(context, mAppWidgetId, item.getId());
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            ItemAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);

            finish();
        }
    };

    // save the index of the item that is choosed
    static void saveItemPref(Context context, int appWidgetId, long id){
        SharedPreferences.Editor pref = context.getSharedPreferences(PREFS_NAME, 0).edit();
        pref.putLong(PREF_PREFIX_KEY + appWidgetId, id);
        pref.commit();
    }

    static long loadItemPref(Context context, int appWidgetId){
        SharedPreferences pref = context.getSharedPreferences(PREFS_NAME, 0);
        long id = pref.getLong(PREF_PREFIX_KEY + appWidgetId, 0);
        return id;
    }

    static void deleteItemPref(Context context, int appWidgetId){
        SharedPreferences.Editor pref = context.getSharedPreferences(PREFS_NAME, 0).edit();
        pref.remove(PREF_PREFIX_KEY + appWidgetId);
        pref.commit();
    }

}

