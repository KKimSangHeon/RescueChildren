package kkimsangheon.rescuechildren;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import kkimsangheon.rescuechildren.DB.VO.Student;
import kkimsangheon.rescuechildren.NFCHelper.NFCReadHelper;

/**
 * Created by SangHeon on 2018-08-06.
 */

public class RegisteredStudentManager extends NFCReadHelper {
    ListView listView;
    Boolean isRegisterStudentMode = false;
    int finalCost = 20000;
    AlertDialog alertDialog;
    AlertDialog.Builder alertDialogBuilder = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_student_manager);

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

        ((Button) findViewById(R.id.registerStudentButton)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                isRegisterStudentMode = true;
                alertDialogBuilder = new AlertDialog.Builder(RegisteredStudentManager.this).setTitle("Touch tag to write");
                alertDialog = alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        isRegisterStudentMode = false;
                    }

                }).create();
                alertDialog.show();

            }
        });


        listView = (ListView) this.findViewById(R.id.listView);

        ArrayList<String> items = new ArrayList<>();

        items.add("감자옹심이");
        items.add("콧등치기");
        items.add("황기닭백숙");
        items.add("한정식");
        items.add("한정식");
        items.add("한정식");
        items.add("한정식");
        items.add("한정식");

        CustomAdapter adapter = new CustomAdapter(this, 0, items);
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

        if (isRegisterStudentMode == false) {
            Toast.makeText(this, super.strRec + "학생 등록 버튼을 누르고 등록해주세요", Toast.LENGTH_SHORT).show();
       }else{

            student = getParsedStudentData(super.strRec);
            Toast.makeText(this, student.getName() + " 학생 등록 완료", Toast.LENGTH_SHORT).show();
            alertDialog.cancel();
        }
    }

    private class CustomAdapter extends ArrayAdapter<String> {
        private ArrayList<String> items;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_view_item, null);
            }


            //음식 종류 쓰기
            TextView textView = (TextView) v.findViewById(R.id.textView);
            textView.setText(items.get(position));

            //잔액


            // ImageView 인스턴스
            ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
//            ImageView upimage = (ImageView) v.findViewById(R.id.upimage);
            TextView priceView = (TextView) v.findViewById(R.id.price);
            TextView detailView = (TextView) v.findViewById(R.id.detail);


            // 리스트뷰의 아이템에 이미지를 변경한다.
            if ("감자옹심이".equals(items.get(position))) {
//                upimage.setImageResource(R.drawable.ic_launcher);
                imageView.setImageResource(R.drawable.ic_launcher);
                priceView.setText("8000 Won");

                detailView.setText("♥ 16");

            }

            final String text = items.get(position);
            ImageButton button = (ImageButton) v.findViewById(R.id.button);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ("감자옹심이".equals(text)) {
                        if (finalCost - 8000 >= 0) {
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(RegisteredStudentManager.this); // 빌더 객체 생성
                            alertBuilder.setTitle("주문확인") // 제목
                                    .setMessage("감자옹심이를 주문하시겠습니까?\n현재 잔액:" + finalCost) // 내용
                                    .setCancelable(false)
                                    .setPositiveButton("Yes",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    finalCost = finalCost - 8000;
                                                    Toast.makeText(RegisteredStudentManager.this, "감자옹심이 주문 완료되었습니다. 잔액:" + finalCost, Toast.LENGTH_SHORT).show();
                                                }


                                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();  // 대회상자 종료
                                }
                            })
                                    .show();


                        }
                    }
                }
            });

            return v;
        }
    }

}