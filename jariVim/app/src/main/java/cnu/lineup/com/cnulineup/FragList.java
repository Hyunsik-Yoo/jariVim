package cnu.lineup.com.cnulineup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static cnu.lineup.com.cnulineup.MainActivity.dbOpenHelper;

/**
 * Created by macgongmon on 5/27/17.
 * 메인화면에서 카테고리별 음식점들의 인원 현황 Fragment를 만들기 위한 부모 클래스
 */

public class FragList extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    private ExpandListAdapter expAdapter;
    private ArrayList<Group> expListItems;
    private ExpandableListView expandList;
    private int lastExpandedPosition = -1;
    private ToggleButton btnSortByPopular;
    private ToggleButton btnSortByText;
    private String category;

    public FragList()
    {

    }

    public static FragList newInstance(String category) {
        FragList f = new FragList();
        Bundle args = new Bundle();
        args.putString("category",category);
        f.setArguments(args);
        return f;
    }

    private void readBundle(Bundle bundle) {
        if (bundle != null) {
            category = bundle.getString("category");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        /**
         * View 생성 이후에 인기도별, 이름별 정렬하는 버튼 정의
         */
        View view=inflater.inflate(R.layout.activity_frag_list, container, false);

        btnSortByPopular = (ToggleButton) view.findViewById(R.id.btn_sortby_popular);
        btnSortByPopular.setOnClickListener(sort_listener);
        btnSortByPopular.setChecked(true);
        btnSortByText = (ToggleButton) view.findViewById(R.id.btn_sortby_text);
        btnSortByText.setOnClickListener(sort_listener);

        Bundle bundle = getArguments();
        category = bundle.getString("category");

        expandList = (ExpandableListView) view.findViewById(R.id.list_main);
        expListItems = setItems(category);
        Collections.sort(expListItems, UtilMethod.comparatorByPopular);

        expAdapter = new ExpandListAdapter(this.getActivity(), expListItems, expandList);
        expandList.setAdapter(expAdapter);
        expandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandList.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });



        return view;
    }

    public ArrayList<Group> setItems(String category) {
        try {

            JSONArray restaurantList = MainActivity.currentProportion.getJSONArray(category);
            ArrayList<Group> list_group = new ArrayList<Group>();


            if (restaurantList != null) {
                for (int i = 0; i < restaurantList.length(); i++) {
                    final String group_name = ((JSONObject) restaurantList.get(i)).getString("title");
                    int proportion = ((JSONObject) restaurantList.get(i)).getInt("proportion");
                    Group group = new Group();
                    group.setName(group_name);
                    group.setProportion(proportion);
                    list_group.add(group);
                }
            }
            return list_group;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    Button.OnClickListener sort_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_sortby_popular:
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    Collections.sort(expListItems, UtilMethod.comparatorByPopular);
                    setExpandListAdapter(expListItems);
                    break;

                case R.id.btn_sortby_text:
                    btnSortByPopular.setChecked(false);
                    btnSortByText.setChecked(true);
                    Collections.sort(expListItems, UtilMethod.comparatorByText);
                    setExpandListAdapter(expListItems);
                    break;
            }
        }
    };

    public void setExpandListAdapter(ArrayList<Group> ExpListItems) {
        expAdapter = new ExpandListAdapter(this.getActivity(), ExpListItems, expandList);
        expandList.setAdapter(expAdapter);
        expandList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandList.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

}
