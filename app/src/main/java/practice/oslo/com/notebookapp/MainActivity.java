package practice.oslo.com.notebookapp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ListView itemList;
    private TextView show_app_name;

    // create a new object of type of ItemAdapter, sub for ArrayList and ArrayAdapter
    private ItemAdapter itemAdapter;
    // the list to store all the notes
    private List<Item> items;
    // the objects for the menu of the list
    private MenuItem add_item, search_item, revert_item, share_item, delete_item;
    // the numbers of items that are selected
    private int selectedCount = 0;

    private ItemDB itemDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processView();
        processListener();
        // database object initialize
        itemDB = new ItemDB(getApplicationContext());
        // if database is empty, default it with samples
        if(itemDB.getCount() == 0){
            itemDB.sample();
        }
        // get all the data from database
        items = itemDB.getAll();

        // create our own adapter object
        // use our itemAdapter and itemList to display, don't use the array one
        itemAdapter = new ItemAdapter(this, R.layout.single_item, items);
        itemList.setAdapter(itemAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == Activity.RESULT_OK){

            // requestCode = 0 means to new an item
            // check in the notebookFunction, add_item switch
            Item item = (Item)data.getExtras().getSerializable("practice.newnotebook.Item");
            // if the alarm setting needs to be revised
            boolean updateAlarm = false;
            if(requestCode == 0){
                item = itemDB.insert(item);

                // add the item into the list
                items.add(item);
                itemAdapter.notifyDataSetChanged();
            } else if(requestCode == 1){
                // requestCode = 1 means to edit an exist item
                // read the index
                int position = data.getIntExtra("position", -1);
                if(position != -1) {
                    // (****) read the original setting of alarm
                    Item origin = itemDB.getItem(item.getId());
                    // update the setting
                    updateAlarm = (item.getAlarmDateTime() != origin.getAlarmDateTime());

                    itemDB.update(item);

                    items.set(position, item);
                    itemAdapter.notifyDataSetChanged();
                    //this.list.set(position, titleText);
                    //adapter.notifyDataSetChanged();
                }
            }

            // set the alarm
            if(item.getAlarmDateTime() != 0 && updateAlarm){
                Intent intentAlarm = new Intent(this, AlarmReceiver.class);
                intentAlarm.putExtra("title", item.getTitle());

                PendingIntent pi = PendingIntent.getBroadcast(this, (int)item.getId(), intentAlarm, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.RTC_WAKEUP, item.getAlarmDateTime(), pi);
            }

        }

    }

    /*
        this function is used for View objects' initialization
     */
    private void processView(){
        itemList = (ListView)findViewById(R.id.item_list);
        show_app_name = (TextView)findViewById(R.id.show_app_name);
    }

    /*
        this method deals with the listeners' creation and
     */
    private void processListener(){

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                //Toast.makeText(MainActivity.this, list.get(pos), Toast.LENGTH_LONG).show();

                //use the same name of the action to create another intent object
                //for the second activity(the edit item one)
                // use Item object for the new version
                Item item = itemAdapter.getItem(pos);
                // when the user has selected some of the notes, the screen should
                // display differently
                if(selectedCount > 0){
                    processMenu(item);
                    itemAdapter.setItemIndex(pos, item);
                } else {
                    Intent intent = new Intent("android.intent.action.EDIT_ITEM");
                    intent.putExtra("position", pos);
                    intent.putExtra("practice.newnotebook.Item", item);
                    // integer "1" for result means to revise an item
                    startActivityForResult(intent, 1);
                }
            }
        };
        itemList.setOnItemClickListener(itemClickListener);

        AdapterView.OnItemLongClickListener itemLongClickListener = new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int pos, long id) {
                //Toast.makeText(MainActivity.this,"Long: " + list.get(pos), Toast.LENGTH_LONG).show();
                Item item = itemAdapter.getItem(pos);
                // this method handles what to display when the user has selected some
                // notes
                processMenu(item);
                itemAdapter.setItemIndex(pos, item);
                return true;
                //return false;
            }
        };
        itemList.setOnItemLongClickListener(itemLongClickListener);

    }

    /*
        this method will display notes is selected and the screen display will
        be different
     */
    private void processMenu(Item item){
        //if the item is not empty, means we need to deal with the selected notes
        if(item != null){
            // the items to be set are those not selected
            item.setSelected(!item.getSelected());

            if(item.getSelected()){
                selectedCount++;
            } else {
                selectedCount--;
            }
        }

        // set the notes visible according to the items selected
        add_item.setVisible(selectedCount == 0);
        search_item.setVisible(selectedCount == 0);
        revert_item.setVisible(selectedCount > 0);
        share_item.setVisible(selectedCount > 0);
        delete_item.setVisible(selectedCount > 0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        // (** new version **)
        // have to get the Items from the menu
        // assign them the value
        add_item = menu.findItem(R.id.add_item);
        search_item = menu.findItem(R.id.search_item);
        revert_item = menu.findItem(R.id.revert_item);
        delete_item = menu.findItem(R.id.delete_item);
        share_item = menu.findItem(R.id.share_item);
        // then call the method to execute and set them
        processMenu(null);

        return true;
    }

    /*
        this method is a connect between xml(clickable).
        The parameter of this kind of function is View
     */
    public void aboutApp(View view){
        /*
            Intents are asynchronous messages which allow application components to
            request functionality from other Android component
         */
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /*
        method for the onClick items
        each item will do their corresponding functions
     */
    public void menuItems(MenuItem menuItem){
        int itemID = menuItem.getItemId();
        switch(itemID){
            case R.id.search_item:
                break;
            case R.id.add_item:

                Intent intent = new Intent("android.intent.action.ADD_ITEM");
                // this 0 passed back means to add a new item
                startActivityForResult(intent, 0);
                break;
            case R.id.delete_item:
                // if no items are selected, then
                if(selectedCount == 0)
                    break;

                // create the dialog for asking to double check if want to delete the item
                AlertDialog.Builder askDelete = new AlertDialog.Builder(this);
                String askMessage = getString(R.string.delete_item);
                // set the display content of the alertDialog
                askDelete.setTitle(R.string.delete)
                        .setMessage(String.format(askMessage, selectedCount));
                // set the ok button, if yes, then have to delete the items
                askDelete.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // the indexes of item to be deleted
                                int indexes = itemAdapter.getCount() - 1;
                                // swipe through all the items, if selected to be delete
                                // then delete it
                                while (indexes > -1){
                                    Item toBeDelete = itemAdapter.getItem(indexes);

                                    if(toBeDelete.getSelected()){
                                        itemAdapter.remove(toBeDelete);
                                        // get the to be deleted item's id
                                        itemDB.delete(toBeDelete.getId());
                                    }
                                    indexes--;
                                }
                                // every time you change the data, need to inform
                                itemAdapter.notifyDataSetChanged();
                                // have to reset the selectedCount for next time use
                                //selectedCount = 0;
                                //processMenu(null);
                            }
                        });
                // no button: nothing to do
                askDelete.setNegativeButton(android.R.string.no, null);
                askDelete.show();
                break;
            case R.id.revert_item:
                for(int i=0;i < itemAdapter.getCount(); i++){
                    Item revertItem = itemAdapter.getItem(i);
                    // if the notes are selected
                    if (revertItem.getSelected()){
                        revertItem.setSelected(false);
                        itemAdapter.setItemIndex(i, revertItem);
                    }
                }
                // every finish setting the items selected, have to reset your choice
                selectedCount = 0;
                // process the menu again because you have updated it
                processMenu(null);

                break;
            case R.id.share_google:
                break;
            case R.id.share_facebook:
                break;
        }

    }

    public void clickPreferences(MenuItem menuItem){
        startActivity(new Intent(this, PrefActivity.class));
    }

}
