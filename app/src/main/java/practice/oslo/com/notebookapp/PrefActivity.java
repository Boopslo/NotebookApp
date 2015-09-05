package practice.oslo.com.notebookapp;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;

/**
 * Created by Oslo on 6/14/15.
 *
 * The preference activity that extends the PreferenceActivity class
 * just overwrite the onCreate method and call addPreferenceFromResource
 * method to implement and set the layout resources
 *
 */
public class PrefActivity extends PreferenceActivity{

    private SharedPreferences sharedPreferences;
    private Preference defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.my_preference);
        defaultColor = (Preference)findPreference("DEFAULT_COLOR");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

    }

    @Override
    protected void onResume(){
        super.onResume();
        int color = sharedPreferences.getInt("DEFAULT_COLOR", -1);
        if(color != -1){
            defaultColor.setSummary(getString(R.string.default_color_summary) +
                    ": " + ItemActivity.getColors(color));
        }

    }


}
