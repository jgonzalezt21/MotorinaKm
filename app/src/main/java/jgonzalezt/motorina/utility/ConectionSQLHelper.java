package jgonzalezt.motorina.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConectionSQLHelper extends SQLiteOpenHelper {

    public ConectionSQLHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Utility.CREATE_TABLE_EQUIPO);
        db.execSQL(Utility.CREATE_TABLE_LECTURAS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(Utility.DROP_TABLE_EQUIPO);
        db.execSQL(Utility.DROP_TABLE_LECTURAS);
        onCreate(db);
    }
}
