package foodtracker.bsuir.by.foodtracker;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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

public class Fridge extends Fragment{

    private ListView mListView;
    private Button mAddButton;

    private ArrayList<Product> mProductList;
    private ProductAdapter mProductAdapter;
    private DBProduct db;

    public static final String FRIDGE = "1";
    private static final String SELECTED = "Выбрано";

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_fridge, container, false);
        System.out.println("Fridge on create");

        mProductList = new ArrayList<>();
        mProductAdapter = new ProductAdapter(mProductList, getContext());

        mListView = rootView.findViewById(R.id.list_fridge);
        setupShortListViewClick();
        setupLongListViewClick();

        mAddButton = rootView.findViewById(R.id.add_product_from_fridge_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddProductTo.class);
                intent.putExtra(DBProduct.PLACE, FRIDGE);
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void setupShortListViewClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(position + " " + id);
                Intent intent = new Intent(getContext(), EditProduct.class);
                intent.putExtra(DBProduct.ID, String.valueOf(mProductList.get(position).getId()));
                startActivity(intent);
            }
        });
    }

    private void setupLongListViewClick() {
        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedCount = mListView.getCheckedItemCount();
                mode.setTitle(checkedCount + " " + SELECTED);
                mProductAdapter.toggleSelection(position);
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
                menu.findItem(R.id.add).setVisible(false);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                System.out.println("clicked");
                switch (item.getItemId()) {
                    case R.id.delete:
                        SparseBooleanArray selected = mProductAdapter.getSelectedIds();
                        SQLiteDatabase database = db.getWritableDatabase();
                        for(int i = selected.size() - 1; i >= 0; i--) {
                            if(selected.valueAt(i)) {
                                Product selectedProduct = mProductAdapter.getItem(selected.keyAt(i));
                                db.deleteProduct(database, selectedProduct.getId());
                                mProductAdapter.remove(selectedProduct);
                            }
                        }
                        mode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                System.out.println("destroy");
                mProductAdapter.removeSelection();
                for(int i = 0; i < mProductList.size(); i++)
                    mListView.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
        });
    }

    private void updateProducts() {
        System.out.println("In update");
        SQLiteDatabase database = db.getWritableDatabase();
        String query = String.format(
                "select PR.%s, PR.%s, PR.%s, AM.%s, AM.%s, PR.%s from %s as PR inner join %s as AM on PR.%s = AM.%s where PR.%s = '%s'",
                DBProduct.ID, DBProduct.NAME, DBProduct.EXPIRATION_DATE, DBProduct.CURRENT_AMOUNT, DBProduct.AMOUNT, DBProduct.PHOTO_PATH,
                DBProduct.TABLE_PRODUCTS, DBProduct.TABLE_PRODUCTS_AMOUNT,
                DBProduct.ID, DBProduct.ID,
                DBProduct.PLACE, FRIDGE);
        Cursor cursor = database.rawQuery(query, null);
        getProducts(cursor);
        cursor.close();
    }

    private void getProducts(Cursor cursor) {
        if(cursor != null)
            if(cursor.moveToFirst()) {
                String [] values = cursor.getColumnNames();
                do {
                    mProductList.add(new Product(
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex(values[0]))),
                            cursor.getString(cursor.getColumnIndex(values[1])),
                            cursor.getString(cursor.getColumnIndex(values[2])),
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex(values[3]))),
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex(values[4]))),
                            cursor.getString(cursor.getColumnIndex(values[5])))
                    );
                } while(cursor.moveToNext());
                mListView.setAdapter(mProductAdapter);
            }
            else return;
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("Fridge on start");
        db = new DBProduct(getActivity());
        updateProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Fridge on resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Fridge on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Fridge on stop");
        db.close();
        mProductList.clear();
        mProductAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("Fridge on destroy view");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Fridge on destroy");
    }
}
