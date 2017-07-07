package cnu.lineup.com.cnulineup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static cnu.lineup.com.cnulineup.MainActivity.dbOpenHelper;


/**
 * Created by macgongmon on 7/18/16.
 */

public class ExpandListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<Group> groups;
    private Typeface customFont;
    private ExpandableListView expandableListView;

    public ExpandListAdapter(Context context, ArrayList<Group> groups, ExpandableListView expandableListView) {
        this.context = context;
        this.groups = groups;
        this.customFont = Typeface.createFromAsset(context.getAssets(),"fonts/BMHANNA.ttf");
        this.expandableListView = expandableListView;
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

    /**
     * 가게 이름 눌렀을 때 발생하는 Child 레이아웃
     * 즐겨찾기 리스트를 불러와서 리스트와 동일한 가게가 존재하면 즐겨찾기 버튼은 눌린상태로 로드
     * @param groupPosition
     * @param childPosition
     * @param isLastChild
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getChildView(final int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, final ViewGroup parent) {

        // 즐겨찾기 리스트 가져오기
        List<String> favoriteList = dbOpenHelper.getFavoriteRestaurant();

        Group group = (Group)getGroup(groupPosition);
        final String title = group.getName();
        final ViewGroup parm_parent = parent;
        Child child = (Child)getChild(groupPosition,childPosition);
        if(convertView == null){
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
                DialogSimple(view,title,String.valueOf(proportion), groupPosition);

            }
        });
        Button btnCancle = (Button)convertView.findViewById(R.id.btn_cancle);
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableListView.collapseGroup(groupPosition);
            }
        });


        // 즐겨찾기 기능 동작하도록 체크하면 Local DB에 저장, 취소하면 삭제
        ToggleButton btnFavo = (ToggleButton)convertView.findViewById(R.id.btn_favo);
        btnFavo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    dbOpenHelper.insertFavoriteRestaurant(title);
                }
                else{
                    dbOpenHelper.deleteFavorite(title);
                }
            }
        });

        // 즐겨찾기 리스트에 포함되어 있으면 체크된 상태로 표시
        if(favoriteList.contains(title))
            btnFavo.setChecked(true);
        else
            btnFavo.setChecked(false);


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

        ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressbar);
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

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    private void DialogSimple(final View view, String title, String proportion, int groupPosition){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(view.getContext());
        final View parm_view = view;
        final String parm_title = title;
        final String parm_proportion = proportion;
        final int parm_groupPosition = groupPosition;

        alt_bld.setMessage("투표하시겠습니까?").setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            if(new threadVote().execute(parm_title, parm_proportion).get())
                            {
                                expandableListView.collapseGroup(parm_groupPosition);
                                // 내가 투표한곳 보여주기위해 코딩중
                                dbOpenHelper.insertVote(parm_title,parm_proportion);
                                //MainActivity.displayAD(context);
                            }

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("확인");
        // Icon for AlertDialog
        alert.show();
    }

    private class threadVote extends AsyncTask<String,Integer, Boolean> {
        @Override
        protected Boolean doInBackground(String... parm) {
            try {
                String title = URLEncoder.encode(parm[0],"utf-8");
                int proportion = Integer.parseInt(parm[1]);
                String time = UtilMethod.getTimeNow();
                String url_str = "http://"+MainActivity.serverIP+":8000/lineup/voting/?title="+title
                        +"&proportion="+proportion+"&time=" + time;
                Log.d("test",url_str);
                URL url = new URL(url_str);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                String result = UtilMethod.getStringFromInputStream(in);

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

}
