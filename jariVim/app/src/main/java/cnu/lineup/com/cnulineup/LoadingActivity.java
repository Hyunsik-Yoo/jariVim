package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class LoadingActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // 상단의 상태 바 제거

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        // 3초 후에 hd Handler 실행
        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000);

    }

    // 서버에서 전체 투표율 받아오는 동안 로딩페이지 나오도록 표시
    private class splashhandler implements Runnable {
        public void run() {
            try {
                MainActivity.currentProportion = new MainActivity.threadVote(LoadingActivity
                        .this).execute().get();

                startActivity(new Intent(LoadingActivity.this, KakaoLogInActivity.class));
                //startActivity(new Intent(LoadingActivity.this, MainActivity.class)); // 대체용
                // 우측에서 새로운 액티비티가 들어오도록 애니메이션 부여
                //overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                LoadingActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
