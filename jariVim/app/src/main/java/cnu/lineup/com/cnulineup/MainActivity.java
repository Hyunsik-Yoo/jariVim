package cnu.lineup.com.cnulineup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.viewpagerindicator.TitlePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    // 현재 인구가 저장되는 JSON Obejct
    public static JSONObject currentProportion;
    public static DBOpenHelper dbOpenHelper;


    ViewPager vp;
    public static InterstitialAd interstitialAd;
    public static Button btnHome, btnResInfo, btnStatistic, btnUserInfo;
    public ImageButton btnSearch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setFullAd(); //메인엑티비티 로딩되면 광고 요청날림
        dbOpenHelper = new DBOpenHelper(this).open();



        /** status bar 색상 변경 */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorBlack));
        }



        vp = (ViewPager)findViewById(R.id.vp);
        btnHome = (Button)findViewById(R.id.btn_home);
        btnResInfo = (Button)findViewById(R.id.btn_info);
        btnStatistic = (Button)findViewById(R.id.btn_statistics);
        btnUserInfo = (Button)findViewById(R.id.btn_account);
        btnSearch = (ImageButton)findViewById(R.id.btn_search);


        //ViewPager
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);

        btnHome.setOnClickListener(movePageListener);
        btnHome.setTag(0);
        btnResInfo.setOnClickListener(movePageListener);
        btnResInfo.setTag(1);
        btnStatistic.setOnClickListener(movePageListener);
        btnStatistic.setTag(2);
        btnUserInfo.setOnClickListener(movePageListener);
        btnUserInfo.setTag(3);


        /** ViewPager 스크롤 기능 추가하던중 미룸 */
        TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
        titleIndicator.setViewPager(vp);
        titleIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /** 검색버튼 */
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Search Activity 로 이동하는 코드
                Intent intent_search = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent_search);
            }
        });

    }

    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int) v.getTag();
            vp.setCurrentItem(tag);
        }
    };

    private void setFullAd() {
        interstitialAd = new InterstitialAd(this); //새 광고를 만듭니다.
        interstitialAd.setAdUnitId(getResources().getString(R.string.ad_test_id)); //이전에 String에 저장해 두었던 광고 ID를 전면 광고에 설정합니다.
        AdRequest adRequest1 = new AdRequest.Builder().build(); //새 광고요청
        interstitialAd.loadAd(adRequest1); //요청한 광고를 load 합니다.
        interstitialAd.setAdListener(new AdListener() { //전면 광고의 상태를 확인하는 리스너 등록

            @Override
            public void onAdClosed() { //전면 광고가 열린 뒤에 닫혔을 때
                AdRequest adRequest1 = new AdRequest.Builder().build();  //새 광고요청
                interstitialAd.loadAd(adRequest1); //요청한 광고를 load 합니다.
            }
        });

    }


    public static void displayAD(Context context) {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("광고 요청중입니다. 다시 시도해 주세요.");
            dialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.show();
        }
    }


    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new FragMain();
                case 1:
                    return new FragInfo();
                case 2:
                    return new FragStatistics();
                case 3:
                    return new FragUserInfo();
                default:
                    return null;
            }
        }
        @Override
        public int getCount()
        {
            return 4;
        }
    }

}
