package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import java.util.List;

import static cnu.lineup.com.cnulineup.List_Activity.getStringFromInputStream;

public class SearchActivity extends Activity {

    private EditText edit_title;
    private ImageButton btn_search2;
    private ExpandListAdapter ExpAdapter;
    private ExpandableListView ExpandList;
    private ArrayList<Group> ExpListItems;
    private int lastExpandedPosition = -1;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ExpandList = (ExpandableListView)findViewById(R.id.list_search);
        ExpandList.setGroupIndicator(null);

        //status bar 색상 변경
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorOrange));
        }

        edit_title = (EditText) findViewById(R.id.edit_title);
        edit_title.getBackground().setColorFilter(getResources().getColor(R.color.colorOrange), PorterDuff.Mode.SRC_ATOP); //EditText 하단 바 색깔 변경


        btn_search2 = (ImageButton) findViewById(R.id.btn_search2);
        btn_search2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String parm_title = edit_title.getText().toString();
                ExpListItems = setItems(parm_title);
                ExpAdapter = new ExpandListAdapter(SearchActivity.this, ExpListItems);
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

                imm.hideSoftInputFromWindow(edit_title.getWindowToken(), 0);
            }
        });



        //키보드에서 확인버튼 눌렀을때
        edit_title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionid, KeyEvent keyEvent) {
                String parm_title = textView.getText().toString();
                ExpListItems = setItems(parm_title);
                ExpAdapter = new ExpandListAdapter(SearchActivity.this, ExpListItems);
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

                imm.hideSoftInputFromWindow(edit_title.getWindowToken(), 0);
                return true;
            }
        });

        //키보드 누를때마다 변화
        edit_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                /*String parm_title = editable.toString();
                ExpListItems = setItems(parm_title);

                if(ExpListItems != null) {
                    ExpAdapter = new ExpandListAdapter(SearchActivity.this, ExpListItems);
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
                }*/

            }
        });

        //폰트 변경
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/BMHANNA.ttf");
        edit_title.setTypeface(custom_font);

        //키보드 자동 오픈
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edit_title, 0);
    }


    private class Thread_search extends AsyncTask<String, Integer, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... parm) {
            try {
                String title = URLEncoder.encode(parm[0], "utf-8");
                URL url = new URL("http://" + List_Activity.server_IP + ":8000/lineup/search/?title=" + title);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = getStringFromInputStream(in);
                if (result.equals("404")) {
                    return null;
                }
                JSONObject json = new JSONObject(result);
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

    public ArrayList<Group> setItems(String category) {
        JSONArray result = null;

        try {
            result = new Thread_search().execute(category).get();


            ArrayList<Group> list_group = new ArrayList<Group>();

            if (result != null) {
                Log.i("debug", result.toString());
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
}
