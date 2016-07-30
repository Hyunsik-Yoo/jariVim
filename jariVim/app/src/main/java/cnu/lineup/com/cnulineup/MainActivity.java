package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private ExpandListAdapter ExpAdapter;
    private ArrayList<Group> ExpListItems;
    private ExpandableListView ExpandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExpandList = (ExpandableListView) findViewById(R.id.list_main);
        ExpListItems = setItems();
        ExpAdapter = new ExpandListAdapter(MainActivity.this, ExpListItems);
        ExpandList.setAdapter(ExpAdapter);
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
