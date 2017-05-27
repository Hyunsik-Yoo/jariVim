package cnu.lineup.com.cnulineup;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by macgongmon on 5/27/17.
 */

public class FragMain extends Fragment {

    private ToggleButton btnBob, btnNoddle, btnCafe, btnDrink, btnFastfood, btnFork, btnSortByPopular,
            btnSortByText;
    public static Button btnRefresh;


    private ExpandListAdapter expAdapter;
    private ArrayList<Group> expListItems;
    private ExpandableListView expandList;
    private int lastExpandedPosition = -1;

    public FragMain()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    MainActivity.currentProportion = new UtilMethod.getAllPopulation(FragMain.this.getActivity()).execute().get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String category_name = "";

                if (btnBob.isChecked()) {
                    category_name = "bob";
                } else if (btnNoddle.isChecked()) {
                    category_name = "noddle";
                } else if (btnCafe.isChecked()) {
                    category_name = "cafe";
                } else if (btnDrink.isChecked()) {
                    category_name = "drink";
                } else if (btnFastfood.isChecked()) {
                    category_name = "fastfood";
                } else if (btnFork.isChecked()) {
                    category_name = "meat";
                }
                expListItems = setItems(category_name);

                if (btnSortByPopular.isChecked())
                    Collections.sort(expListItems, UtilMethod.comparatorByPopular);
                else if (btnSortByText.isChecked())
                    Collections.sort(expListItems, UtilMethod.comparatorByText);
                //정렬 눌린 버튼에 따라서 정렬해야함
                setExpandListAdapter(expListItems);
                Toast.makeText(FragMain.this.getActivity(), "데이터 새로고침", Toast.LENGTH_SHORT).show();
            }
        });

        expandList = (ExpandableListView) getActivity().findViewById(R.id.list_main);
        expListItems = setItems("bob");
        Collections.sort(expListItems, UtilMethod.comparatorByPopular);
        setExpandListAdapter(expListItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.tab_main, container, false);
        //LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.tab_main, container, false);

        btnBob = (ToggleButton) view.findViewById(R.id.btn_category_bob);
        btnBob.setOnClickListener(listener_category);
        btnNoddle = (ToggleButton) view.findViewById(R.id.btn_category_noodle);
        btnNoddle.setOnClickListener(listener_category);
        btnCafe = (ToggleButton) view.findViewById(R.id.btn_category_cafe);
        btnCafe.setOnClickListener(listener_category);
        btnDrink = (ToggleButton) view.findViewById(R.id.btn_category_beer);
        btnDrink.setOnClickListener(listener_category);
        btnFastfood = (ToggleButton) view.findViewById(R.id.btn_category_fastfood);
        btnFastfood.setOnClickListener(listener_category);
        btnFork = (ToggleButton) view.findViewById(R.id.btn_category_fork);
        btnFork.setOnClickListener(listener_category);

        btnSortByPopular = (ToggleButton) view.findViewById(R.id.btn_sortby_popular);
        btnSortByPopular.setOnClickListener(sort_listener);
        btnSortByPopular.setChecked(true);
        btnSortByText = (ToggleButton) view.findViewById(R.id.btn_sortby_text);
        btnSortByText.setOnClickListener(sort_listener);




        /** 새로고침 버튼 */
        btnRefresh = (Button) view.findViewById(R.id.btn_refresh);

        return view;
    }

    Button.OnClickListener listener_category = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String category_name = null;
            switch (view.getId()) {
                case R.id.btn_category_bob:
                    category_name = "bob";
                    btnBob.setChecked(true);
                    btnNoddle.setChecked(false);
                    btnCafe.setChecked(false);
                    btnDrink.setChecked(false);
                    btnFastfood.setChecked(false);
                    btnFork.setChecked(false);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
                case R.id.btn_category_noodle:
                    category_name = "noddle";
                    btnBob.setChecked(false);
                    btnNoddle.setChecked(true);
                    btnCafe.setChecked(false);
                    btnDrink.setChecked(false);
                    btnFastfood.setChecked(false);
                    btnFork.setChecked(false);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
                case R.id.btn_category_fastfood:
                    category_name = "fastfood";
                    btnBob.setChecked(false);
                    btnNoddle.setChecked(false);
                    btnCafe.setChecked(false);
                    btnDrink.setChecked(false);
                    btnFastfood.setChecked(true);
                    btnFork.setChecked(false);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
                case R.id.btn_category_fork:
                    category_name = "meat";
                    btnBob.setChecked(false);
                    btnNoddle.setChecked(false);
                    btnCafe.setChecked(false);
                    btnDrink.setChecked(false);
                    btnFastfood.setChecked(false);
                    btnFork.setChecked(true);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
                case R.id.btn_category_cafe:
                    category_name = "cafe";
                    btnBob.setChecked(false);
                    btnNoddle.setChecked(false);
                    btnCafe.setChecked(true);
                    btnDrink.setChecked(false);
                    btnFastfood.setChecked(false);
                    btnFork.setChecked(false);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
                case R.id.btn_category_beer:
                    category_name = "drink";
                    btnBob.setChecked(false);
                    btnNoddle.setChecked(false);
                    btnCafe.setChecked(false);
                    btnDrink.setChecked(true);
                    btnFastfood.setChecked(false);
                    btnFork.setChecked(false);
                    btnSortByPopular.setChecked(true);
                    btnSortByText.setChecked(false);
                    break;
            }

            expListItems = setItems(category_name);
            Collections.sort(expListItems, UtilMethod.comparatorByPopular);
            setExpandListAdapter(expListItems);
        }
    };

    public ArrayList<Group> setItems(String category) {
        try {
            JSONArray restaurantList = MainActivity.currentProportion.getJSONArray(category);
            ArrayList<Group> list_group = new ArrayList<Group>();

            if (restaurantList != null) {
                for (int i = 0; i < restaurantList.length(); i++) {
                    String group_name = ((JSONObject) restaurantList.get(i)).getString("title");
                    int proportion = ((JSONObject) restaurantList.get(i)).getInt("proportion");
                    Group group = new Group();
                    group.setName(group_name);
                    group.setProportion(proportion);

                    Child child = new Child();

                    SeekBar seekBar = (SeekBar) FragMain.this.getActivity().findViewById(R.id.seekBar);
                    Button btnConfirm = (Button) getView().findViewById(R.id.btn_confirm);
                    Button btnCancle = (Button) getView().findViewById(R.id.btn_cancle);
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
