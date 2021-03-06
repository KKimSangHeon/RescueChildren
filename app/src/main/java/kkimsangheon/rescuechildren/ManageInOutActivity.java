package kkimsangheon.rescuechildren;

/**
 * Created by SangHeon on 2018-08-01.
 */

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import kkimsangheon.rescuechildren.DB.VO.InOutManage;
import kkimsangheon.rescuechildren.DB.VO.Student;
import kkimsangheon.rescuechildren.NFCHelper.NFCReadHelper;


 // 이 액티비티가 켜져있을 때 지속적으로 위치를 서버로 전송할것.
 // 서버구성은 Node.js로 구성할 예정
 //

public class ManageInOutActivity extends NFCReadHelper {
    ListView listView;
    private ArrayList<Student> studentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_in_out);


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

        ((Button) findViewById(R.id.allStudentOutButton)).setOnClickListener(onClickAllStudentOutButton);

        ((EditText) findViewById(R.id.selectStudentName)).addTextChangedListener(textWatcherInput);
        listView = (ListView) this.findViewById(R.id.listView);

        Student student = new Student();
        student.setIsOut(0);
        getStudentList(student);


        try {
            final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
        } catch (SecurityException ex) {
        }


    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //여기서 위치값이 갱신되면 이벤트가 발생한다.
            //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

            Log.d("test", "onLocationChanged, location:" + location);
            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            double altitude = location.getAltitude();   //고도
            float accuracy = location.getAccuracy();    //정확도
            String provider = location.getProvider();   //위치제공자
            //Gps 위치제공자에 의한 위치변화. 오차범위가 좁다.
            //Network 위치제공자에 의한 위치변화
            //Network 위치는 Gps에 비해 정확도가 많이 떨어진다.
            String check = Double.toString(longitude);
            Log.i("gps",check);
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };



    View.OnClickListener onClickAllStudentOutButton = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String message = "모든 학생을 하차 처리 하겠습니까?";

            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ManageInOutActivity.this); // 빌더 객체 생성
            alertBuilder.setTitle("하차 처리") // 제목
                    .setMessage(message) // 내용
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    Student paramStudent = new Student();
                                    paramStudent.setName(((EditText) findViewById(R.id.selectStudentName)).getText().toString());

                                    InOutManage paramInOutManage = new InOutManage();
                                    DBHelper.getInstance(ManageInOutActivity.this).updateAllOutState(paramStudent, paramInOutManage);

                                    Toast.makeText(ManageInOutActivity.this, "모든학생이 하차처리 되었습니다..", Toast.LENGTH_LONG).show();

                                    paramStudent = new Student();
                                    paramStudent.setIsOut(0);
                                    getStudentList(paramStudent);
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


    TextWatcher textWatcherInput = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Student paramStudent = new Student();
            paramStudent.setIsOut(0);
            paramStudent.setName(s.toString());
            getStudentList(paramStudent);
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
        student.setIsOrderByCuttentTagTime(1);
        studentList = DBHelper.getInstance(ManageInOutActivity.this).selectRegisteredStudentList(student);
        ManageInOutActivity.CustomAdapter adapter = new ManageInOutActivity.CustomAdapter(this, 0, studentList);
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
        String resultMessage = "";
        Student paramStudent;
        InOutManage paramInOutManage;
        Student queryResultStudent;

        ArrayList<Student> studentList = new ArrayList<>();
        student = getParsedStudentData(super.strRec);


        // ID로 등록되어있는 칩인지 확인
        paramStudent = new Student();
        paramStudent.setId(student.getId());
        studentList = DBHelper.getInstance(this).selectRegisteredStudentList(paramStudent);

        if (studentList.size() == 0) {
            Toast.makeText(this, "칩을 먼저 등록해주세요", Toast.LENGTH_LONG).show();
            return;
        }

        // 등록되어있을 경우 Student 테이블의 IS_OUT 갱신 및 IN_OUT_MANAGE에 삽입
        queryResultStudent = DBHelper.getInstance(this).selectRegisteredStudentList(student).get(0);


        paramStudent = new Student();
        paramStudent.setId(queryResultStudent.getId());

        if (queryResultStudent.getIsOut() == 0) {
            resultMessage = queryResultStudent.getName() + " 학생 하차 완료";
            paramStudent.setIsOut(1);
        } else {
            resultMessage = queryResultStudent.getName() + " 학생 승차 완료";
            paramStudent.setIsOut(0);
        }

        paramInOutManage = new InOutManage();

        // InOutManage에 위도 경도 세팅하는 모듈추가


        DBHelper.getInstance(this).updateInOutState(paramStudent, paramInOutManage);

        Toast.makeText(this, resultMessage, Toast.LENGTH_LONG).show();
//            alertDialog.cancel();

        paramStudent = new Student();
        paramStudent.setIsOut(0);
        getStudentList(paramStudent);
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

            TextView parentPhoneNumberTextView = (TextView) v.findViewById(R.id.textView3);
            parentPhoneNumberTextView.setText("P/N: " + student.getParentPhoneNumber());

            TextView curretnTagTimeTextView = (TextView) v.findViewById(R.id.textView4);
            curretnTagTimeTextView.setText("승차: " + student.getCurrentTagTime());

            // ImageView 인스턴스
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
            // 추 후 사진적용할 때 사용

            ImageButton imageButton = (ImageButton) v.findViewById(R.id.imageButton);
            imageButton.setImageResource(R.drawable.out);
            imageButton.setBackgroundColor(0x000000);

            ImageButton deleteStudentButton = (ImageButton) v.findViewById(R.id.imageButton);

            deleteStudentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message = student.getClassName() + " 반의 " + student.getName() + " 학생을 수동 하차처리 하시겠습니까";

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ManageInOutActivity.this); // 빌더 객체 생성
                    alertBuilder.setTitle("수동 하차 처리") // 제목
                            .setMessage(message) // 내용
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            Student paramStudent = new Student();
                                            paramStudent.setId(student.getId());
                                            paramStudent.setIsOut(1);

                                            InOutManage paramInOutManage = new InOutManage();
                                            paramInOutManage.setIsManual(1);
                                            DBHelper.getInstance(ManageInOutActivity.this).updateInOutState(paramStudent, paramInOutManage);

                                            paramStudent = new Student();
                                            paramStudent.setIsOut(0);
                                            getStudentList(paramStudent);

                                            Toast.makeText(ManageInOutActivity.this, student.getName() + "학생 수동 하처치리 되었습니다.", Toast.LENGTH_LONG).show();
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
