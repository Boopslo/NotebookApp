package practice.oslo.com.notebookapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/*
    this activity is for selecting and setting what color to be
    for every single note
 */
public class ColorActivity extends Activity {

    private LinearLayout color_gallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        processView();

        ColorListener colorListener = new ColorListener();
        for(Colors c:Colors.values()){
            Button button = new Button(this);
            button.setId(c.parseColorCode());
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(128, 128);
            layout.setMargins(5, 5, 5, 5);
            button.setLayoutParams(layout);
            button.setBackgroundColor(c.parseColorCode());

            button.setOnClickListener(colorListener);
            color_gallery.addView(button);
        }

    }

    private void processView(){
        color_gallery = (LinearLayout)findViewById(R.id.color_gallery);
    }

    private class ColorListener implements View.OnClickListener {

        /*
            new version onClick: it checks if users are using preference to edit color choosing
            or by just clicking on notes to edit its color
         */
        @Override
        public void onClick(View view) {
            // get action from this activity itself
            String action = ColorActivity.this.getIntent().getAction();
            // if the action itself is to be started by preference components
            if(action != null && action.equals("practice.newnotebook.CHOOSE_COLOR")){
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(ColorActivity.this).edit();
                editor.putInt("DEFAULT_COLOR", view.getId());
                editor.commit();
                finish();
            } else {
                // if you change the color by clicking to add or edit an item(note)
                Intent result = getIntent();
                result.putExtra("colorId", view.getId());
                setResult(Activity.RESULT_OK, result);
                finish();
            }
        }
    }

}
