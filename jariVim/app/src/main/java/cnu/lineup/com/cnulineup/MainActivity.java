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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.ads.reward.RewardedVideoAd;
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
import java.util.Map;

public class MainActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();
    public static InterstitialAd interstitialAd;
    private RewardedVideoAd mAd;
    public static String serverIP = "222.239.249.114";
    private ToggleButton btnBob, btnNoddle, btnCafe, btnDrink, btnFastfood, btnFork, btnSortByPopular, btnSortByText;
    private ImageButton btnSearch;
    public static Button btnRefresh, btnAd, btnProfileUpdate;
    private TabHost tabHost;
    private ExpandListAdapter expAdapter;
    private ArrayList<Group> expListItems;
    private ExpandableListView expandList, expandlistFavorite;
    private ListView listVote;
    private int lastExpandedPosition = -1;
    private ImageView kakaoProfile;
    private Bitmap kakaoThumbnail;
    private TextView kakaoNickname;
    private Spinner spinnerAge;
    private RadioGroup radioGroupSex;
    private RadioButton radioMale, radioFemale, radioNothing;
    private PrefManager prefManager;
    private ProgressDialog pd;
    public static JSONObject currentProportion;
    public static DBOpenHelper dbOpenHelper;

    /**
     * 서버로부터 받은 가게이름을 가나다순으로 정렬
     */
    public static Comparator<Group> comparatorByText = new Comparator<Group>() {
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(Group group1, Group group2) {
            return collator.compare(group1.getName(), group2.getName());
        }
    };


    /**
     * Group(가게이름 , 인구비율)을 인기도순으로 정렬
     */
    public static Comparator<Group> comparatorByPopular = new Comparator<Group>() {
        @Override
        public int compare(Group group1, Group group2) {
            return group1.getProportion() < group2.getProportion() ? 1 : group1.getProportion() >
                    group2.getProportion() ? -1 : 0;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFullAd();

        /*
        try {
            currentProportion = new threadVote(this).execute().get();
        }catch (Exception e){
            e.printStackTrace();
        }*/

        /** 앱을 실행한 후 처음으로 실행하는지 확인. 첫실행이면 튜토리얼화면 진행*/
        prefManager = new PrefManager(this);
        if (prefManager.isFirstTimeLaunch()) {
            Log.d(TAG, "It's first time!!");
            prefManager.setFirstTimeLaunch(false);
        }


        /** status bar 색상 변경 */
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorBlack));
        }


        tabHost = (TabHost) findViewById(R.id.footer);

        btnBob = (ToggleButton) findViewById(R.id.btn_category_bob);
        btnBob.setOnClickListener(listener_category);
        btnNoddle = (ToggleButton) findViewById(R.id.btn_category_noodle);
        btnNoddle.setOnClickListener(listener_category);
        btnCafe = (ToggleButton) findViewById(R.id.btn_category_cafe);
        btnCafe.setOnClickListener(listener_category);
        btnDrink = (ToggleButton) findViewById(R.id.btn_category_beer);
        btnDrink.setOnClickListener(listener_category);
        btnFastfood = (ToggleButton) findViewById(R.id.btn_category_fastfood);
        btnFastfood.setOnClickListener(listener_category);
        btnFork = (ToggleButton) findViewById(R.id.btn_category_fork);
        btnFork.setOnClickListener(listener_category);
        btnSearch = (ImageButton) findViewById(R.id.btn_search);
        btnSortByPopular = (ToggleButton) findViewById(R.id.btn_sortby_popular);
        btnSortByPopular.setOnClickListener(sort_listener);
        btnSortByPopular.setChecked(true);
        btnSortByText = (ToggleButton) findViewById(R.id.btn_sortby_text);
        btnSortByText.setOnClickListener(sort_listener);

        /*
        btnProfileUpdate = (Button) findViewById(R.id.btn_profile_update);
        spinnerAge = (Spinner) findViewById(R.id.spinner_age);
        radioGroupSex = (RadioGroup) findViewById(R.id.radioGroup_sex);
        radioMale = (RadioButton) findViewById(R.id.radioButton_male);
        radioFemale = (RadioButton) findViewById(R.id.radioButton_female);
        radioNothing = (RadioButton) findViewById(R.id.radioButton_nothing);
        */

        /** 디버깅용(버튼눌렀을때 광고 뜨도록)*/
        /*
        btnAd = (Button) findViewById(R.id.btn_ad);
        btnAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAD(MainActivity.this);
            }
        });
        */

        /** 사용자 정보 업데이트 버튼*/
        /*
        btnProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (radioGroupSex.getCheckedRadioButtonId()) {
                    case R.id.radioButton_female:
                        UserInfo.SEX = "female";
                        break;
                    case R.id.radioButton_male:
                        UserInfo.SEX = "male";
                        break;
                    default:
                        UserInfo.SEX = "";
                        break;
                }

                UserInfo.AGE = String.valueOf(spinnerAge.getSelectedItem());

                requestUpdateProfile(UserInfo.SEX, UserInfo.AGE);

            }
        });


        ArrayList<Integer> items = new ArrayList<Integer>();
        for (int age = 1; age <= 100; age++) {
            items.add(age);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, items);
        spinnerAge.setAdapter(adapter);
        */


        /** 사용자 정보 업데이트 칸에 사용자 정보 저장되어있으면 저장값 보여주기*/
        /*
        if (UserInfo.AGE != null)
            spinnerAge.setSelection(Integer.valueOf(UserInfo.AGE) - 1);

        if (UserInfo.SEX != null) {
            switch (UserInfo.SEX) {
                case "male":
                    radioMale.setChecked(true);
                    break;
                case "female":
                    radioFemale.setChecked(true);
                    break;
                default:
                    radioNothing.setChecked(true);
                    break;
            }
        }
        */


        /** 새로고침 버튼 */
        btnRefresh = (Button) findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    currentProportion = new threadVote(MainActivity.this).execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String category_name = "";

                if (btnBob.isChecked()) {
                    category_name = "bob";
                } else if (btnNoddle.isChecked()) {
                    category_name = "noddle";
                } else if (btnCafe.isChecked()) {
                    category_name = "cafe";
                } else if (btnDrink.isChecked()) {
                    category_name = "drink";
                } else if (btnFastfood.isChecked()) {
                    category_name = "fastfood";
                } else if (btnFork.isChecked()) {
                    category_name = "meat";
                }
                expListItems = setItems(category_name);

                if (btnSortByPopular.isChecked())
                    Collections.sort(expListItems, comparatorByPopular);
                else if (btnSortByText.isChecked())
                    Collections.sort(expListItems, comparatorByText);
                //정렬 눌린 버튼에 따라서 정렬해야함
                setExpandListAdapter(expListItems);
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


        ImageView tab_home_icon = new ImageView(this);
        tab_home_icon.setImageResource(R.drawable.selector_tab_home);
        ImageView tab_info_icon = new ImageView(this);
        tab_info_icon.setImageResource(R.drawable.selector_tab_info);
        ImageView tab_account_icon = new ImageView(this);
        tab_account_icon.setImageResource(R.drawable.selector_tab_account);
        ImageView tab_statistics_icon = new ImageView(this);
        tab_statistics_icon.setImageResource(R.drawable.selector_tab_statistics);


        /** 카카오 프로필사진 가져오기 */
        /*
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
        */

        /**
         * 내정보
         */
        /*
        try {
            getThumbnail.join(); //쓰레드가 끝나기전에 이미지설정을 하면 안되므로 join으로 기다리기
            kakaoProfile.setImageBitmap(kakaoThumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }

        kakaoNickname = (TextView) findViewById(R.id.kakao_nickname);
        kakaoNickname.setText(UserInfo.KAKAO_NICKNAME);
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

        expandList = (ExpandableListView) findViewById(R.id.list_main);
        expListItems = setItems("bob");
        Collections.sort(expListItems, comparatorByPopular);
        setExpandListAdapter(expListItems);


        /**
         * 내정보 탭
         */
        /*
        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

        listVote = (ListView)findViewById(R.id.list_vote);
        expandlistFavorite = (ExpandableListView)findViewById(R.id.list_favorite);
        */




        /** 메인 탭을 제외한 타머지탭 disable */
        tabHost.getTabWidget().getChildTabViewAt(1).setEnabled(false);
        tabHost.getTabWidget().getChildTabViewAt(2).setEnabled(false);
        //tabHost.getTabWidget().getChildTabViewAt(3).setEnabled(false);


    }


    Button.OnClickListener listener_category = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String category_name = null;
            switch (view.getId()) {
                case R.id.btn_category_bob:
                    category_name = "bob";
                    btnBob.setChecked(true);
                    btnNoddle.setChecked(false);
                    btnCafe.setChecked(false);
                    btnDrink.setChecked(false);
                    btnFastfood.setChecked(false);
                    btnFork.setChecked(false);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
                case R.id.btn_category_noodle:
                    category_name = "noddle";
                    btnBob.setChecked(false);
                    btnNoddle.setChecked(true);
                    btnCafe.setChecked(false);
                    btnDrink.setChecked(false);
                    btnFastfood.setChecked(false);
                    btnFork.setChecked(false);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
                case R.id.btn_category_fastfood:
                    category_name = "fastfood";
                    btnBob.setChecked(false);
                    btnNoddle.setChecked(false);
                    btnCafe.setChecked(false);
                    btnDrink.setChecked(false);
                    btnFastfood.setChecked(true);
                    btnFork.setChecked(false);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
                case R.id.btn_category_fork:
                    category_name = "meat";
                    btnBob.setChecked(false);
                    btnNoddle.setChecked(false);
                    btnCafe.setChecked(false);
                    btnDrink.setChecked(false);
                    btnFastfood.setChecked(false);
                    btnFork.setChecked(true);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
                case R.id.btn_category_cafe:
                    category_name = "cafe";
                    btnBob.setChecked(false);
                    btnNoddle.setChecked(false);
                    btnCafe.setChecked(true);
                    btnDrink.setChecked(false);
                    btnFastfood.setChecked(false);
                    btnFork.setChecked(false);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
                case R.id.btn_category_beer:
                    category_name = "drink";
                    btnBob.setChecked(false);
                    btnNoddle.setChecked(false);
                    btnCafe.setChecked(false);
                    btnDrink.setChecked(true);
                    btnFastfood.setChecked(false);
                    btnFork.setChecked(false);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
            }

            expListItems = setItems(category_name);
            Collections.sort(expListItems, comparatorByPopular);
            setExpandListAdapter(expListItems);
        }
    };

    Button.OnClickListener sort_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_sortby_popular:
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    Collections.sort(expListItems, comparatorByPopular);
                    setExpandListAdapter(expListItems);
                    break;

                case R.id.btn_sortby_text:
                    btnSortByPopular.setChecked(false);
                    btnSortByText.setChecked(true);
                    Collections.sort(expListItems, comparatorByText);
                    setExpandListAdapter(expListItems);
                    break;
            }
        }
    };

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

    public ArrayList<Group> setItems(String category) {
        try {
            JSONArray restaurantList = currentProportion.getJSONArray(category);
            ArrayList<Group> list_group = new ArrayList<Group>();

            if (restaurantList != null) {
                for (int i = 0; i < restaurantList.length(); i++) {
                    String group_name = ((JSONObject) restaurantList.get(i)).getString("title");
                    int proportion = ((JSONObject) restaurantList.get(i)).getInt("proportion");
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

    //현재 음식점 이름과 최근인구밀도를 서버로부터 받아 리턴
    public static class threadVote extends AsyncTask<String, Integer, JSONObject> {
        Context context;

        public threadVote(Context context) {
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... parm) {
            try {
                String time = StaticMethod.getTimeNow();

                URL url = new URL("http://" + serverIP + ":8000/lineup/current/?time=" + time);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = new JSONObject(getStringFromInputStream(in));

                return json;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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


}
