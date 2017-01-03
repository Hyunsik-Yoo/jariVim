package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.content.Intent;
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

public class MainActivity extends Activity {

    public static String server_IP = "lineup-server.cloudapp.net";
    Button btn_bob, btn_noddle, btn_cafe, btn_drink, btn_fastfood, btn_fork;
    ImageButton btn_search;
    private TabHost tabHost;
    private ExpandListAdapter ExpAdapter;
    private ArrayList<Group> ExpListItems;
    private ExpandableListView ExpandList;
    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //status bar 색상 변경
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorBlack));
        }


        tabHost = (TabHost)findViewById(R.id.footer);

        btn_bob = (Button)findViewById(R.id.btn_category_bob);
        btn_bob.setOnClickListener(listener_category);
        btn_noddle = (Button)findViewById(R.id.btn_category_noodle);
        btn_noddle.setOnClickListener(listener_category);
        btn_cafe = (Button)findViewById(R.id.btn_category_cafe);
        btn_cafe.setOnClickListener(listener_category);
        btn_drink = (Button)findViewById(R.id.btn_category_beer);
        btn_drink.setOnClickListener(listener_category);
        btn_fastfood = (Button)findViewById(R.id.btn_category_fastfood);
        btn_fastfood.setOnClickListener(listener_category);
        btn_fork = (Button)findViewById(R.id.btn_category_fork);
        btn_fork.setOnClickListener(listener_category);
        btn_search = (ImageButton)findViewById(R.id.btn_search);



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
        ExpAdapter = new ExpandListAdapter(MainActivity.this, ExpListItems);
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
            String category_name=null;
            switch (view.getId()){
                case R.id.btn_category_bob:
                    category_name = "bob";
                    break;
                case R.id.btn_category_noodle:
                    category_name = "noddle";
                    break;
                case R.id.btn_category_fastfood:
                    category_name = "fastfood";
                    break;
                case R.id.btn_category_fork:
                    category_name = "meat";
                    break;
                case R.id.btn_category_cafe:
                    category_name = "cafe";
                    break;
                case R.id.btn_category_beer:
                    category_name = "drink";
                    break;
            }

            ExpListItems = setItems(category_name);
            ExpAdapter = new ExpandListAdapter(MainActivity.this, ExpListItems);
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
    };


    public ArrayList<Group> setItems(String category) {
        JSONArray result=null;

        try {
            result = new Thread_vote().execute(category).get();
            Log.i("debug",result.toString());

            ArrayList<Group> list_group = new ArrayList<Group>();

            if (result != null) {
                for(int i=0; i<result.length(); i++){
                    String group_name = ((JSONObject)result.get(i)).getString("title");
                    int proportion = ((JSONObject)result.get(i)).getInt("proportion");
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
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    //카테고리별 음식점 이름과 최근인구밀도를 서버로부터 받아 리턴
    private class Thread_vote extends AsyncTask<String,Integer, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... parm) {
            try {
                String category = parm[0];
                URL url = new URL("http://"+server_IP+":8000/lineup/current/?category="+category);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = new JSONObject(getStringFromInputStream(in));
                JSONArray data = json.getJSONArray("data");

                return data;

            }catch (Exception e){
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


}
