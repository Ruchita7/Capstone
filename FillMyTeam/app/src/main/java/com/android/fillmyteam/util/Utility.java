package com.android.fillmyteam.util;

import com.android.fillmyteam.R;
import com.android.fillmyteam.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 * Created by dgnc on 5/8/2016.
 */
public class Utility {
    public static final String months[] = {
            "Jan", "Feb", "Mar", "Apr",
            "May", "Jun", "Jul", "Aug",
            "Sep", "Oct", "Nov", "Dec"};

    public static final String timezone[] = {"AM", "PM"};

    public static String getCurrentDate(GregorianCalendar gcalendar) {
        String date = gcalendar.get(Calendar.DATE) + " " + months[gcalendar.get(Calendar.MONTH)] + " " + gcalendar.get(Calendar.YEAR);
        return date;
    }

    public static String getCurrentTime(GregorianCalendar gcalendar) {
        String time = gcalendar.get(Calendar.HOUR) + ":" + gcalendar.get(Calendar.MINUTE) + " " + timezone[gcalendar.get(Calendar.AM_PM)];
        return time;
    }

    public static String getPlayingDate(String inputDate, String inputTime) {
        Date date = null;
        String displayDate = null;
        try {
            String dateString = inputDate.replace(" ", "-");
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
            SimpleDateFormat parseFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            date = parseFormat.parse(dateString + " " + inputTime);
            displayDate = displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return displayDate;
    }

    public static String encodeEmail(String emailId) {
        return emailId.replace(".", ",");
    }


    public static String decodeEmail(String emailId) {
        return emailId.replace(",", ".");
    }

    public static User retrieveUserObject(Map<String, Object> userMap) {
        User user = new User();
        String key;
        for (Map.Entry<String, Object> userValues : userMap.entrySet()) {
            key = userValues.getKey();
            switch (key) {
                case Constants.NAME:
                    user.setName((String) userValues.getValue());
                    break;
                case Constants.EMAIL:
                    user.setEmail((String) userValues.getValue());
                    break;
                case Constants.LATITUDE:
                    user.setLatitude((Double) userValues.getValue());
                    break;
                case Constants.LONGITUDE:
                    user.setLongitude((Double) userValues.getValue());
                    break;

                case Constants.PLAYINGTIME:
                    user.setPlayingTime((String) userValues.getValue());
                    break;
                case Constants.SPORT:
                    user.setSport((String) userValues.getValue());
                    break;
            }
        }
        return user;
    }

    public static String getPlayingDateInfoMarker(String inputDate, String inputTime) {
        Date date = null;
        String displayDate = null;
        try {
            String dateString = inputDate.replace(" ", "-");
            SimpleDateFormat parseFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            date = parseFormat.parse(dateString + " " + inputTime);
            displayDate = displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return displayDate;
    }

    public static String getPlayingTimeInfo(String inputTime) {
        Date date = null;
        String displayDate = null;
        try {
            //   String dateString = inputDate.replace(" ","-");
            SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm:SS");
            SimpleDateFormat displayFormat = new SimpleDateFormat("hh:mm a");
            date = parseFormat.parse(inputTime);
            displayDate = displayFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return displayDate;
    }

    public static String[] retrieveHourMinute(String inputTime) {
        int[] timeArr;
        String[] playingTime = inputTime.split(" ");
        String timeInterval;
        String[] timeStamps = null;
        if (playingTime.length == 2) {
            timeInterval = playingTime[1];
            timeStamps = playingTime[0].split(":");
            if (timeStamps.length == 2) {
                if (timeInterval.equalsIgnoreCase("PM")) {
                    timeStamps[0] = String.valueOf(Integer.parseInt(timeStamps[0]) + 12);
                }
            }
        }
        return timeStamps;
    }

    public static String updateTime(int hourOfDay, int minute) {

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.set(Calendar.HOUR, hourOfDay);
        gregorianCalendar.set(Calendar.MINUTE, minute);
        String time;
        if (hourOfDay == 0) {
            time = "12:";
        } else {
            if (hourOfDay > 12) {
                time = (hourOfDay - 12) + ":";
            } else {
                time = hourOfDay + ":";
            }
        }
        if (minute < 10) {
            time += "0" + gregorianCalendar.get(Calendar.MINUTE);
        } else {
            time += gregorianCalendar.get(Calendar.MINUTE);
        }
        String playingTime;
        if (hourOfDay >= 12) {
            //     playingTime = time + " PM";
            playingTime = time + Constants.PM;

        } else {
            //    playingTime = time + " AM";
            playingTime = time + Constants.AM;
        }

        return playingTime;
        //  mUser.setPlayingTime(playingTime);
    }

    public static int retrieveSportsIcon(String sportName) {
        int sportDrawable = 0;
        switch (sportName) {

            case Constants.FOOTBALL:
                sportDrawable = R.drawable.ic_football;
                break;
            case Constants.CRICKET:
                sportDrawable = R.drawable.cricket;
                break;
            case Constants.BASKETBALL:
                sportDrawable = R.drawable.ic_basketball;
                break;
            case Constants.HOCKEY:
                sportDrawable = R.drawable.ic_hockey;
                break;
            case Constants.TENNIS:
                sportDrawable = R.drawable.ic_tennis;
                break;
            case Constants.BADMINTON:
                sportDrawable = R.drawable.ic_badminton;
                break;
            case Constants.BASE_BALL:
            case Constants.BASEBALL :
                sportDrawable = R.drawable.ic_baseball;
                break;
            case Constants.RUGBY:
                sportDrawable = R.drawable.ic_rugby;
                break;
            case Constants.VOLLEYBALL:
            case Constants.VOLLEY_BALL:
                sportDrawable = R.drawable.ic_volleyball;
                break;
            case Constants.TABLETENNIS:
            case Constants.TABLE_TENNIS:
                sportDrawable = R.drawable.ic_table_tennis;
                break;
        }
        return sportDrawable;
    }

}
