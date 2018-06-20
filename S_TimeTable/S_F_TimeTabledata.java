package com.example.kyh.real.S_TimeTable;

/**
 * Created by choigwanggyu on 2015. 2. 2..
 */
public class S_F_TimeTabledata {
    String class_name;
    String class_room;
    int class_stime;
    int class_etime;
    String type;

        public S_F_TimeTabledata(String name,String room,int start_time,int end_time,String type ){
            this.class_name=name;
            this.class_room=room;
            this.class_stime=start_time;
            this.class_etime=end_time;
            this.type=type;
        }

}
