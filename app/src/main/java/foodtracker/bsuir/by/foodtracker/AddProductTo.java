package foodtracker.bsuir.by.foodtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AddProductTo extends AppCompatActivity {

    private EditText mEditTextName;
    private EditText mEditTextDate;
    private Calendar mExpirationDate;
    private EditText mEditTextAmount;
    private Button mAddButton;
    private ImageView mPhotoButton;

    private int id;

    private int year;
    private int month;
    private int date;
    private int amount;
    private String name;
    private String expiration_date;
    private String address;
    private String place;
    private DBProduct db;

    private static final int CAMERA_REQUEST = 0;
    private static final int SElECT_FILE = 1;
    private static final int QUALITY = 90;
    private static final String TITLE = "Добавить фото";
    private static final String TAKE_PHOTO = "Камера";
    private static final String CHOOSE_FROM_GALLERY = "Галерея";
    private static final String CANCEL = "Отмена";
    private static final String ERROR = "Ошибка добавления";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Add  product on create");
        setContentView(R.layout.activity_add_product_to);

        String value = getIntent().getStringExtra(DBProduct.PLACE);
        if(value.length() == 1) place = value;
        else {
            place = String.valueOf(value.charAt(0));
            name = value.substring(1);
        }

        mEditTextName = findViewById(R.id.add_product_to_db_name);
        mEditTextName.setText(name);
        mEditTextAmount = findViewById(R.id.add_product_to_db_amount);
        db = new DBProduct(this);

        setupExpirationDate();
        setupAddButton();
        setupPhotoButton();
    }

    private void setupExpirationDate() {
        mEditTextDate = findViewById(R.id.add_product_to_db_calendar);
        mEditTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpirationDate = Calendar.getInstance();
                year = mExpirationDate.get(Calendar.YEAR);
                month = mExpirationDate.get(Calendar.MONTH);
                date = mExpirationDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePickerDialog = new DatePickerDialog(AddProductTo.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mEditTextDate.setText(dayOfMonth + "-" + month + "-" + year);
                        mExpirationDate.set(year, month, dayOfMonth);
                    }
                }, year, month, date);
                mDatePickerDialog.show();
            }
        });
    }

    private void setupAddButton() {
        mAddButton = findViewById(R.id.add_product_to_db_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = mEditTextName.getText().toString();
                amount = Integer.parseInt(mEditTextAmount.getText().toString());
                expiration_date = mEditTextDate.getText().toString();
                if(!expiration_date.isEmpty() && amount != 0 && !name.isEmpty()) {
                    //String path = new String(address.isEmpty() ? "" : ", " + DBProduct.PHOTO_PATH);
                    SQLiteDatabase database = db.getWritableDatabase();
                    db.insertProductToProducts(database, name, place, expiration_date);
                    db.insertProductToProductsAmount(database, name, amount);
                    finish();
                }
                else imageError();
            }
        });
    }

    private void setupPhotoButton() {
        mPhotoButton = findViewById(R.id.photo_button);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {TAKE_PHOTO, CHOOSE_FROM_GALLERY, CANCEL};
                AlertDialog.Builder builder = new AlertDialog.Builder(AddProductTo.this);
                builder.setTitle(TITLE);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (options[which].equals(TAKE_PHOTO)) setupCameraIntent();
                        else if (options[which].equals(CHOOSE_FROM_GALLERY)) setupGalleryIntent();
                        else if (options[which].equals(CANCEL)) dialog.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void setupCameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    private void setupGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, CHOOSE_FROM_GALLERY), SElECT_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(resultCode == Activity.RESULT_OK)
                switch (requestCode) {
                    case CAMERA_REQUEST: onCaptureImageResult(data); break;
                    case SElECT_FILE: onSelectFromGalleryResult(data); break;
                }
    }


    private void onCaptureImageResult(Intent data) {
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream file;
        try {
            destination.createNewFile();
            file = new FileOutputStream(destination);
            file.write(bytes.toByteArray());
            file.close();
        } catch (IOException e) {
            imageError();
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        if(data != null)
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                        getApplicationContext().getContentResolver(), data.getData());
                mPhotoButton.setImageBitmap(bitmap);
            } catch (IOException e) {
                imageError();
            }
    }

    private void imageError() {
        final CharSequence[] options = {CANCEL};
        AlertDialog.Builder builder = new AlertDialog.Builder(AddProductTo.this);
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
        System.out.println("Add product on start");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("Add  product on resume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("Add  product on pause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("Add  product on stop");
        db.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Add  product on destroy");
    }

}
