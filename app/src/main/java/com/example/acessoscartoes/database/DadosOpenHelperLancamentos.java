package com.example.acessoscartoes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DadosOpenHelperLancamentos extends SQLiteOpenHelper {
    public DadosOpenHelperLancamentos(@Nullable Context context) {
        super(context, "banco_lanc.db", null, 3);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ScriptDLL.getCreateTableLancamentos());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
