package cnu.lineup.com.cnulineup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by macgongmon on 1/26/17.
 */

public class StaticMethod {
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
