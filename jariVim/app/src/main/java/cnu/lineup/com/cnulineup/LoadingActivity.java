package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.os.Bundle;
import android.os.Handler;
import java.io.IOException;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class LoadingActivity extends Activity {

    //private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        //SessionCallback callback = new SessionCallback();
        //Session.getCurrentSession().addCallback(callback);
        //Session.getCurrentSession().checkAndImplicitOpen();




        Handler hd = new Handler();
        hd.postDelayed(new splashhandler() , 4000); // 3초 후에 hd Handler 실행
    }

    private class splashhandler implements Runnable{
        public void run() {
            startActivity(new Intent(getApplication(), MainActivity.class)); // 로딩이 끝난후 이동할 Activity
            LoadingActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
        }
    }
}
