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

public class Shelf extends Fragment {

    private ListView mListView;
    private Button mAddButton;

    private ArrayList<Product> productList;
    private ProductAdapter productAdapter;
    private DBProduct db;

    public static final String SHELF = "2";
    private static final String SELECTED = "Выбрано";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tab_shelf, container, false);

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, getContext());

        mListView = rootView.findViewById(R.id.list_shelf);
        mAddButton = rootView.findViewById(R.id.add_product_from_shelf_button);

        setupShortListViewClick();
        setupLongListViewClick();
        setupAddButton();

        return rootView;
    }

    private void setupShortListViewClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), EditProduct.class);
                intent.putExtra(DBProduct.ID, String.valueOf(productList.get(position).getId()));
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
                productAdapter.toggleSelection(position);
                mListView.getChildAt(position).setBackgroundColor(checked ? Color.parseColor("#00a577") : Color.WHITE);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_toolbar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                menu.findItem(R.id.delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                menu.findItem(R.id.add).setVisible(false);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        SparseBooleanArray selected = productAdapter.getSelectedIds();
                        SQLiteDatabase database = db.getWritableDatabase();
                        for(int i = selected.size() - 1; i >= 0; i--) {
                            if(selected.valueAt(i)) {
                                Product selectedProduct = productAdapter.getItem(selected.keyAt(i));
                                db.deleteProduct(database, selectedProduct.getId());
                                productAdapter.remove(selectedProduct);
                            }
                        }
                        mode.finish();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                productAdapter.removeSelection();
                for(int i = 0; i < productList.size(); i++)
                    mListView.getChildAt(i).setBackgroundColor(Color.WHITE);
            }
        });
    }

    private void setupAddButton() {
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddProductTo.class);
                intent.putExtra(DBProduct.PLACE, SHELF);
                startActivity(intent);
            }
        });
    }

    private void updateProducts() {
        SQLiteDatabase database = db.getWritableDatabase();
        String query = String.format(
                "select PR.%s, PR.%s, PR.%s, AM.%s, AM.%s, PR.%s from %s as PR inner join %s as AM on PR.%s = AM.%s where PR.%s = '%s'",
                DBProduct.ID, DBProduct.NAME, DBProduct.EXPIRATION_DATE, DBProduct.CURRENT_AMOUNT, DBProduct.AMOUNT, DBProduct.PHOTO_PATH,
                DBProduct.TABLE_PRODUCTS, DBProduct.TABLE_PRODUCTS_AMOUNT,
                DBProduct.ID, DBProduct.ID,
                DBProduct.PLACE, SHELF);
        Cursor cursor = database.rawQuery(query, null);
        getProducts(cursor);
        cursor.close();
    }

    private void getProducts(Cursor cursor) {
        if(cursor != null)
            if(cursor.moveToFirst()) {
                String [] values = cursor.getColumnNames();
                do {
                    productList.add(new Product(
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex(values[0]))),
                            cursor.getString(cursor.getColumnIndex(values[1])),
                            cursor.getString(cursor.getColumnIndex(values[2])),
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex(values[3]))),
                            Integer.parseInt(cursor.getString(cursor.getColumnIndex(values[4]))),
                            cursor.getString(cursor.getColumnIndex(values[5])))
                    );
                } while(cursor.moveToNext());
                mListView.setAdapter(productAdapter);
            }
            else return;
    }

    @Override
    public void onStart() {
        super.onStart();
        db = new DBProduct(getActivity());
        updateProducts();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        db.close();
        productList.clear();
        productAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
