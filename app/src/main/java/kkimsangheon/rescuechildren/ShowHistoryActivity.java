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
import kkimsangheon.rescuechildren.DB.VO.InOutManage;
import kkimsangheon.rescuechildren.DB.VO.Student;

/**
 * Created by SangHeon on 2018-08-07.
 */

public class ShowHistoryActivity extends Activity {

    ListView listView;
    private ArrayList<Student> studentList;
    private ArrayList<InOutManage> inOutManageList;

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
        inOutManageList = DBHelper.getInstance(ShowHistoryActivity.this).selectInOutMange(student);
        ShowHistoryActivity.CustomAdapter adapter = new ShowHistoryActivity.CustomAdapter(this, 0, inOutManageList);
        listView.setAdapter(adapter);
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    private class CustomAdapter extends ArrayAdapter<InOutManage> {
        ArrayList<InOutManage> inOutManageList;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<InOutManage> inOutManageList) {
            super(context, textViewResourceId, inOutManageList);
            this.inOutManageList = inOutManageList;

        }

        public View getView(int position, View convertView, ViewGroup parent) {

            final InOutManage inOutManage = inOutManageList.get(position);

            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_view_item, null);
            }

            // 리스트뷰 내 item의 name 설정
            TextView studentNameTextView = (TextView) v.findViewById(R.id.textView1);
            studentNameTextView.setText("Name: " + inOutManage.getStudent().getName());

            TextView classNameTextView = (TextView) v.findViewById(R.id.textView2);
            classNameTextView.setText("Class Name: " + inOutManage.getStudent().getClassName());

            TextView studentIdTextView = (TextView) v.findViewById(R.id.textView3);
            studentIdTextView.setText((inOutManage.getIsOut() == 0 ? "승차시간" : "하차시간") + inOutManage.getInOutTime());

            TextView parentPhoneNumberTextView = (TextView) v.findViewById(R.id.textView4);
            parentPhoneNumberTextView.setText("수동처리 여부: " + (inOutManage.getIsManual() == 1 ? 'Y' : 'N'));

            // ImageView 인스턴스
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            // 추 후 사진적용할 때 사용


            ImageButton imageButton = (ImageButton) v.findViewById(R.id.imageButton);
            imageButton.setImageResource(R.drawable.more);
            imageButton.setBackgroundColor(0x000000);

            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 정보 더 확인하는 용으로 사용.. 위도,경도,등등


                    String message = "P/N : " + inOutManage.getStudent().getParentPhoneNumber()+"\nLongitude : "+inOutManage.getLongitude()+"\nLatitude : "+inOutManage.getLatitude();

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ShowHistoryActivity.this); // 빌더 객체 생성
                    alertBuilder.setTitle("등록된 데이터 제거") // 제목
                            .setMessage(message) // 내용
                            .setCancelable(false)
                            .setNegativeButton("창 닫기", new DialogInterface.OnClickListener() {
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
