package kkimsangheon.rescuechildren;

/**
 * Created by SangHeon on 2018-08-01.
 */

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.widget.Toast;

import kkimsangheon.rescuechildren.NFCHelper.NFCReadHelper;


public class ManageInOutActivity extends NFCReadHelper {


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
    }
    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

            Toast.makeText(this, super.strRec , Toast.LENGTH_SHORT).show();
    }

    public void onResume() {
        super.onResume();
        super.enableNFCReadMode();
    }

    public void onPause() {
        super.onPause();
        super.disableNFCReadMode();
    }


}