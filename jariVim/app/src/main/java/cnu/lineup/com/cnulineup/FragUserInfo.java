package cnu.lineup.com.cnulineup;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by macgongmon on 5/27/17.
 */

public class FragUserInfo extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private ExpandableListView expandlistFavorite, expandListVote;
    private ExpandListAdapter expAdapter;

    private ToggleButton btnFavorite, btnVote;
    private ImageView kakaoProfile;
    private Bitmap kakaoThumbnail;

    private ProgressDialog pd;
    private LinearLayout frameFavorite, frameVote;

    private ArrayList<Group> expListItems;
    private int lastExpandedPosition = -1;

    public FragUserInfo()
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

        btnFavorite.setOnClickListener(userInfoListener);
        btnVote.setOnClickListener(userInfoListener);
        expandlistFavorite = (ExpandableListView) getActivity().findViewById(R.id.list_favorite);
        expandListVote = (ExpandableListView) getActivity().findViewById(R.id.list_vote);

        expListItems = setItemsFavorite();
        setExpandListAdapterFavorite(expListItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.tab_user_info, container, false);


        /** UI Mapping */
        btnFavorite = (ToggleButton)view.findViewById(R.id.btn_favorite);
        btnVote = (ToggleButton)view.findViewById(R.id.btn_vote_list);

        frameFavorite = (LinearLayout) view.findViewById(R.id.layout_favorite);
        frameVote = (LinearLayout) view.findViewById(R.id.layout_vote_list);

        /** 카카오 프로필사진 가져오기 */
        kakaoProfile = (ImageView) view.findViewById(R.id.kakao_profile);
        Thread getThumbnail = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(UserInfo.PROFILE_IMAGE_PATH);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    kakaoThumbnail = BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.run();
            }
        };
        getThumbnail.start();
        try {
            getThumbnail.join(); //쓰레드가 끝나기전에 이미지설정을 하면 안되므로 join으로 기다리기
            kakaoProfile.setImageBitmap(kakaoThumbnail);
        } catch (Exception e) {
            e.printStackTrace();
        }







        return view;
    }

    /**
     * 개인정보 탭에서 토글버튼 기능
     */
    Button.OnClickListener userInfoListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_favorite:
                    btnVote.setChecked(false);
                    btnFavorite.setChecked(true);
                    frameFavorite.setVisibility(LinearLayout.VISIBLE);
                    frameVote.setVisibility(LinearLayout.INVISIBLE);
                    expListItems = setItemsFavorite();
                    setExpandListAdapterFavorite(expListItems);
                    break;
                case R.id.btn_vote_list:
                    btnVote.setChecked(true);
                    btnFavorite.setChecked(false);
                    expListItems = setItemsVote();
                    setExpandListAdapterVote(expListItems);
                    frameFavorite.setVisibility(LinearLayout.INVISIBLE);
                    frameVote.setVisibility(LinearLayout.VISIBLE);

                    break;

            }
        }
    };

    public void setExpandListAdapterFavorite(ArrayList<Group> ExpListItems) {
        expAdapter = new ExpandListAdapter(FragUserInfo.this.getActivity(), ExpListItems, expandlistFavorite);
        expandlistFavorite.setAdapter(expAdapter);
        expandlistFavorite.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandlistFavorite.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }

    public void setExpandListAdapterVote(ArrayList<Group> ExpListItems) {
        expAdapter = new ExpandListAdapter(FragUserInfo.this.getActivity(), ExpListItems, expandListVote);
        expandListVote.setAdapter(expAdapter);
        expandListVote.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expandListVote.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }


    public ArrayList<Group> setItemsFavorite() {
        try {
            ArrayList<Group> list_group = new ArrayList<Group>();
            ArrayList<JSONArray> restaurantList = new ArrayList<>();
            restaurantList.add(MainActivity.currentProportion.getJSONArray("bob"));
            restaurantList.add(MainActivity.currentProportion.getJSONArray("noddle"));
            restaurantList.add(MainActivity.currentProportion.getJSONArray("cafe"));
            restaurantList.add(MainActivity.currentProportion.getJSONArray("drink"));
            restaurantList.add(MainActivity.currentProportion.getJSONArray("fastfood"));
            restaurantList.add(MainActivity.currentProportion.getJSONArray("meat"));

            List<String> favoriteRes = MainActivity.dbOpenHelper.getFavoriteRestaurant();


            Iterator<JSONArray> iterRestaurant = restaurantList.iterator();
            while(iterRestaurant.hasNext()){
                JSONArray restaurant = iterRestaurant.next();

                if (restaurant != null) {
                    for (int i = 0; i < restaurant.length(); i++) {
                        String group_name = ((JSONObject) restaurant.get(i)).getString("title");
                        int proportion = ((JSONObject) restaurant.get(i)).getInt("proportion");
                        if(!favoriteRes.contains(group_name)){
                            continue;
                        }
                        Log.d(TAG,group_name);
                        Group group = new Group();
                        group.setName(group_name);
                        group.setProportion(proportion);

                        Child child = new Child();
                        SeekBar seekBar = (SeekBar) getActivity().findViewById(R.id.seekBar);
                        Button btnConfirm = (Button) getActivity().findViewById(R.id.btn_confirm);
                        Button btnCancle = (Button) getActivity().findViewById(R.id.btn_cancle);
                        child.setSeekBar(seekBar);
                        child.setConfirm(btnConfirm);
                        child.setCancle(btnCancle);

                        group.setItems(child);
                        list_group.add(group);
                    }
                }
            }
            return list_group;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<Group> setItemsVote() {
        try {
            ArrayList<Group> list_group = new ArrayList<Group>();

            ArrayList<ArrayList<String>> voteRes = new ArrayList<>(MainActivity.dbOpenHelper.getVote());
            Iterator<ArrayList<String>> voteIter =voteRes.iterator();
            while(voteIter.hasNext()){
                ArrayList<String> votePair = voteIter.next();
                String group_name = votePair.get(0);
                int proportion = Integer.parseInt(votePair.get(1));

                Group group = new Group();
                group.setName(group_name);
                group.setProportion(proportion);
                Log.d(TAG,group_name + " : "+proportion);

                list_group.add(group);
            }
            return list_group;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
