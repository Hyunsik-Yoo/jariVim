package cnu.lineup.com.cnulineup;

import android.app.Activity;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    public static InterstitialAd interstitialAd;
    public static String serverIP = "168.188.127.132";
    public static Button btnRefresh;
    public static JSONObject currentProportion;
    public static DBOpenHelper dbOpenHelper;

    private ToggleButton btnBob, btnNoddle, btnCafe, btnDrink, btnFastfood, btnFork,
            btnFavorite, btnVote;
    private ImageButton btnSearch;
    private TabHost tabHost;
    private ExpandListAdapter expAdapter;
    private ArrayList<Group> expListItems;
    private ExpandableListView expandList, expandlistFavorite, expandListVote;
    private int lastExpandedPosition = -1;
    private ImageView kakaoProfile;
    private Bitmap kakaoThumbnail;

    private ProgressDialog pd;
    private LinearLayout frameFavorite, frameVote;
    private ViewPager vp;
    private FragList FragBob, FragNoddle, FragCafe, FragDrink, FragFastfood, FragFork;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setFullAd(); //메인엑티비티 로딩되면 광고 요청날림
        dbOpenHelper = new DBOpenHelper(this).open();

        //dbOpenHelper.insertFavoriteRestaurant("하오치");
        //Log.d(TAG,"insert 하오치 in sqlite");

        FragBob = FragList.newInstance("bob");
        FragNoddle = FragList.newInstance("noddle");
        FragFastfood = FragList.newInstance("fastfood");
        FragFork =  FragList.newInstance("meat");
        FragCafe =  FragList.newInstance("cafe");
        FragDrink = FragList.newInstance("drink");




        /** status bar 색상 변경 */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorBlack));
        }


        tabHost = (TabHost) findViewById(R.id.footer);

        btnBob = (ToggleButton) findViewById(R.id.btn_category_bob);
        btnNoddle = (ToggleButton) findViewById(R.id.btn_category_noodle);
        btnCafe = (ToggleButton) findViewById(R.id.btn_category_cafe);
        btnDrink = (ToggleButton) findViewById(R.id.btn_category_beer);
        btnFastfood = (ToggleButton) findViewById(R.id.btn_category_fastfood);
        btnFork = (ToggleButton) findViewById(R.id.btn_category_fork);
        btnSearch = (ImageButton) findViewById(R.id.btn_search);
        btnFavorite = (ToggleButton)findViewById(R.id.btn_favorite);
        btnVote = (ToggleButton)findViewById(R.id.btn_vote_list);
        frameFavorite = (LinearLayout) findViewById(R.id.layout_favorite);
        frameVote = (LinearLayout) findViewById(R.id.layout_vote_list);


        /** ViewPager Setting **/
        vp = (ViewPager)findViewById(R.id.vp);
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
        vp.setCurrentItem(0);

        /** Resgister ViewPager **/
        btnBob.setOnClickListener(movePageListener);
        btnBob.setTag(0);
        btnNoddle.setOnClickListener(movePageListener);
        btnNoddle.setTag(1);
        btnFastfood.setOnClickListener(movePageListener);
        btnFastfood.setTag(2);
        btnFork.setOnClickListener(movePageListener);
        btnFork.setTag(3);
        btnCafe.setOnClickListener(movePageListener);
        btnCafe.setTag(4);
        btnDrink.setOnClickListener(movePageListener);
        btnDrink.setTag(5);




        /**
         * 새로고침 버튼
         * 서버에서 새로운 데이터를 받아 Refresh시킴
         * TODO :  화면에도 바로 갱신시켜주는 것 필요함 */
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    currentProportion = new UtilMethod.threadVote(MainActivity.this).execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "데이터 새로고침", Toast.LENGTH_SHORT).show();
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


        /**
         * 밑에 탭 이미지 등록
         */
        ImageView tab_home_icon = new ImageView(this);
        tab_home_icon.setImageResource(R.drawable.selector_tab_home);
        ImageView tab_info_icon = new ImageView(this);
        tab_info_icon.setImageResource(R.drawable.selector_tab_info);
        ImageView tab_account_icon = new ImageView(this);
        tab_account_icon.setImageResource(R.drawable.selector_tab_account);
        ImageView tab_statistics_icon = new ImageView(this);
        tab_statistics_icon.setImageResource(R.drawable.selector_tab_statistics);


        /** 카카오 프로필사진 가져오기 */
        kakaoProfile = (ImageView) findViewById(R.id.kakao_profile);
        Thread getThumbnail = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(UserInfo.PROFILE_IMAGE_PATH);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    kakaoThumbnail = BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        getThumbnail.start();
        try {
            getThumbnail.join(); //쓰레드가 끝나기전에 이미지설정을 하면 안되므로 join으로 기다리기
            kakaoProfile.setImageBitmap(kakaoThumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }


        /**
         * 하단 탭 등록
         */
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("Tab1").setContent(R.id.tab1)
                .setIndicator(tab_home_icon);
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Tab2").setContent(R.id.tab2)
                .setIndicator(tab_info_icon);
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Tab3").setContent(R.id.tab3)
                .setIndicator(tab_statistics_icon);
        TabHost.TabSpec tab4 = tabHost.newTabSpec("Tab4").setContent(R.id.tab4)
                .setIndicator(tab_account_icon);

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);


        /**
         * 내정보 탭
         */
        btnFavorite.setOnClickListener(userInfoListener);
        btnVote.setOnClickListener(userInfoListener);
        expandlistFavorite = (ExpandableListView)findViewById(R.id.list_favorite);
        expandListVote = (ExpandableListView)findViewById(R.id.list_vote);
        expListItems = setItemsFavorite();
        setExpandListAdapterFavorite(expListItems);




        /** 메인 탭을 제외한 타머지탭 disable */
        tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(false);
        tabHost.getTabWidget().getChildTabViewAt(2).setEnabled(false);
        //tabHost.getTabWidget().getChildTabViewAt(3).setEnabled(false);


    }


    Button.OnClickListener userInfoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_favorite:
                    btnVote.setChecked(false);
                    btnFavorite.setChecked(true);
                    frameFavorite.setVisibility(LinearLayout.VISIBLE);
                    frameVote.setVisibility(LinearLayout.INVISIBLE);
                    expListItems = setItemsFavorite();
                    setExpandListAdapterFavorite(expListItems);
                    break;
                case R.id.btn_vote_list:
                    btnVote.setChecked(true);
                    btnFavorite.setChecked(false);
                    expListItems = setItemsVote();
                    setExpandListAdapterVote(expListItems);
                    frameFavorite.setVisibility(LinearLayout.INVISIBLE);
                    frameVote.setVisibility(LinearLayout.VISIBLE);

                    break;
            }
        }
    };


    // TODO : 밑에 3개 함수 하나로 합칠 수 있을듯?
    public void setExpandListAdapter(ArrayList<Group> ExpListItems) {
        expAdapter = new ExpandListAdapter(MainActivity.this, ExpListItems, expandList);
        expandList.setAdapter(expAdapter);
        expandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandList.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    public void setExpandListAdapterFavorite(ArrayList<Group> ExpListItems) {
        expAdapter = new ExpandListAdapter(MainActivity.this, ExpListItems, expandlistFavorite);
        expandlistFavorite.setAdapter(expAdapter);
        expandlistFavorite.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandlistFavorite.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    public void setExpandListAdapterVote(ArrayList<Group> ExpListItems) {
        expAdapter = new ExpandListAdapter(MainActivity.this, ExpListItems, expandListVote);
        expandListVote.setAdapter(expAdapter);
        expandListVote.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandListVote.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    public ArrayList<Group> setItemsFavorite() {
        try {
            ArrayList<Group> list_group = new ArrayList<Group>();
            ArrayList<JSONArray> restaurantList = new ArrayList<>();
            restaurantList.add(currentProportion.getJSONArray("bob"));
            restaurantList.add(currentProportion.getJSONArray("noddle"));
            restaurantList.add(currentProportion.getJSONArray("cafe"));
            restaurantList.add(currentProportion.getJSONArray("drink"));
            restaurantList.add(currentProportion.getJSONArray("fastfood"));
            restaurantList.add(currentProportion.getJSONArray("meat"));

            List<String> favoriteRes = dbOpenHelper.getFavoriteRestaurant();


            Iterator<JSONArray> iterRestaurant = restaurantList.iterator();
            while(iterRestaurant.hasNext()){
                JSONArray restaurant = iterRestaurant.next();

                if (restaurant != null) {
                    for (int i = 0; i < restaurant.length(); i++) {
                        String group_name = ((JSONObject) restaurant.get(i)).getString("title");
                        int proportion = ((JSONObject) restaurant.get(i)).getInt("proportion");
                        if(!favoriteRes.contains(group_name)){
                            continue;
                        }
                        Log.d(TAG,group_name);
                        Group group = new Group();
                        group.setName(group_name);
                        group.setProportion(proportion);

                        Child child = new Child();
                        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
                        Button btnConfirm = (Button) findViewById(R.id.btn_confirm);
                        Button btnCancle = (Button) findViewById(R.id.btn_cancle);
                        child.setSeekBar(seekBar);
                        child.setConfirm(btnConfirm);
                        child.setCancle(btnCancle);

                        group.setItems(child);
                        list_group.add(group);
                    }
                }
            }
            return list_group;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Group> setItemsVote() {
        try {
            ArrayList<Group> list_group = new ArrayList<Group>();

            ArrayList<ArrayList<String>> voteRes = new ArrayList<>(dbOpenHelper.getVote());
            Iterator<ArrayList<String>> voteIter =voteRes.iterator();
            while(voteIter.hasNext()){
                ArrayList<String> votePair = voteIter.next();
                String group_name = votePair.get(0);
                int proportion = Integer.parseInt(votePair.get(1));

                Group group = new Group();
                group.setName(group_name);
                group.setProportion(proportion);
                Log.d(TAG,group_name + " : "+proportion);

                list_group.add(group);
            }
            return list_group;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




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

    protected void requestUpdateProfile(String sex, String age) { //유저의 정보를 받아오는 함수
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put("sex", sex);
        properties.put("age", age);

        UserManagement.requestUpdateProfile(new ApiResponseCallback<Long>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Log.e(TAG, errorResult.getErrorMessage());
            }

            @Override
            public void onNotSignedUp() {

            }

            @Override
            public void onSuccess(Long userId) {
                Log.i(TAG, "succeeded to update user profile");
            }
        }, properties);
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
                    return FragBob;
                case 1:
                    return FragNoddle;
                case 2:
                    return FragFastfood;
                case 3:
                    return FragFork;
                case 4:
                    return FragCafe;
                case 5:
                    return FragDrink;
                default:
                    return null;
            }
        }

        /**
         * btnBob = (ToggleButton) findViewById(R.id.btn_category_bob);
         btnNoddle = (ToggleButton) findViewById(R.id.btn_category_noodle);
         btnCafe = (ToggleButton) findViewById(R.id.btn_category_cafe);
         btnDrink = (ToggleButton) findViewById(R.id.btn_category_beer);
         btnFastfood = (ToggleButton) findViewById(R.id.btn_category_fastfood);
         btnFork = (ToggleButton) findViewById(R.id.btn_category_fork);
         btnSearch = (ImageButton) findViewById(R.id.btn_search);
         btnFavorite = (ToggleButton)findViewById(R.id.btn_favorite);
         btnVote = (ToggleButton)findViewById(R.id.btn_vote_list);
         frameFavorite = (LinearLayout) findViewById(R.id.layout_favorite);
         frameVote = (LinearLayout) findViewById(R.id.layout_vote_list);
         */

        @Override
        public int getCount()
        {
            return 5;
        }
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

}
