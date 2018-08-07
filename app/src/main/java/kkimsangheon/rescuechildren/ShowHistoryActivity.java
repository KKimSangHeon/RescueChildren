package kkimsangheon.rescuechildren;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kkimsangheon.rescuechildren.DB.DBHelper;
import kkimsangheon.rescuechildren.DB.VO.Student;

/**
 * Created by SangHeon on 2018-08-07.
 */

public class ShowHistoryActivity extends Activity {

    ListView listView;
    private ArrayList<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_history);

        ((EditText) findViewById(R.id.selectStudentName)).addTextChangedListener(textWatcherInput);
        listView = (ListView) this.findViewById(R.id.listView);

        getInOutList(new Student());
    }


    TextWatcher textWatcherInput = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Student student = new Student();
            student.setName(s.toString());
            getInOutList(student);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void getInOutList(Student student) {
        // student list 조회
        studentList = DBHelper.getInstance(ShowHistoryActivity.this).selectRegisteredStudentList(student);
        ShowHistoryActivity.CustomAdapter adapter = new ShowHistoryActivity.CustomAdapter(this, 0, studentList);
        listView.setAdapter(adapter);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    private class CustomAdapter extends ArrayAdapter<Student> {
        ArrayList<Student> studentList;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<Student> studentList) {
            super(context, textViewResourceId, studentList);
            this.studentList = studentList;

        }

        public View getView(int position, View convertView, ViewGroup parent) {

            final Student student = studentList.get(position);

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_view_item, null);
            }

            // 리스트뷰 내 item의 name 설정
            TextView studentNameTextView = (TextView) v.findViewById(R.id.textView1);
            studentNameTextView.setText("Name: " + student.getName());

            TextView classNameTextView = (TextView) v.findViewById(R.id.textView2);
            classNameTextView.setText("Class Name: " + student.getClassName());

            TextView studentIdTextView = (TextView) v.findViewById(R.id.textView3);
//            if (승차면)
//                studentIdTextView.setText("승차 시간: " + student.getId());
//            else
//                studentIdTextView.setText("하차 시간: " + student.getId());


            TextView parentPhoneNumberTextView = (TextView) v.findViewById(R.id.textView4);
            parentPhoneNumberTextView.setText("수동처리 여부: " + student.getParentPhoneNumber());

            // ImageView 인스턴스
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            // 추 후 사진적용할 때 사용


            ImageButton deleteStudentButton = (ImageButton) v.findViewById(R.id.imageButton);

            deleteStudentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 정보 더 확인하는 용으로 사용.. 위도,경도,등등


                    String message = "삭제대상" + "\nName : " + student.getName() + "\nClass Name: " + student.getClassName() + "\nID: " + student.getId() + "\nP/N: " + student.getParentPhoneNumber();

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ShowHistoryActivity.this); // 빌더 객체 생성
                    alertBuilder.setTitle("등록된 데이터 제거") // 제목
                            .setMessage(message) // 내용
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            DBHelper.getInstance(ShowHistoryActivity.this).deleteStudent(student);
                                            Toast.makeText(ShowHistoryActivity.this, student.getName() + "님의 데이터가 제거되었습니다.", Toast.LENGTH_LONG).show();
                                            getInOutList(new Student());
                                        }


                                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();  // 대회상자 종료
                        }
                    })
                            .show();


                }
            });

            return v;
        }
    }

}
