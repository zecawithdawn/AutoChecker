package com.example.kyh.real.S_Message;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.kyh.real.R;

import java.util.List;

/**
 * Created by kyh on 2015. 2. 7..
 */
public class S_Message_Adapter extends ArrayAdapter<S_Message_List_data> {
    private Context m_Context = null;
    Typeface tf;

    public S_Message_Adapter(Context context, int resource, List<S_Message_List_data> objects) {
        super(context, resource, objects);
        this.m_Context =context;
        tf = Typeface.createFromAsset(context.getAssets(), "KoPubDotumMedium.ttf");


    }

//    public S_Message_Adapter(S_F_Message context, int textViewResourceId, ArrayList<S_Message_List_data> items) {
//        super(context, textViewResourceId, items);
//        this.m_Context = context;
//    }

    @Override
    public View getView(int nPosition, View convertView, ViewGroup parent) {
        // 뷰를 재사용 하기 위해 필요한 클래스
        PointerView pView = null;

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(m_Context).inflate(R.layout.s_message_list, null);
            pView = new PointerView(view);
            view.setTag(pView);
        }

        pView = (PointerView) view.getTag();

        // 데이터 클래스에서 해당 리스트목록의 데이터를 가져온다.
        S_Message_List_data custom_list_data = getItem(nPosition);

        if (custom_list_data != null) {
            // 현재 item의 position에 맞는 이미지와 글을 넣어준다.
            pView.GetCourseView().setText(custom_list_data.getCourse_Name());
            pView.GetTitleView().setText(custom_list_data.getMain_Title());
            pView.GetSubTitleView().setText(custom_list_data.getSub_Title());
            pView.GetMessageNum().setText(custom_list_data.getMessage_Num());

        }

        return view;
    }



    /**
     * 뷰를 재사용 하기위해 필요한 클래스
     * 클래스 자체를 view tag로 저장/불러오므로 재사용가능
     */
    private class PointerView {
        private View m_BaseView = null;

        private TextView m_tvTitle = null;
        private TextView m_tvSubTitle = null;
        private TextView m_coursename = null;
        private TextView m_messagenum = null;
        private String m_realcoursename = null;

        public PointerView(View BaseView) {
            this.m_BaseView = BaseView;
        }


        public TextView GetCourseView() {
            if (m_coursename == null) {

                m_coursename = (TextView) m_BaseView.findViewById(R.id.s_m_course_name);
                m_coursename.setTypeface(tf);
            }
            return m_coursename;
        }

        public TextView GetMessageNum() {
            if (m_messagenum == null) {
                m_messagenum = (TextView) m_BaseView.findViewById(R.id.s_message_number);
                m_messagenum.setTypeface(tf);
            }
            return m_messagenum;
        }

        public TextView GetTitleView() {
            if (m_tvTitle == null) {
                m_tvTitle = (TextView) m_BaseView.findViewById(R.id.s_m_prof_name);
                m_tvTitle.setTypeface(tf);
            }

            return m_tvTitle;
        }

        public TextView GetSubTitleView() {
            if (m_tvSubTitle == null) {
                m_tvSubTitle = (TextView) m_BaseView.findViewById(R.id.s_message_info);
                m_tvSubTitle.setTypeface(tf);
            }

            return m_tvSubTitle;
        }


    }
}
