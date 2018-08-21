package com.example.kyh.real.S_Message;

import android.widget.TextView;

/**
 * Created by kyh on 2015. 2. 9..
 */
public class S_Message_Info_List_data {


    private String m_s_m_info_content;
    private String m_s_m_info_date;
    private String r_w;
    public S_Message_Info_List_data(String _m_s_m_info_content, String _m_s_m_info_date) {


        this.setM_s_m_info_content(_m_s_m_info_content);
        this.setM_s_m_info_date(_m_s_m_info_date);
//        this.setR_w(r_w);
    }

    public String getM_s_m_info_content() {
        return m_s_m_info_content;
    }

    public void setM_s_m_info_content(String sMInfoContent) {
        m_s_m_info_content = sMInfoContent;
    }

    public String getM_s_m_info_date() {
        return m_s_m_info_date;
    }

    public void setM_s_m_info_date(String sMInfoDate) {
        m_s_m_info_date = sMInfoDate;
    }
//    public String getR_w(){
//        return r_w;
//    }
//    public void setR_w(String rw){
//        r_w = rw;
//    }
}