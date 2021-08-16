package com.example.acessoscartoes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DadosOpenHelperConfig extends SQLiteOpenHelper {
    public DadosOpenHelperConfig(@Nullable Context context) {
        super(context, "CONFIG", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL( ScriptDLL.getCreateTableConfig());

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
