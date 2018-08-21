package com.example.kyh.real.S_Message;

/**
 * Created by kyh on 2015. 2. 9..
 */
public class S_Message_List_data {

    private String Main_Title;
    private String Sub_Title;
    private String Message_Num;
    private String Course_Name;
    private String m_realcoursename;
    public S_Message_List_data(String _Course_Name, String _Main_Title, String _Sub_Title, String _Message_Num) {

        this.setCourse_Name(_Course_Name);
        this.setMain_Title(_Main_Title);
        this.setSub_Title(_Sub_Title);
        this.setMessage_Num(_Message_Num);
    }

    public String getCourse_Name() {
        return Course_Name;
    }

    public void setCourse_Name(String course_Name) {
        Course_Name = course_Name;
    }

    public String getMain_Title() {
        return Main_Title;
    }

    public void setMain_Title(String main_Title) {
        Main_Title = main_Title;
    }

    public String getSub_Title() {
        return Sub_Title;
    }


    public void setSub_Title(String sub_Title) {
        Sub_Title = sub_Title;
    }

    public String getMessage_Num() {
        return Message_Num;
    }

    public void setMessage_Num(String message_Num) {
        Message_Num = message_Num;
    }

    public String getReal(){
        return m_realcoursename;
    }
    public void setReal(String real){
        m_realcoursename = real;
    }
}