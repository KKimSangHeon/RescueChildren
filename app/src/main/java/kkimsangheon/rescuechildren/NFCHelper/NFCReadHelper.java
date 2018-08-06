package kkimsangheon.rescuechildren.NFCHelper;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.StringTokenizer;

import kkimsangheon.rescuechildren.DB.VO.Student;

/**
 * Created by SangHeon on 2018-08-06.
 */

public class NFCReadHelper extends Activity {
    protected TextView mTextView;
    protected NfcAdapter mNfcAdapter; // NFC 어댑터
    protected PendingIntent mPendingIntent; // 수신받은 데이터가 저장된 인텐트
    protected IntentFilter[] mIntentFilters; // 인텐트 필터
    protected String[][] mNFCTechLists;
    protected String strRec = "";

    protected void enableNFCReadMode() {
        //  NFC 어댑터를 활성화 한다
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilters, mNFCTechLists);

        // NFC 태그 스캔으로 앱이 자동 실행되었을때
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
            // 인텐트에 포함된 정보를 분석해서 화면에 표시
            onNewIntent(getIntent());
    }

    protected void disableNFCReadMode() {
        // 앱이 종료될때 NFC 어댑터를 비활성화 한다
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(this);
    }

    // NFC 태그 정보 수신 함수. 인텐트에 포함된 정보를 분석해서 화면에 표시
    @Override
    public void onNewIntent(Intent intent) {
        // 인텐트에서 액션을 추출
        String action = intent.getAction();
        // 인텐트에서 태그 정보 추출
        String tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG).toString();
        String strMsg = action + "\n\n" + tag;
        // 액션 정보와 태그 정보를 화면에 출력

        // 인텐트에서 NDEF 메시지 배열을 구한다
        Parcelable[] messages = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (messages == null) return;
        for (int i = 0; i < messages.length; i++)
            showMsg((NdefMessage) messages[i]);
        // NDEF 메시지를 화면에 출력
    }

    // NDEF 메시지를 화면에 출력
    private void showMsg(NdefMessage mMessage) {
        // NDEF 메시지에서 NDEF 레코드 배열을 구한다
        NdefRecord[] recs = mMessage.getRecords();
        for (int i = 0; i < recs.length; i++) {
            // 개별 레코드 데이터를 구한다
            NdefRecord record = recs[i];
            byte[] payload = record.getPayload();
            // 레코드 데이터 종류가 텍스트 일때
            if (Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) {
                // 버퍼 데이터를 인코딩 변환
                strRec = byteDecoding(payload);
            }
            // 레코드 데이터 종류가 URI 일때
            else if (Arrays.equals(record.getType(), NdefRecord.RTD_URI)) {
                strRec = new String(payload, 0, payload.length);
                strRec = "URI: " + strRec;
            }
        }
    }

    // 버퍼 데이터를 디코딩해서 String 으로 변환
    private String byteDecoding(byte[] buf) {
        String strText = "";
        String textEncoding;
        if ((buf[0] & 0200) == 0)
            textEncoding = "UTF-8";
        else
            textEncoding = "UTF-16";

        int langCodeLen = buf[0] & 0077;
        try {
            strText = new String(buf, langCodeLen + 1,
                    buf.length - langCodeLen - 1, textEncoding);
        } catch (Exception e) {
            Log.d("tag1", e.toString());
        }
        return strText;
    }

    protected Student getParsedStudentData(String str) {
        Student student = new Student();

        StringTokenizer st = new StringTokenizer(str, "/");

        student.setId(st.nextToken());
        student.setName(st.nextToken());
        student.setClassName(st.nextToken());
        student.setParentPhoneNumber(st.nextToken());

        return student;
    }

}
