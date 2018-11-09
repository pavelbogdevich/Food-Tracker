package foodtracker.bsuir.by.foodtracker;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


public class ProductAdapter extends ArrayAdapter<Product> {

    private SparseBooleanArray selectedItemsIds;
    private ArrayList<Product> productList;
    private Context context;

    public ProductAdapter(ArrayList<Product> productList, Context context) {
        super(context, R.layout.product, productList);
        this.productList = productList;
        this.context = context;
        selectedItemsIds = new SparseBooleanArray();
    }

    private class ViewHolder {
        TextView name;
        TextView expirationDate;
        TextView amount;
        //ImageView photo;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if(view == null) {
            holder = new ViewHolder();
            view = View.inflate(context, R.layout.product, null);

            holder.name = view.findViewById(R.id.list_product_name);
            holder.expirationDate = view.findViewById(R.id.list_product_expiation_date);
            holder.amount = view.findViewById(R.id.list_product_amount);
            //holder.photo = view.findViewById(R.id.product_photo);

            view.setTag(holder);
        }
        else holder = (ViewHolder) view.getTag();

        holder.name.setText(String.valueOf(productList.get(position).getName()));
        holder.expirationDate.setText(String.valueOf(productList.get(position).getExpirationDate()));
        holder.amount.setText(String.valueOf(productList.get(position).getCurrentAmount() + " / " + productList.get(position).getAmount()));

        return view;
    }

    @Override
    public void remove(Product object) {
        productList.remove(object);
        notifyDataSetChanged();
    }

    public ArrayList<Product> getProductList() {
        return productList;
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedItemsIds.get(position));
    }

    public void removeSelection() {
        selectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) selectedItemsIds.put(position, value);
        else selectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedItemsIds;
    }

}
