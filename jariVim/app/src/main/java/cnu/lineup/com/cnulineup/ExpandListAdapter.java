package cnu.lineup.com.cnulineup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.util.ArrayList;

/**
 * Created by macgongmon on 7/18/16.
 */

public class ExpandListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<Group> groups;

    public ExpandListAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
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
            LayoutInflater infalInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.child_item,null);
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
            LayoutInflater inf = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inf.inflate(R.layout.group_item,null);

        }
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
