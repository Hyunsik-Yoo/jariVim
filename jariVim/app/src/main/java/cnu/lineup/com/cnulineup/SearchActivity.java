package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static cnu.lineup.com.cnulineup.List_Activity.getStringFromInputStream;

public class SearchActivity extends Activity {
    private String TAG = this.getClass().getSimpleName();

    //private EditText edit_title;
    private ImageButton btn_search2;
    private ExpandListAdapter ExpAdapter;
    private ExpandableListView ExpandList;
    private Map<String,Integer> itemList;
    private ArrayList<Group> ExpListItems;
    private int lastExpandedPosition = -1;
    private InputMethodManager imm;
    private AutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //폰트 변경
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/BMHANNA.ttf");

        // 리스트 처음에 빈값으로 초기화
        ExpandList = (ExpandableListView)findViewById(R.id.list_search);
        ExpandList.setGroupIndicator(null);

        //status bar 색상 변경
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.mainColor));
        }


        itemList = setallItems();
        autoCompleteTextView = (AutoCompleteTextView)findViewById(R.id.edit_title);
        ArrayAdapter<String> autoCompleteAdapter = new ArrayAdapter<String>(
                this,android.R.layout.simple_dropdown_item_1line,
                itemList.keySet().toArray(new String[itemList.keySet().size()])){

            Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/BMHANNA.ttf");

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView)v).setTypeface(custom_font);
                return super.getView(position, convertView, parent);
            }
        };

        autoCompleteTextView.setAdapter(autoCompleteAdapter);



        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteTextView.showDropDown();
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String title = autoCompleteTextView.getText().toString();
                ExpListItems = getSearchResult(title);

                if(ExpListItems != null) {
                    Collections.sort(ExpListItems, MainActivity.comparatorByPopular);
                    ExpAdapter = new ExpandListAdapter(SearchActivity.this, ExpListItems, ExpandList);
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

                    imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                }

            }
        });

        autoCompleteTextView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String title = autoCompleteTextView.getText().toString();
                ExpListItems = getSearchResult(title);

                if(ExpListItems != null) {
                    Collections.sort(ExpListItems, MainActivity.comparatorByPopular);
                    ExpAdapter = new ExpandListAdapter(SearchActivity.this, ExpListItems, ExpandList);
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

                    imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        autoCompleteTextView.getBackground().setColorFilter(getResources().getColor(R.color.mainColor),
                PorterDuff.Mode.SRC_ATOP);


        autoCompleteTextView.setTypeface(custom_font);

        //키보드 자동 오픈
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(autoCompleteTextView, 0);
    }

    public ArrayList<Group> getSearchResult(String parm_title){
        ArrayList<Group> list_group = new ArrayList<Group>();

        if(!itemList.containsKey(parm_title))
            return null;

        Iterator<String> itRest = itemList.keySet().iterator();
        while(itRest.hasNext()){
            String restaurantName = itRest.next();
            if(restaurantName.contains(parm_title)){
                int proportion = (int)itemList.get(restaurantName);
                Group group = new Group();
                group.setName(restaurantName);
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
    }


    public Map<String,Integer> setallItems() {

        Map<String,Integer> restMap = new HashMap<String,Integer>();

        try {
            Iterator<String> keys = MainActivity.currentProportion.keys();
            while(keys.hasNext()){
                JSONArray restaurantList = MainActivity.currentProportion.getJSONArray(keys.next());

                for(int i=0; i<restaurantList.length() ; i++){
                    String restName = ((JSONObject) restaurantList.get(i)).getString("title");
                    int proportion = ((JSONObject) restaurantList.get(i)).getInt("proportion");
                    restMap.put(restName, proportion);
                }
            }
            return restMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
