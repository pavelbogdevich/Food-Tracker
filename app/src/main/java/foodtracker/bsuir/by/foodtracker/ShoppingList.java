package foodtracker.bsuir.by.foodtracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShoppingList extends Fragment {

    private ListView mListView;
    private Button mAddButton;

    private ArrayList<Item> mItemList;
    private ItemAdapter mItemAdapter;
    private DBItem db;

    private static final String SELECTED = "Выбрано";
    private static final boolean isAdd = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_shopping_list, container, false);
        System.out.println("Shopping list on create");

        mItemList = new ArrayList<>();
        mItemAdapter = new ItemAdapter(mItemList, getContext());

        mListView = rootView.findViewById(R.id.list_item);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedCount = mListView.getCheckedItemCount();
                mode.setTitle(checkedCount + " " + SELECTED);
                mItemAdapter.toggleSelection(position);
                mListView.getChildAt(position).setBackgroundColor(checked ? Color.parseColor("#00a577") : Color.WHITE);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                System.out.println("create");
                mode.getMenuInflater().inflate(R.menu.menu_toolbar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                System.out.println("prepare");
                menu.findItem(R.id.delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                menu.findItem(R.id.add).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                System.out.println("clicked");
                switch (item.getItemId()) {
                    case R.id.delete: reformItems(!isAdd); mode.finish(); break;
                    case R.id.add: reformItems(isAdd); mode.finish(); break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                System.out.println("destroy");
                mItemAdapter.removeSelection();
                for(int i = 0; i < mItemList.size(); i++)
                    mListView.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
        });

        mAddButton = rootView.findViewById(R.id.add_item_from_shopping_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddItemTo.class);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void reformItems(boolean flag) {
        SQLiteDatabase database = db.getWritableDatabase();
        SparseBooleanArray selected = mItemAdapter.getSelectedIds();
        for(int i = selected.size() - 1; i >= 0; i--) {
            if(selected.valueAt(i)) {
                Item selectedItem = mItemAdapter.getItem(selected.keyAt(i));
                if(flag) addItemTo(database, selectedItem.getId(), selectedItem.getName());
                db.deleteItem(database, selectedItem.getId());
                mItemAdapter.remove(selectedItem);
            }
        }
    }

    private void addItemTo(SQLiteDatabase database, int id, String name) {
        String query = String.format("select %s from %s where %s = %s", DBItem.PLACE, DBItem.TABLE_ITEMS, DBItem.ID, id);
        Cursor cursor = database.rawQuery(query, null);
        if(cursor != null)
            if(cursor.moveToFirst()) {
                String [] values = cursor.getColumnNames();
                String place = cursor.getString(cursor.getColumnIndex(values[0]));
                Intent intent = new Intent(getContext(), AddProductTo.class);
                intent.putExtra(DBProduct.PLACE, place + name);
                startActivity(intent);
            }
    }

    private  void updateItems() {
        System.out.println("update in shop list");
        SQLiteDatabase database = db.getWritableDatabase();
        String query = String.format("select * from %s", DBItem.TABLE_ITEMS);
        Cursor cursor = database.rawQuery(query, null);
        getItems(cursor);
        cursor.close();
    }

    private void getItems(Cursor cursor) {
        System.out.println("get item in shop list");
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                String [] values = cursor.getColumnNames();
                do {
                    mItemList.add(new Item(
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex(values[0]))),
                            cursor.getString(cursor.getColumnIndex(values[1])),
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex(values[2]))),
                            cursor.getString(cursor.getColumnIndex(values[3]))));
                } while(cursor.moveToNext());
                mListView.setAdapter(mItemAdapter);
            }
        }
        else return;
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("Shopping list on start");
        db = new DBItem(getActivity());
        updateItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Shopping list on resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Shopping list on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Shopping list on stop");
        db.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Shopping list on destroy view");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Shopping list on destroy");
    }

}
