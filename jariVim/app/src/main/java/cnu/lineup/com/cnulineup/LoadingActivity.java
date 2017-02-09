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


        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.


        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 3000); // 3초 후에 hd Handler 실행

    }

    private class splashhandler implements Runnable {
        public void run() {
            try {
                MainActivity.currentProportion = new MainActivity.threadVote(LoadingActivity
                        .this).execute()
                        .get();
                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                LoadingActivity.this.finish(); // 로딩페이지 Activity Stack에서 제거
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
