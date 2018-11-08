package foodtracker.bsuir.by.foodtracker;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ItemAdapter extends ArrayAdapter<Item> {

    private SparseBooleanArray mSelectedItemsIds;
    private ArrayList<Item> itemList;
    private Context context;

    private static final Map<String, String> placeMap = new HashMap<String, String>(){
        {
            put(Fridge.FRIDGE, "FRIDGE");
            put(Shelf.SHELF, "SHELF");
        }
    };

    public ItemAdapter(ArrayList<Item> itemList, Context context) {
        super(context, R.layout.item, itemList);
        this.itemList = itemList;
        this.context = context;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    private class ViewHolder {
        TextView name;
        TextView place;
        TextView comment;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ItemAdapter.ViewHolder holder;
        if(view == null) {
            holder = new ItemAdapter.ViewHolder();
            view = View.inflate(context, R.layout.item, null);
            holder.name = view.findViewById(R.id.list_item_name);
            holder.place = view.findViewById(R.id.list_item_place);
            holder.comment = view.findViewById(R.id.list_item_comment);
            view.setTag(holder);
        }
        else holder = (ItemAdapter.ViewHolder) view.getTag();

        holder.name.setText(String.valueOf(itemList.get(position).getName()));
        holder.place.setText(placeMap.get(String.valueOf(itemList.get(position).getPlace())));
        holder.comment.setText(String.valueOf(itemList.get(position).getComment()));

        return view;
    }

    public ArrayList<Item> getProductList() {

        return itemList;
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {

        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {

        return mSelectedItemsIds;
    }

    @Override
    public void remove(Item object) {
        itemList.remove(object);
        notifyDataSetChanged();
    }

}
