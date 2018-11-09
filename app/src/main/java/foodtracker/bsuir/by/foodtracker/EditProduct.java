package foodtracker.bsuir.by.foodtracker;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditProduct extends AppCompatActivity {

    private EditText mEditTextCurrentAmount;
    private TextView mTextViewName;
    private TextView mTextViewStartAmount;
    private TextView mTextViewLeft;
    private ImageView mImageViewPhoto;
    private Button mOkButton;

    private int id;
    private DBProduct db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        id = Integer.parseInt(getIntent().getStringExtra(DBProduct.ID));
        db = new DBProduct(this);

        mEditTextCurrentAmount = findViewById(R.id.current_product_edit);
        mTextViewName = findViewById(R.id.name_product_edit);
        mTextViewStartAmount = findViewById(R.id.start_product_edit);
        mTextViewLeft = findViewById(R.id.left_product_edit);
        mOkButton = findViewById(R.id.ok_product_edit_button);

        fillActivity();
        setupOkButton();
    }

    private void fillActivity() {
        SQLiteDatabase database = db.getWritableDatabase();
        String query = String.format(
                "select PR.%s, PR.%s, AM.%s, AM.%s, PR.%s from %s as PR inner join %s as AM on PR.%s = AM.%s where PR.%s = '%s'",
                DBProduct.NAME, DBProduct.EXPIRATION_DATE, DBProduct.CURRENT_AMOUNT, DBProduct.AMOUNT, DBProduct.PHOTO_PATH,
                DBProduct.TABLE_PRODUCTS, DBProduct.TABLE_PRODUCTS_AMOUNT,
                DBProduct.ID, DBProduct.ID,
                DBProduct.ID, id);
        Cursor cursor = database.rawQuery(query, null);
        if(cursor != null)
            if(cursor.moveToFirst()) {
                String [] values = cursor.getColumnNames();
                mTextViewName.setText(cursor.getString(cursor.getColumnIndex(values[0])));
                mTextViewLeft.setText(cursor.getString(cursor.getColumnIndex(values[1])));
                mEditTextCurrentAmount.setText(cursor.getString(cursor.getColumnIndex(values[2])));
                mTextViewStartAmount.setText(cursor.getString(cursor.getColumnIndex(values[3])));
                //cursor.getString(cursor.getColumnIndex(values[4]));
            }
        cursor.close();
    }

    private void setupOkButton() {
        mOkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SQLiteDatabase database = db.getWritableDatabase();
                int amount = Integer.parseInt(mEditTextCurrentAmount.getText().toString());
                if(amount == 0) db.deleteProduct(database, id);
                else db.updateProduct(database, id, amount);
                finish();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

