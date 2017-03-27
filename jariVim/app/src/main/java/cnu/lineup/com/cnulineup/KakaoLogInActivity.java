package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

public class KakaoLogInActivity extends Activity {
    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        callback = new SessionCallback();
        // 로그인정보가 남아있는지 확인
        // 아직 열려있으면 그대로 해당 세션그대로 사용 -> redirectSignupAcitivy()
        // 닫혀있으면 새로운 세션을 열도록 callback 함수 호출
        if(Session.getCurrentSession().isClosed()){
            setContentView(R.layout.activity_kakao_log_in);

            // 상단에 상태바 제거
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
            Session.getCurrentSession().addCallback(callback);
            Session.getCurrentSession().checkAndImplicitOpen();
        }else{
            redirectSignupActivity();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private class SessionCallback implements ISessionCallback {
        /**
         * 카카오톡 로그인세션을 담당하는 Callback 함수
         */
        @Override
        public void onSessionOpened() {
            redirectSignupActivity();  // 세션 연결성공 시 redirectSignupActivity() 호출
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            if(exception != null) {
                Logger.e(exception);
            }
            // 세션연결이 실패하면 로그인화면으로 다시 이동
            setContentView(R.layout.activity_kakao_log_in);
        }
    }

    protected void redirectSignupActivity() {
        /**
         * 세션 연결 성공 시 KakaoSignupActivity로 이동
         */
        /*
        final Intent intent = new Intent(this, KakaoSignupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
        */
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

                //UserInfo.SEX = userProfile.getProperty("sex");
                //UserInfo.AGE = userProfile.getProperty("age");


                /*
                Logger.d("UserProfile : " + userProfile);
                if(UserInfo.AGE != null)
                    Log.d("AGE",UserInfo.AGE);
                if(UserInfo.SEX != null)
                    Log.d("SEX",UserInfo.SEX);
                */
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
