package kkimsangheon.rescuechildren;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import kkimsangheon.rescuechildren.DB.DBHelper;

public class MainActivity extends Activity {

    private DBHelper dbHelper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(dbHelper == null) { dbHelper = new DBHelper(MainActivity.this, "SHDB", null , DBHelper.DB_VERSION); }
        db = dbHelper.getWritableDatabase();
    }

    public void registerChip(View v) {
        Intent intent = new Intent(this, RegisterChipActivity.class);
        startActivity(intent);
    }

    public void registeredStudentManage(View v) {
        Intent intent = new Intent(this, RegisteredStudentManager.class);
        startActivity(intent);
    }

    public void startSystem(View v) {
        Intent intent = new Intent(this, ManageInOutActivity.class);
        startActivity(intent);
    }

    public void showHistory(View v) {
        Intent intent = new Intent(this, ManageInOutActivity.class);
        startActivity(intent);
    }
}