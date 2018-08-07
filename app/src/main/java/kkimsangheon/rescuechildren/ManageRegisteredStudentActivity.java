package kkimsangheon.rescuechildren;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kkimsangheon.rescuechildren.DB.DBHelper;
import kkimsangheon.rescuechildren.DB.VO.Student;
import kkimsangheon.rescuechildren.NFCHelper.NFCReadHelper;

/**
 * Created by SangHeon on 2018-08-06.
 */

public class ManageRegisteredStudentActivity extends NFCReadHelper {
    ListView listView;
    Boolean isRegisterStudentMode = false;
    AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder = null;
    private ArrayList<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_registered_student);


        // NFC 어댑터를 구한다
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        // NFC 어댑터가 null 이라면 칩이 존재하지 않는 것으로 간주
        if (mNfcAdapter == null) {
            mTextView.setText("This phone is not NFC enable.");
            return;
        }

        // NFC 데이터 활성화에 필요한 인텐트를 생성
        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        // NFC 데이터 활성화에 필요한 인텐트 필터를 생성
        IntentFilter iFilter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            iFilter.addDataType("*/*");
            mIntentFilters = new IntentFilter[]{iFilter};
        } catch (Exception e) {
            mTextView.setText("Make IntentFilter error");
        }
        mNFCTechLists = new String[][]{new String[]{NfcF.class.getName()}};

        ((Button) findViewById(R.id.registerStudentButton)).setOnClickListener(onClickRegisterStudent);
        ((Button) findViewById(R.id.deleteAllStudentButton)).setOnClickListener(onClickDeleteAllStudent);


        ((EditText) findViewById(R.id.selectStudentName)).addTextChangedListener(textWatcherInput);
        listView = (ListView) this.findViewById(R.id.listView);

        getStudentList(new Student());

    }

    View.OnClickListener onClickDeleteAllStudent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message ="한번 데이터를 지우면 복구할 수 없습니다. 지우시겠습니까?";

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ManageRegisteredStudentActivity.this); // 빌더 객체 생성
            alertBuilder.setTitle("등록된 데이터 제거") // 제목
                    .setMessage(message) // 내용
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Student student = new Student();
                                    student.setName(((EditText) findViewById(R.id.selectStudentName)).getText().toString());
                                    DBHelper.getInstance(ManageRegisteredStudentActivity.this).deleteStudent(student);
                                    Toast.makeText(ManageRegisteredStudentActivity.this, "등록된 모든 데이터가 제거되었습니다.", Toast.LENGTH_LONG).show();
                                    getStudentList(new Student());
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();  // 대회상자 종료
                }
            })
                    .show();

        }
    };



    View.OnClickListener onClickRegisterStudent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            isRegisterStudentMode = true;
            alertDialogBuilder = new AlertDialog.Builder(ManageRegisteredStudentActivity.this).setTitle("Touch tag to write");
            alertDialog = alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    isRegisterStudentMode = false;
                }

            }).create();
            alertDialog.show();
        }
    };

    TextWatcher textWatcherInput = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Student student = new Student();
            student.setName(s.toString());
            getStudentList(student);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void getStudentList(Student student) {
        // student list 조회
        studentList = DBHelper.getInstance(ManageRegisteredStudentActivity.this).selectRegisteredStudentList(student);
        CustomAdapter adapter = new CustomAdapter(this, 0, studentList);
        listView.setAdapter(adapter);
    }

    public void onResume() {
        super.onResume();
        super.enableNFCReadMode();
    }

    public void onPause() {
        super.onPause();
        super.disableNFCReadMode();
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Student student;
        Student tempStudent = new Student();
        ArrayList<Student> idDupList = new ArrayList<>();
        String resultMessage = "";

        if (isRegisterStudentMode == false) {
            Toast.makeText(this, super.strRec + "학생 등록 버튼을 누르고 등록해주세요", Toast.LENGTH_LONG).show();
        } else {

            student = getParsedStudentData(super.strRec);

            // 이미등록된 학생인지 판단
            tempStudent.setId(student.getId());
            idDupList = DBHelper.getInstance(this).selectRegisteredStudentList(tempStudent);

            if (idDupList.size() == 0) {
                DBHelper.getInstance(this).insertStudent(student);
                resultMessage = student.getName() + " 학생 등록 완료";
                getStudentList(new Student());
            } else {
                resultMessage = "ID: " + idDupList.get(0).getId() + " 이름: " + idDupList.get(0).getName() + "\n이미 등록되어있습니다.";
            }
            Toast.makeText(this, resultMessage, Toast.LENGTH_LONG).show();
//            alertDialog.cancel();
        }
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
            studentIdTextView.setText("ID: " + student.getId());

            TextView parentPhoneNumberTextView = (TextView) v.findViewById(R.id.textView4);
            parentPhoneNumberTextView.setText("P/N: " + student.getParentPhoneNumber());

            // ImageView 인스턴스
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            // 추 후 사진적용할 때 사용

            ImageButton imageButton = (ImageButton) v.findViewById(R.id.imageButton);
            imageButton.setBackgroundColor(0x000000);

            ImageButton deleteStudentButton = (ImageButton) v.findViewById(R.id.imageButton);

            deleteStudentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                            String message ="삭제대상"+"\nName : "+ student.getName() + "\nClass Name: " +   student.getClassName() + "\nID: "+student.getId() + "\nP/N: " + student.getParentPhoneNumber();

                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ManageRegisteredStudentActivity.this); // 빌더 객체 생성
                            alertBuilder.setTitle("등록된 데이터 제거") // 제목
                                    .setMessage(message) // 내용
                                    .setCancelable(false)
                                    .setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    DBHelper.getInstance(ManageRegisteredStudentActivity.this).deleteStudent(student);
                                                    Toast.makeText(ManageRegisteredStudentActivity.this, student.getName()+"님의 데이터가 제거되었습니다.", Toast.LENGTH_LONG).show();
                                                    getStudentList(new Student());
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