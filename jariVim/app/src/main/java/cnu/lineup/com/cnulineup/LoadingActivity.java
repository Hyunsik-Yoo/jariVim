package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;


import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.util.concurrent.ExecutionException;

public class LoadingActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();
    private SessionCallback callback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // 상단의 상태 바 제거
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        try {
            MainActivity.currentProportion = new MainActivity.threadVote(LoadingActivity
                    .this).execute().get();
            //Thread.sleep(3000);
        }catch (ExecutionException e){
            e.printStackTrace();
        }catch (InterruptedException e){
            e.printStackTrace();
        }


        //postDelayed함수가 메세지큐에 함수를 넣고있다가 2초뒤에 실행하기 때문에 메인(UI)쓰레드에 영향을 주지 않는다.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Session.getCurrentSession().isClosed()){
                    setContentView(R.layout.activity_kakao_log_in);
                    Session.getCurrentSession().addCallback(callback);
                    Session.getCurrentSession().checkAndImplicitOpen();
                    Log.d(TAG,"in Handler()");
                }else{
                    redirectSignupActivity();
                }
            }
        },2000);

    }


    private class SessionCallback implements ISessionCallback {
        /**
         * 카카오톡 로그인세션을 담당하는 Callback 함수
         */
        @Override
        public void onSessionOpened() {
            Log.d(TAG,"onSessionOpened()");
            redirectSignupActivity();  // 세션 연결성공 시 redirectSignupActivity() 호출
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
                Log.d(TAG,exception.toString());
            }
            // 세션연결이 실패하면 로그인화면으로 다시 이동
            Log.d(TAG,"before kakao_log_in");
            setContentView(R.layout.activity_kakao_log_in);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            redirectMainActivity();
            return;
        }
        else{
            Log.d(TAG,"in ActivityResult2");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void redirectSignupActivity() {
        requestMe();
    }

    protected void requestMe() {
        /**
         * 유저의 정보를 받아오는 요청함
         * Callback 함수 사용
         */

        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);
                Log.d(TAG,message);


                ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                if (result == ErrorCode.CLIENT_ERROR_CODE) {
                    // CLIENT_ERROR_CODE : 클라이언트 단에서 http 요청 전,후로 에러 발생한 경우.
                    finish();
                } else {
                    redirectLoginActivity();
                }
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                redirectLoginActivity();
            }

            @Override
            public void onNotSignedUp() {
                Log.e("test","test_hello2222");
            } // 카카오톡 회원이 아닐 시 showSignup(); 호출해야함

            @Override
            public void onSuccess(UserProfile userProfile) {  //성공 시 userProfile 형태로 반환
                UserInfo.KAKAO_NICKNAME = userProfile.getNickname();     // Nickname 값을 가져옴
                UserInfo.PROFILE_IMAGE_PATH = userProfile.getThumbnailImagePath();
                UserInfo.VOTING_OPPORTUNITY = userProfile.getProperty("voting_opportunity");

                Log.d(TAG,userProfile.toString());
                redirectMainActivity(); // 로그인 성공시 MainActivity로
            }
        });
    }

    private void redirectMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
        finish();
    }
    protected void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoadingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        finish();
    }
}
