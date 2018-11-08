package foodtracker.bsuir.by.foodtracker;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

public class AddItemTo extends AppCompatActivity {

    private EditText mEditTextName;
    private RadioGroup mRadioGroup;
    private EditText mEditTextComment;
    private Button mAddItemButton;

    private int id;
    private String name;
    private String comment;
    private DBItem db;

    private static final String CANCEL = "Отмена";
    private static final String ERROR = "Ошибка добавления";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_to);

        db = new DBItem(this);

        mEditTextName = findViewById(R.id.add_item_to_db_name);
        mRadioGroup = findViewById(R.id.radioGroup);
        mEditTextComment = findViewById(R.id.add_item_to_db_comment);
        mAddItemButton = findViewById(R.id.add_item_to_db_button);
        mAddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = mEditTextName.getText().toString();
                comment = mEditTextComment.getText().toString();
                if(!name.isEmpty() && mRadioGroup.getCheckedRadioButtonId() != -1) {
                    SQLiteDatabase database = db.getWritableDatabase();
                    String place = new String();
                    switch (mRadioGroup.getCheckedRadioButtonId()) {
                        case R.id.fridge_radio_button:  place = Fridge.FRIDGE; break;
                        case R.id.shelf_radio_button: place = Shelf.SHELF; break;
                    }
                    db.insertItem(database, name, place, comment);
                    finish();
                }
                else imageError();
            }
        });

    }

    private void imageError() {
        final CharSequence[] options = {CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddItemTo.this);
        builder.setTitle(ERROR);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals(CANCEL)) dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("Add item on start");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Add item on resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Add item on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Add item on stop");
        db.close();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Add item on destroy");
    }
}
