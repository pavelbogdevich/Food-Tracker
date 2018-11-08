package foodtracker.bsuir.by.foodtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBProduct extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "products_db";
    public static final String TABLE_PRODUCTS = "products";
    public static final String TABLE_PRODUCTS_AMOUNT = "products_amount";

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String PLACE = "place";
    public static final String EXPIRATION_DATE = "expiration_date";
    public static final String PHOTO_PATH = "photo_path";
    public static final String CURRENT_AMOUNT = "current_amount";
    public static final String AMOUNT = "amount";

    public DBProduct(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_PRODUCTS + "( " +
                ID + " integer primary key, " +
                NAME + " text, " +
                PLACE + " integer, " +
                EXPIRATION_DATE + " text, " +
                PHOTO_PATH + " text ) ");
        db.execSQL(
                "create table " + TABLE_PRODUCTS_AMOUNT + "( " +
                ID + " integer, " +
                NAME + " text, " +
                CURRENT_AMOUNT + " integer, " +
                AMOUNT + " integer, " +
                " foreign key (" + ID + ") references " + TABLE_PRODUCTS + " (" + ID + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteProduct(SQLiteDatabase database, int id) {
        database.delete(TABLE_PRODUCTS, ID + "= ?", new String[] {String.valueOf(id)});
        database.delete(TABLE_PRODUCTS_AMOUNT, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void updateProduct(SQLiteDatabase database, int id, int newCurrentAmount) {
        ContentValues values = new ContentValues();
        values.put(DBProduct.CURRENT_AMOUNT, newCurrentAmount);
        database.update(TABLE_PRODUCTS_AMOUNT, values, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void insertProductToProducts(SQLiteDatabase database, String name, String place, String expiration_date) {
        ContentValues values = new ContentValues();
        String query = String.format("select %s from %s", DBProduct.ID, DBProduct.TABLE_PRODUCTS);
        Cursor cursor = database.rawQuery(query, null);
        int id = cursor != null ? cursor.getCount() + 1: 0;

        values.put(DBProduct.ID, id);
        values.put(DBProduct.NAME, name);
        values.put(DBProduct.PLACE, place);
        values.put(DBProduct.EXPIRATION_DATE, expiration_date);

        database.insert(DBProduct.TABLE_PRODUCTS, null, values);
        cursor.close();
    }

    public void insertProductToProductsAmount(SQLiteDatabase database, String name, int amount) {
        ContentValues values = new ContentValues();
        String query = String.format("select %s from %s", DBProduct.ID, DBProduct.TABLE_PRODUCTS_AMOUNT);
        Cursor cursor = database.rawQuery(query, null);
        int id = cursor != null ? cursor.getCount() + 1: 0;

        values.put(DBProduct.ID, id);
        values.put(DBProduct.NAME, name);
        values.put(DBProduct.CURRENT_AMOUNT, amount);
        values.put(DBProduct.AMOUNT, amount);

        database.insert(DBProduct.TABLE_PRODUCTS_AMOUNT, null, values);
        cursor.close();
    }

}
