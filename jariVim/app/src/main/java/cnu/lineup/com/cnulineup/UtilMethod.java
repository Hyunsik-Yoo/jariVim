package cnu.lineup.com.cnulineup;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import static cnu.lineup.com.cnulineup.MainActivity.currentProportion;
import static cnu.lineup.com.cnulineup.MainActivity.dbOpenHelper;

/**
 * Created by macgongmon on 6/11/17.
 */

public class UtilMethod {

    public static Comparator<Group> comparatorByText = new Comparator<Group>() {
        /**
         * 서버로부터 받은 가게이름을 가나다순으로 정렬
         */
        private final Collator collator = Collator.getInstance();

        @Override
        public int compare(Group group1, Group group2) {
            return collator.compare(group1.getName(), group2.getName());
        }
    };


    public static Comparator<Group> comparatorByPopular = new Comparator<Group>() {
        @Override
        public int compare(Group group1, Group group2) {
            /**
             * Group(가게이름 , 인구비율)을 인기도순으로 정렬
             */
            return group1.getProportion() < group2.getProportion() ? 1 : group1.getProportion() >
                    group2.getProportion() ? -1 : 0;
        }
    };

    //서버에서 정보를 받는 과정
    public static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }


    public static class threadVote extends AsyncTask<String, Integer, JSONObject> {
        /**
         * 현재 시간에 해당하는 음식점 전체의 예측인구밀도를 반환해준다.
         */
        Context context;

        public threadVote(Context context) {
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... parm) {
            try {
                String time = UtilMethod.getTimeNow();

                URL url = new URL("http://" + MainActivity.serverIP + ":8000/lineup/current/?time=" + time);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = new JSONObject(UtilMethod.getStringFromInputStream(in));

                return json;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static class threadRestaurant extends AsyncTask<String, Integer, JSONObject>{
        Context context;

        public threadRestaurant(Context context){
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                URL url = new URL("http://" + MainActivity.serverIP + ":8000/lineup/restaurnats/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = new JSONObject(UtilMethod.getStringFromInputStream(in));

                return json;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public static ArrayList<Group> setItemsFavorite() {
        try {
            ArrayList<Group> list_group = new ArrayList<Group>();
            ArrayList<JSONArray> restaurantList = new ArrayList<>();
            restaurantList.add(currentProportion.getJSONArray("bob"));
            restaurantList.add(currentProportion.getJSONArray("noodle"));
            restaurantList.add(currentProportion.getJSONArray("cafe"));
            restaurantList.add(currentProportion.getJSONArray("drink"));
            restaurantList.add(currentProportion.getJSONArray("fastfood"));
            restaurantList.add(currentProportion.getJSONArray("meat"));

            List<String> favoriteRes = dbOpenHelper.getFavoriteRestaurant();


            Iterator<JSONArray> iterRestaurant = restaurantList.iterator();
            while (iterRestaurant.hasNext()) {
                JSONArray restaurant = iterRestaurant.next();

                if (restaurant != null) {
                    for (int i = 0; i < restaurant.length(); i++) {
                        String group_name = ((JSONObject) restaurant.get(i)).getString("title");
                        int proportion = ((JSONObject) restaurant.get(i)).getInt("proportion");
                        // 즐겨찾기 리스트에 없으면 Pass
                        if (!favoriteRes.contains(group_name)) {
                            continue;
                        }

                        Group group = new Group();
                        group.setName(group_name);
                        group.setProportion(proportion);
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

    // 내가 투표한 곳을 보여주네?
    public static ArrayList<Group> setItemsVote() {
        try {
            ArrayList<Group> list_group = new ArrayList<Group>();

            ArrayList<ArrayList<String>> voteRes = new ArrayList<>(dbOpenHelper.getVote());
            Iterator<ArrayList<String>> voteIter = voteRes.iterator();
            while (voteIter.hasNext()) {
                ArrayList<String> votePair = voteIter.next();
                String group_name = votePair.get(0);
                int proportion = Integer.parseInt(votePair.get(1));

                Group group = new Group();
                group.setName(group_name);
                group.setProportion(proportion);
                list_group.add(group);
            }
            return list_group;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 현재시간을 ISO format형태로 반환해줌
     * yyyy-MM-ddTHH:mmZ format
     * @return ISO format의 날짜 문자열
     */
    public static String getTimeNow(){
        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());

        return nowAsISO;
    }




}
