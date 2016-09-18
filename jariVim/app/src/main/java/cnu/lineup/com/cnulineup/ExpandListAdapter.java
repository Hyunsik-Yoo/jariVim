package cnu.lineup.com.cnulineup;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by macgongmon on 7/18/16.
 */

public class ExpandListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<Group> groups;
    private Typeface customFont;

    public ExpandListAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
        AssetManager temp = context.getAssets();
        this.customFont = Typeface.createFromAsset(context.getAssets(),"fonts/BMHANNA.ttf");
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        Child chList = groups.get(groupPosition).getItems();
        return chList;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        Child child = (Child)getChild(groupPosition,childPosition);
        if(convertView == null){
            //새로 child 레이아웃을 생성하는 듯!
            LayoutInflater infalInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = infalInflater.inflate(R.layout.child_item,null,false);
        }


        //SeekBar seekBar = (SeekBar)convertView.findViewById(R.id.seekBar);
        //Button btnConrifm = (Button)convertView.findViewById(R.id.btn_confirm);
        //Button btnCancle = (Button)convertView.findViewById(R.id.btn_cancle);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        Child chList = groups.get(groupPosition).getItems();
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount()
    {
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPoistion, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        Group group = (Group)getGroup(groupPoistion);
        if(convertView == null){
            LayoutInflater inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_item,null);
        }
        TextView text_shop_name = (TextView)convertView.findViewById(R.id.text_shop_name);
        text_shop_name.setText(group.getName());
        text_shop_name.setTypeface(customFont);

        TextView text_population = (TextView)convertView.findViewById(R.id.text_population);
        text_population.setText("퍼센트 나오는곳!");
        text_population.setTypeface(customFont);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
