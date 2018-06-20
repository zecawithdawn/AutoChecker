package com.example.kyh.real.S_F;

/**
 * Created by park on 2015. 2. 24..
 */
public class S_F_Home_CourseInfo {
    public S_F_Home_CourseInfo next;
    public S_F_Home_CourseInfo prev;
    private String courseName;
    private int startTime;
    private int endTime;
    private String beaconId;
    private String courseId;
    private String groupId;
    private int timeslotId;

    public S_F_Home_CourseInfo (String courseName, int startTime, int endTime, String beaconId, String courseId, String groupId, int timeslotId) {
        this.courseName = courseName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.beaconId = beaconId;
        this.courseId = courseId;
        this.groupId = groupId;
        this.timeslotId = timeslotId;
    }

    public String getCourseName () {
        return courseName;
    }

    public int getStartTime () {
        return startTime;
    }

    public int getEndTime () {
        return endTime;
    }

    public String getBeaconId () { return beaconId; }

    public String getCourseId() { return courseId; }

    public String getGroupId() { return groupId; }

    public int getTimeslotId() { return timeslotId; }
}
