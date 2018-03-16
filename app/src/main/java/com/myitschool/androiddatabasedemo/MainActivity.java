package com.myitschool.androiddatabasedemo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 0x000000;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_contacts);

        mDBHelper = new DatabaseHelper(this);

        // обновление базы данных
        try {
            mDBHelper.updateDataBase();
        } catch (IOException e) {
            throw new RuntimeException("Невозможно загрузить базу данных", e);
        }

        mDb = mDBHelper.getWritableDatabase();


        ContactsRepository data = new DBContactsRepository(this);

        List<Person> persons = data.getContacts();



        //определяем поля макета, лэйаута, в которые будут выводиться значения столбцов
        int[] fields = new int[]{R.id.item_id, R.id.item_name, R.id.item_phone};

        // заголовки столбцов результирующей таблицы-выборки из базы данных
        String[] headers = new String[]{"_id", "fullname", "phone"};

        // сделаем выборку данных из базы данных, указав необходимые столбцы и таблицу
        // последний параметр null означает, что мы не делаем фильтрацию, выбирам все строки таблицы БД.
        Cursor c = mDb.rawQuery("SELECT id AS _id, fullname, phone FROM person", null);

        // на базе курсора создаем адаптер, используя все заготовленные параметры и данные.
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.contacts_item, c,
                headers, fields, SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        //Находим компоненту списка
        ListView lvContacts = (ListView) findViewById(R.id.lvcontacts);

        //Применяем к ней адаптер
        lvContacts.setAdapter(new MyPersonsAdapter);

        //добавляем обработку нажатия на элемент из списка контактов
        lvContacts.setOnItemClickListener(this);
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Закрываем соединение с базой данных
        mDBHelper.close();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String phoneNumber = ((TextView)view.findViewById(R.id.item_phone)).getText().toString();
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);

            // MY_PERMISSIONS_REQUEST_CALL_PHONE это определенная константа
            // callback-метод onRequestPermissionResult обрабатывает результаты выбора ползователя
        } else {
            //Уже есть разрешение
            try {
                startActivity(intent); //звоним
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }
        //startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // Если запрос отменен, то результирующие массивы пустые
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // разрешение получено, можно звонить
                } else {
                    // разрешение не получено
                    // отключаем соответствующую функциональность
                }
            }
        }
    }

}
