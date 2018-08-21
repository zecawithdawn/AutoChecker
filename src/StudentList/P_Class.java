package com.example.kyh.real.StudentList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by park on 2015. 2. 6..
 */
public class P_Class {
    private String course_name;
    private int start_time;
    private int end_time;
    private String course_id;
    private String group_id;
    private String timeslot_id;
    private int current_time;

    public P_Class(String course_name, int start_time, int end_time, String course_id, String group_id, String timeslot_id) {
        this.course_name = course_name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.course_id = course_id;
        this.group_id = group_id;
        this.timeslot_id = timeslot_id;
    }

    public String getCourse_name () { return course_name; }

    public int getStart_time () {
        return start_time;
    }

    public int getEnd_time () { return end_time; }

    public String getCourse_id () { return course_id; }

    public String getGroup_id() {
        return group_id;
    }

    public String getTimeslot_id() { return timeslot_id; }
}
