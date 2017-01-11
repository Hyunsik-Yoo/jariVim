package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends Activity {

    public static InterstitialAd interstitialAd;
    private RewardedVideoAd mAd;
    public static String server_IP = "lineup-server.cloudapp.net";
    ToggleButton btn_bob, btn_noddle, btn_cafe, btn_drink, btn_fastfood, btn_fork, btn_sortby_popular, btn_sortby_text;
    ImageButton btn_search;
    public static Button btn_refresh, btn_ad;
    private TabHost tabHost;
    private ExpandListAdapter ExpAdapter;
    private ArrayList<Group> ExpListItems;
    private ExpandableListView ExpandList;
    private int lastExpandedPosition = -1;
    private ImageView kakao_profile;
    private Bitmap kakao_thumbnail;
    private TextView kakao_nickname;

    public static Comparator<Group> comparatorByText = new Comparator<Group>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(Group group1, Group group2) {
            return collator.compare(group1.getName(), group2.getName());
        }
    };

    public static Comparator<Group> comparatorByPopular = new Comparator<Group>() {
        @Override
        public int compare(Group group1, Group group2) {
            return group1.getProportion() < group2.getProportion() ? 1 : group1.getProportion() > group2.getProportion() ? -1 : 0;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //status bar 색상 변경
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorBlack));
        }


        tabHost = (TabHost) findViewById(R.id.footer);

        btn_bob = (ToggleButton) findViewById(R.id.btn_category_bob);
        btn_bob.setOnClickListener(listener_category);
        btn_noddle = (ToggleButton) findViewById(R.id.btn_category_noodle);
        btn_noddle.setOnClickListener(listener_category);
        btn_cafe = (ToggleButton) findViewById(R.id.btn_category_cafe);
        btn_cafe.setOnClickListener(listener_category);
        btn_drink = (ToggleButton) findViewById(R.id.btn_category_beer);
        btn_drink.setOnClickListener(listener_category);
        btn_fastfood = (ToggleButton) findViewById(R.id.btn_category_fastfood);
        btn_fastfood.setOnClickListener(listener_category);
        btn_fork = (ToggleButton) findViewById(R.id.btn_category_fork);
        btn_fork.setOnClickListener(listener_category);
        btn_search = (ImageButton) findViewById(R.id.btn_search);
        btn_sortby_popular = (ToggleButton) findViewById(R.id.btn_sortby_popular);
        btn_sortby_popular.setOnClickListener(sort_listener);
        btn_sortby_popular.setChecked(true);
        btn_sortby_text = (ToggleButton) findViewById(R.id.btn_sortby_text);
        btn_sortby_text.setOnClickListener(sort_listener);



        setFullAd();
        btn_ad = (Button) findViewById(R.id.btn_ad);
        btn_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAD(MainActivity.this);
            }
        });

        btn_refresh = (Button) findViewById(R.id.btn_refresh);
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String category_name = "";

                if (btn_bob.isChecked()) {
                    category_name = "bob";
                } else if (btn_noddle.isChecked()) {
                    category_name = "noddle";
                } else if (btn_cafe.isChecked()) {
                    category_name = "cafe";
                } else if (btn_drink.isChecked()) {
                    category_name = "drink";
                } else if (btn_fastfood.isChecked()) {
                    category_name = "fastfood";
                } else if (btn_fork.isChecked()) {
                    category_name = "meat";
                }
                ExpListItems = setItems(category_name);
                //정렬 눌린 버튼에 따라서 정렬해야함
                setExpandListAdapter(ExpListItems);
                Toast.makeText(MainActivity.this, "데이터 새로고침", Toast.LENGTH_SHORT).show();
            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Search Activity 로 이동하는 코드
                Intent intent_search = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent_search);
            }
        });


        ImageView tab_home_icon = new ImageView(this);
        tab_home_icon.setImageResource(R.drawable.selector_tab_home);
        ImageView tab_info_icon = new ImageView(this);
        tab_info_icon.setImageResource(R.drawable.selector_tab_info);
        ImageView tab_account_icon = new ImageView(this);
        tab_account_icon.setImageResource(R.drawable.selector_tab_account);
        ImageView tab_statistics_icon = new ImageView(this);
        tab_statistics_icon.setImageResource(R.drawable.selector_tab_statistics);


        //카카오 프로필사진 가져오기
        kakao_profile = (ImageView) findViewById(R.id.kakao_profile);
        Thread getThumbnail = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(KakaoSignupActivity.profileImage);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    kakao_thumbnail = BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        getThumbnail.start();

        try {
            getThumbnail.join(); //쓰레드가 끝나기전에 이미지설정을 하면 안되므로 join으로 기다리기
            kakao_profile.setImageBitmap(kakao_thumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        kakao_nickname = (TextView) findViewById(R.id.kakao_nickname);
        kakao_nickname.setText(KakaoSignupActivity.kakaoNickname);


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

        ExpandList = (ExpandableListView) findViewById(R.id.list_main);
        ExpListItems = setItems("bob");
        Collections.sort(ExpListItems, comparatorByPopular);
        setExpandListAdapter(ExpListItems);


        //메인 탭을 제외한 타머지탭 disable
        /*
        tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(false);
        tabHost.getTabWidget().getChildTabViewAt(2).setEnabled(false);
        tabHost.getTabWidget().getChildTabViewAt(3).setEnabled(false);
        */

    }


    Button.OnClickListener listener_category = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String category_name = null;
            switch (view.getId()) {
                case R.id.btn_category_bob:
                    category_name = "bob";
                    btn_bob.setChecked(true);
                    btn_noddle.setChecked(false);
                    btn_cafe.setChecked(false);
                    btn_drink.setChecked(false);
                    btn_fastfood.setChecked(false);
                    btn_fork.setChecked(false);
                    break;
                case R.id.btn_category_noodle:
                    category_name = "noddle";
                    btn_bob.setChecked(false);
                    btn_noddle.setChecked(true);
                    btn_cafe.setChecked(false);
                    btn_drink.setChecked(false);
                    btn_fastfood.setChecked(false);
                    btn_fork.setChecked(false);
                    break;
                case R.id.btn_category_fastfood:
                    category_name = "fastfood";
                    btn_bob.setChecked(false);
                    btn_noddle.setChecked(false);
                    btn_cafe.setChecked(false);
                    btn_drink.setChecked(false);
                    btn_fastfood.setChecked(true);
                    btn_fork.setChecked(false);
                    break;
                case R.id.btn_category_fork:
                    category_name = "meat";
                    btn_bob.setChecked(false);
                    btn_noddle.setChecked(false);
                    btn_cafe.setChecked(false);
                    btn_drink.setChecked(false);
                    btn_fastfood.setChecked(false);
                    btn_fork.setChecked(true);
                    break;
                case R.id.btn_category_cafe:
                    category_name = "cafe";
                    btn_bob.setChecked(false);
                    btn_noddle.setChecked(false);
                    btn_cafe.setChecked(true);
                    btn_drink.setChecked(false);
                    btn_fastfood.setChecked(false);
                    btn_fork.setChecked(false);
                    break;
                case R.id.btn_category_beer:
                    category_name = "drink";
                    btn_bob.setChecked(false);
                    btn_noddle.setChecked(false);
                    btn_cafe.setChecked(false);
                    btn_drink.setChecked(true);
                    btn_fastfood.setChecked(false);
                    btn_fork.setChecked(false);
                    break;
            }

            ExpListItems = setItems(category_name);
            Collections.sort(ExpListItems, comparatorByPopular);
            setExpandListAdapter(ExpListItems);
        }
    };

    Button.OnClickListener sort_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_sortby_popular:
                    btn_sortby_popular.setChecked(true);
                    btn_sortby_text.setChecked(false);
                    Collections.sort(ExpListItems, comparatorByPopular);
                    setExpandListAdapter(ExpListItems);

                    break;
                case R.id.btn_sortby_text:
                    btn_sortby_popular.setChecked(false);
                    btn_sortby_text.setChecked(true);
                    Collections.sort(ExpListItems, comparatorByText);
                    setExpandListAdapter(ExpListItems);
                    break;
            }
        }
    };

    public void setExpandListAdapter(ArrayList<Group> ExpListItems) {
        ExpAdapter = new ExpandListAdapter(MainActivity.this, ExpListItems, ExpandList);
        ExpandList.setAdapter(ExpAdapter);
        ExpandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    ExpandList.collapseGroup(lastExpandedPosition);

                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    public ArrayList<Group> setItems(String category) {
        JSONArray result = null;

        try {
            result = new Thread_vote().execute(category).get();
            Log.i("debug", result.toString());

            ArrayList<Group> list_group = new ArrayList<Group>();

            if (result != null) {
                for (int i = 0; i < result.length(); i++) {
                    String group_name = ((JSONObject) result.get(i)).getString("title");
                    int proportion = ((JSONObject) result.get(i)).getInt("proportion");
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
            return list_group;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //카테고리별 음식점 이름과 최근인구밀도를 서버로부터 받아 리턴
    private class Thread_vote extends AsyncTask<String, Integer, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... parm) {
            try {
                String category = parm[0];
                URL url = new URL("http://" + server_IP + ":8000/lineup/current/?category=" + category);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = new JSONObject(getStringFromInputStream(in));
                JSONArray data = json.getJSONArray("data");

                return data;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

    //서버에서 정보를 받는 과정
    public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }

    private void setFullAd() {
        /*
        MobileAds.initialize(this, "ca-app-pub-1527951560812478~9578415948");
        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {

            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {

            }

            @Override
            public void onRewarded(RewardItem rewardItem) {

            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }
        });
        loadRewardedVideoAd();
        */


        interstitialAd = new InterstitialAd(this); //새 광고를 만듭니다.
        interstitialAd.setAdUnitId(getResources().getString(R.string.ad_unit_id)); //이전에 String에 저장해 두었던 광고 ID를 전면 광고에 설정합니다.
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

    /*
    private void loadRewardedVideoAd() {
        if (!mAd.isLoaded()) {
            mAd.loadAd(getResources().getString(R.string.ad_unit_id), new AdRequest.Builder().build());
        }
    }*/


    public static void displayAD(Context context) {
        if(interstitialAd.isLoaded()){
            interstitialAd.show();
        }else{
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

}
