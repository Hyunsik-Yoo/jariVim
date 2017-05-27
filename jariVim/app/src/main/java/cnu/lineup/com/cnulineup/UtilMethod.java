package cnu.lineup.com.cnulineup;

import android.content.Context;
import android.os.AsyncTask;

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
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by macgongmon on 1/26/17.
 */

public class UtilMethod {
    public static String serverIP = "168.188.127.132";

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


    public static String getStringFromInputStream(InputStream is) {
        /**
         * 서버에서 받은 정보를 String으로 변환해주는 함수
         */
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


    public static class getAllPopulation extends AsyncTask<String, Integer, JSONObject> {
        /**
         * 전체 음식점과 음식점들에 대한 현재시간의 인구예측결과를 받아온다.
         */
        Context context;

        public getAllPopulation(Context context) {
            this.context = context;
        }

        @Override
        protected JSONObject doInBackground(String... parm) {
            try {
                String time = UtilMethod.getTimeNow();

                URL url = new URL("http://" + serverIP + ":8000/lineup/current/?time=" + time);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject json = new JSONObject(getStringFromInputStream(in));

                return json;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }


}
