package com.lianren.android.util.greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.lianren.android.basicData.db.BasicBeanDao;
import com.lianren.android.basicData.db.DaoMaster;

public class MySqliteOpenHelper extends DaoMaster.OpenHelper {
    public MySqliteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        MigrationHelper.migrate(db, BasicBeanDao.class);
    }
}
