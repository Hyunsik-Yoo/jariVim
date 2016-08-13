package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.TabHost;

import java.util.ArrayList;

public class MainActivity extends Activity {
    private ExpandListAdapter ExpAdapter;
    private ArrayList<Group> ExpListItems;
    private ExpandableListView ExpandList;

    private TabHost tabHost;
    private int lastExpandedPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ExpandList = (ExpandableListView) findViewById(R.id.list_main);
        ExpListItems = setItems();
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
        //ExpandList.setDivider(null);

        tabHost = (TabHost)findViewById(R.id.footer);
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
        tabHost.addTab(tab4);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ExpandList.setIndicatorBounds(ExpandList.getRight()-40,ExpandList.getWidth());


    }

    public ArrayList<Group> setItems() {
        String[] names = {"a", "b", "c"};
        String vote = "투표하기";

        ArrayList<Group> list = new ArrayList<Group>();

        ArrayList<Child> chList;

        int size = 3;
        int j = 0;

        for (String groupName : names) {
            Group gru = new Group();
            gru.setName(groupName);

            Child ch = new Child();
            SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
            Button btnConfirm = (Button) findViewById(R.id.btn_confirm);
            Button btnCancle = (Button) findViewById(R.id.btn_cancle);
            ch.setSeekBar(seekBar);
            ch.setConfirm(btnConfirm);
            ch.setCancle(btnCancle);

            gru.setItems(ch);
            list.add(gru);
        }

        return list;
    }

}
