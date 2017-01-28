package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
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

public class List_Activity extends Activity {
    public static String server_IP = "222.239.249.114";
    private ExpandListAdapter ExpAdapter;
    private ArrayList<Group> ExpListItems;
    private ExpandableListView ExpandList;
    private TextView text_user_info;
    private ImageButton btn_back;
    private Intent intent_main;
    private TabHost tabHost;
    private int lastExpandedPosition = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //status bar 색상 변경
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorOrange));
        }

        //List_Activity의 상단 카테고리 이미지 설정
        intent_main = getIntent();
        String parm_category = intent_main.getStringExtra("category");



        /*
        text_user_info = (TextView)findViewById(R.id.text_user_info);
        Typeface custom_font = Typeface.createFromAsset(getAssets(),"fonts/BMHANNA.ttf");
        text_user_info.setTypeface(custom_font);

        btn_back = (ImageButton)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List_Activity.super.onBackPressed();
            }
        });

        ExpandList = (ExpandableListView) findViewById(R.id.list_main);
        ExpListItems = setItems(parm_category);
        ExpAdapter = new ExpandListAdapter(List_Activity.this, ExpListItems);
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
        */



        //ExpandList.setDivider(null);

        /*tabHost = (TabHost)findViewById(R.id.footer);
        tabHost.setup();

        TabHost.TabSpec tab1 = tabHost.newTabSpec("Tab1").setContent(R.id.tab1)
                .setIndicator("Tab1");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Tab2").setContent(R.id.tab2)
                .setIndicator("Tab2");
        TabHost.TabSpec tab3 = tabHost.newTabSpec("Tab3").setContent(R.id.tab3)
                .setIndicator("Tab3");
        TabHost.TabSpec tab4 = tabHost.newTabSpec("Tab4").setContent(R.id.tab4)
                .setIndicator("Tab4");

        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        tabHost.addTab(tab3);
        tabHost.addTab(tab4);*/

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ExpandList.setIndicatorBounds(ExpandList.getRight()-40,ExpandList.getWidth());


    }

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

    @Override
    protected void onResume() {
        super.onResume();
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


    //카테고리별 음식점 이름과 최근인구밀도를 서버로부터 받아 리턴
    private class Thread_vote extends AsyncTask<String,Integer, JSONArray>{
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


}
