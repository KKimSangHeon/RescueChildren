package kkimsangheon.rescuechildren.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

import kkimsangheon.rescuechildren.DB.VO.Student;

/**
 * Created by SangHeon on 2018-08-02.
 * Reference : http://cocomo.tistory.com/409
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 2;
    public static final String DB_NAME = "SH";
    public static DBHelper dbHelper = null;
    private Context context;
    private SQLiteDatabase db;

    private DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        db = this.getWritableDatabase();
    }

    public static DBHelper getInstance(Context context) { // 싱글턴 패턴
        if (dbHelper == null) {
            dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        }
        return dbHelper;
    }

    /**
     * Database가 존재하지 않을 때, 딱 한번 실행된다. * DB를 만드는 역할을 한다. * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) { // String 보다 StringBuffer가 Query 만들기 편하다.
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE STUDENT ( ");
        sb.append(" ID TEXT PRIMARY KEY , ");
        sb.append(" NAME TEXT, ");
        sb.append(" CLASS_NAME TEXT, ");
        sb.append(" IS_OUT INTEGER, ");  //  0 이면 승차
        sb.append(" PARENT_PHONE_NUMBER TEXT ) "); // SQLite Database로 쿼리 실행

        StringBuffer sb2 = new StringBuffer();
        sb2.append(" CREATE TABLE IN_OUT_MANAGE ( ");
        sb2.append(" SEQ INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb2.append(" LONGITUDE TEXT, ");  // 위도
        sb2.append(" LATITUDE TEXT, ");  // 경도
        sb2.append(" IN_OUT_TIME DATE, ");  //
        sb2.append(" IS_MANUAL INTEGER, ");  //
        sb2.append(" STUDENT_ID TEXT, ");  //
        sb2.append(" CONSTRAINT STUDENT_ID FOREIGN KEY(STUDENT_ID) REFERENCES STUDENT(ID)) ");  //

        db.execSQL(sb.toString());
        db.execSQL(sb2.toString());
        Toast.makeText(context, "DB 구축완료", Toast.LENGTH_SHORT).show();
    }

    /**
     * Application의 버전이 올라가서 * Table 구조가 변경되었을 때 실행된다. * @param db * @param oldVersion * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Toast.makeText(context, "버전이 올라갔습니다.", Toast.LENGTH_SHORT).show();
    }

    // 학생등록할 때
    public void insertStudent(Student student) {

        StringBuffer sb = new StringBuffer();
        sb.append(" INSERT INTO STUDENT ( ");
        sb.append(" ID, NAME, CLASS_NAME, IS_OUT, PARENT_PHONE_NUMBER ) ");
        sb.append(" VALUES( ? , ? , ?, ?, ?) ");

        db.execSQL(sb.toString(), new Object[]{
                student.getId(),
                student.getName(),
                student.getClassName(),
                student.getIsOut(),
                student.getParentPhoneNumber()
        });

    }


    // 학생조회할 때  ( 학생 등록 및 관리에서 검색할 때 , 등하차시스템에서 내리지 않은 학생 판단할 때 사용)
    public ArrayList<Student> selectRegisteredStudentList(Student student) {
        ArrayList<Student> resultLIst = new ArrayList<>();
        Student resultStudent;

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ID, NAME, CLASS_NAME, IS_OUT, PARENT_PHONE_NUMBER FROM STUDENT WHERE 1=1");

        if (!student.getName().equals("") ) {
            sb.append(" AND NAME LIKE '%" + student.getName() + "%'");
        }

        Cursor cursor = db.rawQuery(sb.toString(), null);

        while (cursor.moveToNext()) {
            resultStudent = new Student();
            resultStudent.setId(cursor.getString(0));
            resultStudent.setName(cursor.getString(1));
            resultStudent.setClassName(cursor.getString(2));
            resultStudent.setIsOut(cursor.getInt(3));
            resultStudent.setParentPhoneNumber(cursor.getString(4));
            resultLIst.add(resultStudent);
        }
        return resultLIst;
    }

    // 내리지 않은 학생 조회할 때

    // 기록 확인할 때

    // 태깅하였을 때
    public void tagging(Student student) {
        // 5분 이내에 다시 찍었을 경우 이미 처리되었다고 출력


    }

    // 수동 하차 시


    // 조회 할 때


}
