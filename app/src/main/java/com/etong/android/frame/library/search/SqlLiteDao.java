package com.etong.android.frame.library.search;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.etong.android.frame.utils.logger.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouxiqing on 2016/10/12.
 */
public class SqlLiteDao {
    private static SqlLiteDao instance;

    private static SQLiteOpenHelper dbHandler;

    private Boolean DBLock = false;

    private String sql;

    public static SqlLiteDao getInstance(Context context) {
        if (instance == null) {
            instance = new SqlLiteDao(context);
        }
        return instance;
    }

    private SqlLiteDao(Context context) {
        dbHandler = new SQLiteOpenHelper(context.getApplicationContext(), "db_data", null, 1) {

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion,
                                  int newVersion) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onCreate(SQLiteDatabase db) {
                sql = "create table t_list(sid integer primary key autoincrement," +
                        "id varchar(200)," +
                        "name varchar(200)," +
                        "type varchar(200))";

                db.execSQL(sql);
            }
        };
    }

    /**
     * 插入记录(异步任务，数据库事务提交)
     */
    public void insert(JSONArray array) {
        if (array == null || array.size() == 0) {
            return;
        }
        DBLock = true;
        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = dbHandler.getWritableDatabase();
                db.execSQL("delete from t_list");//清空表数据
                db.execSQL("update sqlite_sequence SET seq = 0 where name ='t_list'");//自增长ID归零
                String sql = "insert into t_list (id,name,type)";
                JSONArray array = (JSONArray) objects[0];
                try {
                    db.beginTransaction(); // 手动设置开始事务
                    for (Object o : array) {
                        JSONObject root = (JSONObject) o;
                        BrandCarset data = JSON.toJavaObject(root, BrandCarset.class);
                        db.execSQL(sql + "values('" + data.getId() + "','" + data.getName() + "','" + data.getType() + "')");
                    }
                    db.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
                } catch (SQLException e) {
                    e.printStackTrace();
                    Logger.e(e,"SQLException");
                } finally {
                    db.endTransaction(); // 处理完成
                    db.close();
                    DBLock = false;
                }
                return null;
            }
        };
        task.execute(array);
    }

    /**
     * 模糊匹配
     */
    public List<BrandCarset> find(String str) {
        if(DBLock){
            Logger.e("数据库访问锁定，无法读取");
            return null;
        }
        List<BrandCarset> lists = new ArrayList<>();
        BrandCarset r = null;
        SQLiteDatabase db = dbHandler.getReadableDatabase();
        sql = "select * from t_list where name like '%" + str + "%'";
        // 用游标Cursor接收从数据库检索到的数据
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            r = new BrandCarset();
            r.setSid(cursor.getInt(cursor.getColumnIndex("sid")));
            r.setId(cursor.getString(cursor.getColumnIndex("id")));
            r.setName(cursor.getString(cursor.getColumnIndex("name")));
            r.setType(cursor.getString(cursor
                    .getColumnIndex("type")));
            lists.add(r);
        }
        cursor.close();
        db.close();
        return lists;
    }
}
