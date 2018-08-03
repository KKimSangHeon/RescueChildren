package kkimsangheon.rescuechildren.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import kkimsangheon.rescuechildren.DB.VO.Student;

/**
 * Created by SangHeon on 2018-08-02.
 * Reference : http://cocomo.tistory.com/409
 */

public class DBHelper extends SQLiteOpenHelper {
    private Context context;

    DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    /**
     * Database가 존재하지 않을 때, 딱 한번 실행된다. * DB를 만드는 역할을 한다. * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) { // String 보다 StringBuffer가 Query 만들기 편하다.
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE STUDENT ( ");
        sb.append(" SEQ INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(" NAME TEXT, ");
        sb.append(" CLASS_ID TEXT, ");
        sb.append(" LONGITUDE TEXT, ");  // 위도
        sb.append(" LATITUDE TEXT, ");  // 경도
        sb.append(" GET_IN_TIME DATE, ");  // 탑승시간
        sb.append(" GET_OUT_TIME DATE, ");  // 하차시간
        sb.append(" PARENT_PHONE_NUMBER TEXT ) "); // SQLite Database로 쿼리 실행
        db.execSQL(sb.toString());
        Toast.makeText(context, "초기 DB생성완료", Toast.LENGTH_SHORT).show();
    }

    /**
     * Application의 버전이 올라가서 * Table 구조가 변경되었을 때 실행된다. * @param db * @param oldVersion * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(context, "버전이 올라갔습니다.", Toast.LENGTH_SHORT).show();
    }

    // 조회할 때


    // 태깅하였을 때
    public void tagging(Student student) {
    // 5분 이내에 다시 찍었을 경우 이미 처리되었다고 출력


    }

    // 수동 하차 시


    // 조회 할 때






















    /** * */
    public void testDB() {
        SQLiteDatabase db = getReadableDatabase();
    }
}
