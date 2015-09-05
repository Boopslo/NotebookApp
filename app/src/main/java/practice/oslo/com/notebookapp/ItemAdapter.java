package practice.oslo.com.notebookapp;


import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Oslo on 6/11/15.
 *
 * An Adapter Class for displaying every item in the ListView
 * Create a new adapter for item use
 *
 */
public class ItemAdapter extends ArrayAdapter<Item>{

    // the numbers for the screen source
    private int resource;
    // the list items
    private List<Item> items;

    public ItemAdapter(Context context, int resource, List<Item> items) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LinearLayout itemView;
        // read the current position of the note object
        final Item item = getItem(position);
        // if the current view is empty, have to create a new view
        if (convertView == null) {
            // create a new layout
            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, itemView, true);

        } else {
            itemView = (LinearLayout)convertView;
        }

        // read the note's color, selected, the title, and the dateTime component
        RelativeLayout typeColor = (RelativeLayout)itemView.findViewById(R.id.type_color);
        ImageView selectedItem = (ImageView)itemView.findViewById(R.id.selected_item);
        TextView titleView = (TextView)itemView.findViewById(R.id.single_item_title);
        TextView dateView = (TextView)itemView.findViewById(R.id.single_item_date);

        // set a single note's background color
        GradientDrawable background = (GradientDrawable)typeColor.getBackground();
        background.setColor(item.getColor().parseColorCode());

        // set a single note's title and dateTime
        titleView.setText(item.getTitle());
        dateView.setText(item.getLocaleDateTime());

        // set if a single note is selected
        selectedItem.setVisibility(item.getSelected()? View.VISIBLE : View.INVISIBLE);

        return itemView;
    }

    // set the indexed Item's data
    public void setItemIndex(int index, Item item){
        if( (index >= 0) && (index < items.size()) ){
            items.set(index, item);
            notifyDataSetChanged();
        }
    }

    // get the indexed Item's data
    public Item getItemIndex(int index){
        return items.get(index);
    }


}
