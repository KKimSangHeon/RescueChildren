package kkimsangheon.rescuechildren.intro;

/**
 * Created by SangHeon on 2018-08-01.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

import kkimsangheon.rescuechildren.R;

/**
 * Created by 상헌 on 2016-07-23.
 */
public class IntroActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);
        InnerClassHandler noLeakHandler = new InnerClassHandler(this);
        noLeakHandler.sendEmptyMessageDelayed(0, 1000); // 2초 뒤에 메시지 전달
    }
    // static inner class 로 선언
    static class InnerClassHandler extends Handler
    {
        private final WeakReference<IntroActivity> mActivity;

        InnerClassHandler(IntroActivity activity) {
            mActivity = new WeakReference<IntroActivity>(activity);
        }
        public void handleMessage(Message msg)
        {
            IntroActivity activity = mActivity.get();
            if(activity != null) {
                activity.handleMessage(msg);
            }
        }
    }

    private void handleMessage(Message msg) {
        Intent intent = new Intent(this, IntroActivity2.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade, R.anim.hold);
        finish();   // MainActivity 종료
    }
}