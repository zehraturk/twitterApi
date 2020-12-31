package com.zeze.twitterApi;

import java.sql.Date;

public class DateParser {
    public static String parseDate(String timestamp){
        long timestamp_long = Long.parseLong(timestamp);
        Date date=new Date(timestamp_long);
        String str = date.toString();
        return str;
    }
}
