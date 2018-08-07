package kkimsangheon.rescuechildren.DB;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import kkimsangheon.rescuechildren.DB.VO.InOutManage;
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
        sb.append(" CURRENT_TAG_TIME TEXT, ");
        sb.append(" IS_OUT INTEGER, ");  //  0 이면 승차
        sb.append(" PARENT_PHONE_NUMBER TEXT ) "); // SQLite Database로 쿼리 실행

        StringBuffer sb2 = new StringBuffer();
        sb2.append(" CREATE TABLE IN_OUT_MANAGE ( ");
        sb2.append(" LONGITUDE TEXT, ");  // 위도
        sb2.append(" LATITUDE TEXT, ");  // 경도
        sb2.append(" IN_OUT_TIME TEXT, ");  //
        sb2.append(" IS_MANUAL INTEGER, ");  //
        sb2.append(" STUDENT_ID TEXT, ");  //
        sb2.append(" IS_OUT TEXT, ");  //
        sb2.append(" CONSTRAINT STUDENT_ID FOREIGN KEY(STUDENT_ID) REFERENCES STUDENT(ID) ON DELETE CASCADE) ");  //

        db.execSQL(sb.toString());
        db.execSQL(sb2.toString());
        db.execSQL("PRAGMA foreign_keys=on;");
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

    // 학생삭제할 때
    public void deleteStudent(Student student) {

        ArrayList<Student> studentArrayList;
        Student paramStudent = new Student();
        paramStudent.setName(student.getName());
        studentArrayList = selectRegisteredStudentList(paramStudent);

        InOutManage paramInOutManage = new InOutManage();
        for (Student tempStudent : studentArrayList) {
            paramInOutManage.setStudentId(tempStudent.getId());
            deleteInOutManage(paramInOutManage);
        }

        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM STUDENT WHERE 1 = 1 ");

        if (!student.getId().equals("")) {
            sb.append("AND ID = '" + student.getId() + "'");
        }

        if (!student.getName().equals("")) {
            sb.append("AND NAME LIKE '%" + student.getName() + "%'");
        }

        db.execSQL(sb.toString());

    }

    // 학생삭제할 때
    public void deleteInOutManage(InOutManage inOutManage) {


        StringBuffer sb = new StringBuffer();
        sb.append("DELETE FROM IN_OUT_MANAGE WHERE 1 = 1 ");

        if (!inOutManage.getStudentId().equals("")) {
            sb.append("AND STUDENT_ID = '" + inOutManage.getStudentId() + "'");
        }

        db.execSQL(sb.toString());
    }


    // 학생조회할 때  ( 학생 등록 및 관리에서 검색할 때 , 등하차시스템에서 내리지 않은 학생 판단할 때 사용)
    public ArrayList<Student> selectRegisteredStudentList(Student student) {
        ArrayList<Student> resultLIst = new ArrayList<>();
        Student resultStudent;

        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT ID, NAME, CLASS_NAME, IS_OUT, PARENT_PHONE_NUMBER,CURRENT_TAG_TIME FROM STUDENT WHERE 1=1");

        if (!student.getName().equals("")) {
            sb.append(" AND NAME LIKE '%" + student.getName() + "%'");
        }

        if (!student.getId().equals("")) {
            sb.append(" AND ID = '" + student.getId() + "'");
        }

        if (student.getIsOut() == 0) {
            sb.append(" AND IS_OUT = 0");
        }

        Cursor cursor = db.rawQuery(sb.toString(), null);

        while (cursor.moveToNext()) {
            resultStudent = new Student();
            resultStudent.setId(cursor.getString(0));
            resultStudent.setName(cursor.getString(1));
            resultStudent.setClassName(cursor.getString(2));
            resultStudent.setIsOut(cursor.getInt(3));
            resultStudent.setParentPhoneNumber(cursor.getString(4));
            resultStudent.setCurrentTagTime(cursor.getString(5));
            resultLIst.add(resultStudent);
        }
        return resultLIst;
    }


    // 전체하차 할 때
    public void updateAllOutState(Student student, InOutManage inOutManage) {

        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("MM월dd hh:mm:ss");
        String dateStr = dayTime.format(new Date(time));
        ArrayList<Student> studentList;

        Student paramStudent = new Student();
        paramStudent.setIsOut(0);
        paramStudent.setName(student.getName());
        studentList = selectRegisteredStudentList(paramStudent);

        for (Student tempStudent : studentList) {
            InOutManage paramInOutManage = new InOutManage();
            paramInOutManage.setStudentId(tempStudent.getId());
            paramInOutManage.setIsManual(1);
            paramInOutManage.setLatitude(inOutManage.getLatitude());
            paramInOutManage.setLongitude(inOutManage.getLongitude());
            paramInOutManage.setIsOut(1);
            paramInOutManage.setInOutTime(dateStr);

            insertInOutManage(paramInOutManage);

        }



        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE STUDENT SET IS_OUT = 1, CURRENT_TAG_TIME = '" + dateStr + "' WHERE IS_OUT=0 ");

        if (!student.getName().equals("")) {
            sb.append(" AND NAME LIKE '%" + student.getName() + "%'");
        }
        db.execSQL(sb.toString());


    }

    // 태깅하였을 때
    public void updateInOutState(Student student, InOutManage inOutManage) {
        // 5분 이내에 다시 찍었을 경우 이미 처리되었다고 출력

        long time = System.currentTimeMillis();
        SimpleDateFormat dayTime = new SimpleDateFormat("MM월dd hh:mm:ss");
        String dateStr = dayTime.format(new Date(time));

        StringBuffer sb = new StringBuffer();
        sb.append("UPDATE STUDENT SET IS_OUT = " + student.getIsOut() + ", CURRENT_TAG_TIME = '" + dateStr + "'");
        if (!student.getId().equals("")) {
            sb.append(" WHERE ID = '" + student.getId() + "'");
        }
        db.execSQL(sb.toString());


        InOutManage paramInOutManage = new InOutManage();
        paramInOutManage.setStudentId(student.getId());
        paramInOutManage.setIsManual(inOutManage.getIsManual());
        paramInOutManage.setLatitude(inOutManage.getLatitude());
        paramInOutManage.setLongitude(inOutManage.getLongitude());
        paramInOutManage.setIsOut(student.getIsOut());
        paramInOutManage.setInOutTime(dateStr);

        insertInOutManage(paramInOutManage);
    }

    public void insertInOutManage(InOutManage inOutManage) {
        StringBuffer sb = new StringBuffer();
        sb.append(" INSERT INTO IN_OUT_MANAGE ( ");
        sb.append(" STUDENT_ID , LONGITUDE , LATITUDE , IN_OUT_TIME , IS_MANUAL, IS_OUT  ) ");
        sb.append(" VALUES( ? , ? , ?, ?, ?,?) ");

        db.execSQL(sb.toString(), new Object[]{
                inOutManage.getStudentId(),
                inOutManage.getLongitude(),
                inOutManage.getLatitude(),
                inOutManage.getInOutTime(),
                inOutManage.getIsManual(),
                inOutManage.getIsOut()
        });

    }

    // 수동 하차 시


    // history 조회 할 때
    public ArrayList<InOutManage> selectInOutMange(Student student) {
        StringBuffer sb = new StringBuffer();
        ArrayList<InOutManage> resultList = new ArrayList<>();
        sb.append(" SELECT STUDENT.ID, STUDENT.NAME, STUDENT.CLASS_NAME, STUDENT.PARENT_PHONE_NUMBER, IN_OUT_MANAGE.LONGITUDE,IN_OUT_MANAGE.LATITUDE ,IN_OUT_MANAGE.IN_OUT_TIME ,IN_OUT_MANAGE.IS_MANUAL, IN_OUT_MANAGE.IS_OUT FROM IN_OUT_MANAGE LEFT JOIN STUDENT ON IN_OUT_MANAGE.STUDENT_ID = STUDENT.ID");

        if (!student.getName().equals("")) {
            sb.append(" WHERE NAME LIKE '%" + student.getName() + "%'");
        }

        sb.append(" ORDER BY IN_OUT_MANAGE.IN_OUT_TIME");

        Cursor cursor = db.rawQuery(sb.toString(), null);

        while (cursor.moveToNext()) {
            InOutManage tempInOutManage = new InOutManage();

            Student tempStudent = new Student();
            tempStudent.setId(cursor.getString(0));
            tempStudent.setName(cursor.getString(1));
            tempStudent.setClassName(cursor.getString(2));
            tempStudent.setParentPhoneNumber(cursor.getString(3));
            tempInOutManage.setStudent(tempStudent);


            tempInOutManage.setLongitude(cursor.getString(4));
            tempInOutManage.setLatitude(cursor.getString(5));
            tempInOutManage.setInOutTime(cursor.getString(6));
            tempInOutManage.setIsManual(cursor.getInt(7));
            tempInOutManage.setIsOut(cursor.getInt(8));

            resultList.add(tempInOutManage);
        }
        return resultList;

    }

}
