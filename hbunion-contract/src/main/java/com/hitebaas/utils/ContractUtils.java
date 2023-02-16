package com.hitebaas.utils;


import java.text.SimpleDateFormat;
import java.util.Date;

public class ContractUtils {

    
    public static final String DATE_FORMAT_1 = "yyyy-MM-dd HH:mm:ss";

    public static String formatNowToYMDHMS() throws Exception {
    	 String timeInfo = "[ " + new SimpleDateFormat(DATE_FORMAT_1).format(new Date()) + " ]  ";
        return timeInfo;
    }
    
    
}