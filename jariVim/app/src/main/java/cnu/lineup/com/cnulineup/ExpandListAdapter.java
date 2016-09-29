package cnu.lineup.com.cnulineup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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

import static cnu.lineup.com.cnulineup.List_Activity.getStringFromInputStream;
import static cnu.lineup.com.cnulineup.List_Activity.server_IP;

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
        Group group = (Group)getGroup(groupPosition);
        final String title = group.getName();
        final ViewGroup parm_parent = parent;
        Child child = (Child)getChild(groupPosition,childPosition);
        if(convertView == null){
            //새로 child 레이아웃을 생성하는 듯!
            LayoutInflater infalInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = infalInflater.inflate(R.layout.child_item,null,false);
        }

        final SeekBar seekBar = (SeekBar)convertView.findViewById(R.id.seekBar);
        seekBar.setProgress(group.getProportion());

        Button btnConrifm = (Button)convertView.findViewById(R.id.btn_confirm);
        btnConrifm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int proportion = seekBar.getProgress();
                DialogSimple(view,title,String.valueOf(proportion));

            }
        });
        Button btnCancle = (Button)convertView.findViewById(R.id.btn_cancle);
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
        text_population.setText(group.getProportion()+"%");
        text_population.setTypeface(customFont);

        TextProgressBar progressBar = (TextProgressBar)convertView.findViewById(R.id.progressbar);
        progressBar.setProgress(group.getProportion());

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

    //카테고리별 음식점 이름과 최근인구밀도를 서버로부터 받아 리턴
    private class Thread_vote extends AsyncTask<String,Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... parm) {
            try {
                String title = URLEncoder.encode(parm[0],"utf-8");
                int proportion = Integer.parseInt(parm[1]);
                String url_str = "http://"+server_IP+":8000/lineup/voting/?title="+title+"&proportion="+proportion;
                URL url = new URL(url_str);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = getStringFromInputStream(in);

                if(result.substring(1,8).equals("Success"))
                    return true;
                else
                    return false;

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

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private void DialogSimple(final View view, String title, String proportion){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
        final View parm_view = view;
        final String parm_title = title;
        final String parm_proportion = proportion;
        alt_bld.setMessage("투표하시겠습니까?").setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            if(new Thread_vote().execute(parm_title, parm_proportion).get())
                            {

                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("확인");
        // Icon for AlertDialog
        alert.show();
    }


}
