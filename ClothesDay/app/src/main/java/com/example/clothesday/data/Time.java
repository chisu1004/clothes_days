package com.example.clothesday.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {

    public Time() {

    }

    public String getDayOfWeek(String date) throws ParseException {
        String dow = date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date nDate = dateFormat.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(nDate);

        int dayNum = cal.get(Calendar.DAY_OF_WEEK) ;

        switch(dayNum){
            case 1:
                dow = "일";
                break ;
            case 2:
                dow = "월";
                break ;
            case 3:
                dow = "화";
                break ;
            case 4:
                dow = "수";
                break ;
            case 5:
                dow = "목";
                break ;
            case 6:
                dow = "금";
                break ;
            case 7:
                dow = "토";
                break ;
             }

        return dow;
    }


}
