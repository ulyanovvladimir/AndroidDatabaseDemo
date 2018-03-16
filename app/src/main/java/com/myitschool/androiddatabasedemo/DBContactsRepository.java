package com.myitschool.androiddatabasedemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by teacher on 16.03.18.
 */

public class DBContactsRepository implements ContactsRepository{

    private final Context context;
    private final SQLiteDatabase db;

    public DBContactsRepository(Context context) {
        this.context = context;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try {
            dbHelper.updateDataBase();
        } catch (IOException e) {
            throw new RuntimeException("Database connection problem", e);
        }
        this.db = dbHelper.getWritableDatabase();
    }


    @Override
    public List<Person> getContacts() {
        List<Person> ret = new ArrayList<>();


        String[] columns = new String[]{"id", "fullname", "phone"};
        Cursor c = db.query("person", columns, null, null, null, null,null);

        if (c.moveToFirst()){
            // обрабатываем запрос
            //считывание строки
            int idIdx = c.getColumnIndex("id");
            int fnIdx = c.getColumnIndex("fullname");
            int phoneIdx = c.getColumnIndex("phone");
            do {
                // стоим на строке
                int id = c.getInt(idIdx);
                String name = c.getString(fnIdx);
                String tel = c.getString(phoneIdx);
                Person p = new Person(id, name, tel);
                ret.add(p);
            } while (c.moveToNext());
        };

        return ret;
    }

    @Override
    public Person getPerson(int id) {
        List<Person> ret = new ArrayList<>();


        String[] columns = new String[]{"id", "fullname", "phone"};
        Cursor c = db.rawQuery("SELECT id, fullname, phone FROM person WHERE id=?", new String[]{String.valueOf(id)});

        if (c.moveToFirst()){
            // обрабатываем запрос
            //считывание строки
            int idIdx = c.getColumnIndex("id");
            int fnIdx = c.getColumnIndex("fullname");
            int phoneIdx = c.getColumnIndex("phone");

            // стоим на строке
            int personId = c.getInt(idIdx);
            String name = c.getString(fnIdx);
            String tel = c.getString(phoneIdx);
            return new Person(personId, name, tel);

        };
        return null;
    }

    @Override
    public Person insert(Person p) {
        ContentValues cv = new ContentValues();
        //cv.put("id", p.getId());
        cv.put("fullname", p.getFullname());
        cv.put("phone", p.getPhone());
        long id = db.insert("person",null, cv);
        p.setId(id);
        return p;
    }

    @Override
    public void update(Person p) {
        ContentValues cv = new ContentValues();
        cv.put("fullname", p.getFullname());
        cv.put("phone", p.getPhone());
        String[] whereArgs = {String.valueOf(p.getId())};
        db.update("person", cv, "id=?", whereArgs);
    }

    @Override
    public void delete(Person p) {
        String[] whereArgs = {String.valueOf(p.getId())};
        db.delete("person","id=?", whereArgs);
    }
}
