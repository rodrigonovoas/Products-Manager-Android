package app.rodrigonovoa.myproductsmanager.common;

import android.text.format.DateFormat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utils {

    private static Utils utils_instance = null;

    public static Utils getInstance()
    {
        if (utils_instance == null)
            utils_instance = new Utils();

        return utils_instance;
    }

    public Long currentDateToTimestamp(){
        Long timestamp_date = System.currentTimeMillis()/1000;

        return timestamp_date;
    }

    public Long fromDateToTimestamp(String date){
        Timestamp timestamp = null;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date parsedDate = dateFormat.parse(date);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
        } catch(Exception e) { //this generic but you can control another types of exception
            // look the origin of excption
        }

        return timestamp.getTime();
    }

    public String fromTimestampToDateString(Long time){
        String date = "";

        date = getDate(time);

        return date;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}
