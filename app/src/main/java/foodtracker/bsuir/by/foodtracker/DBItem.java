package foodtracker.bsuir.by.foodtracker;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBItem extends SQLiteOpenHelper {

    private Context context;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "items_db";
    public static final String TABLE_ITEMS = "items";
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String PLACE = "place";
    public static final String COMMENT = "comment";

    public DBItem(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_ITEMS + "( " +
                ID + " integer primary key, " +
                NAME + " text, " +
                PLACE + " integer, " +
                COMMENT + " text ) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void deleteItem(SQLiteDatabase database, int id) {
        database.delete(TABLE_ITEMS, ID + "= ?", new String[] {String.valueOf(id)});
    }

    public void insertItem(SQLiteDatabase database, String name, String place, String comment) {
        ContentValues values = new ContentValues();
        String query = String.format("select %s from %s", ID, TABLE_ITEMS);
        Cursor cursor = database.rawQuery(query, null);
        int id = cursor != null ? cursor.getCount() + 1: 0;

        values.put(DBItem.ID, id);
        values.put(DBItem.NAME, name);
        values.put(DBItem.PLACE, place);
        values.put(DBItem.COMMENT, comment);

        database.insert(DBItem.TABLE_ITEMS, null, values);
        cursor.close();
    }

}
