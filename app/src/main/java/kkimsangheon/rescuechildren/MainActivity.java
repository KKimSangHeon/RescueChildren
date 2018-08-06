package kkimsangheon.rescuechildren;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void registerChip(View v) {
        Intent intent = new Intent(this, RegisterChipActivity.class);
        startActivity(intent);
    }

    public void registerStudent(View v) {
        Intent intent = new Intent(this, InOutManageActivity.class);
        startActivity(intent);
    }

    public void startSystem(View v) {
        Intent intent = new Intent(this, InOutManageActivity.class);
        startActivity(intent);
    }

    public void showHistory(View v) {
        Intent intent = new Intent(this, InOutManageActivity.class);
        startActivity(intent);
    }
}