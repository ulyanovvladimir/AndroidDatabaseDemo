package com.myitschool.androiddatabasedemo;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static String DB_NAME = "helper.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 4;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        Log.d(TAG, "DB_PATH="+DB_PATH);
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    /**
     * Удаляет существующий файл базы данных и заменяет на новый файл из assets в случае если
     * установлен флаг mNeedUpdate=true. По завершении сбрасывает флаг в false
     *
     * @throws IOException при невозможности выполнения оперкации с файлами
     */
    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    /**
     * Проверяет наличие базыданных с указанным именем в константе DB_NAME
     * @return в случае, если файл базы существует, возвращает true
     */
    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    /**
     * Копирует базу данных из assets при отсутствии файла база данных
     */
    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase", mIOException);
            }
        }
    }


    /**
     * Копирует с заменой файл базы данных из папки Assets
     *
     * @throws IOException
     */
    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        File target = new File(DB_PATH + DB_NAME);
        OutputStream mOutput = new FileOutputStream(target);
        try {
            byte[] mBuffer = new byte[1024];
            int mLength;
            while ((mLength = mInput.read(mBuffer)) > 0)
                mOutput.write(mBuffer, 0, mLength);
        } finally {
            mOutput.flush();
            mOutput.close();
            mInput.close();
        }
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    /**
     * Закрывает соединение с базой данных.
     */
    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    /**
     * Выполняется при создании базы данных.
     * Может использоваться для вызова SQL-запросов к базе данных при установке приложения.
     * Обычно создает БД средствами команд CREATE TABLE и INSERT
     * @param db база данных, созданная Android для данного приложения.
     */

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /**
     * Механизм обновления базы данных при обновлении приложения
     *
     * @param db база данных
     * @param oldVersion номер текущей версии  приложения (старой)
     * @param newVersion номер версии приложения до которого нужно обновиться (новой)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // В случае обновления новой версии приложения, вся база данных уничтожается и заменяется на новую копию из Assets
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }
}