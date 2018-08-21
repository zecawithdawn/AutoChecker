package com.example.kyh.real.S_Message;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kyh.real.R;

import java.util.List;

/**
 * Created by kyh on 2015. 2. 7..
 */
public class S_Message_Info_Adapter extends ArrayAdapter<S_Message_Info_List_data> {
    private Context m_Context = null;
    Typeface tf;

    public S_Message_Info_Adapter(Context context, int resource, List<S_Message_Info_List_data> objects) {
        super(context, resource, objects);
        this.m_Context = context;
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
            view = LayoutInflater.from(m_Context).inflate(R.layout.s_message_info_list, null);
            pView = new PointerView(view);
            view.setTag(pView);
        }

        pView = (PointerView) view.getTag();

        // 데이터 클래스에서 해당 리스트목록의 데이터를 가져온다.
        S_Message_Info_List_data custom_list_data = getItem(nPosition);

        if (custom_list_data != null) {
            // 현재 item의 position에 맞는 이미지와 글을 넣어준다.

            pView.GetInfoContentView().setText(custom_list_data.getM_s_m_info_content());
            pView.GetInfoDateView().setText(custom_list_data.getM_s_m_info_date());
//            pView.getM_s_m_info_rl(custom_list_data);

        }

        return view;
    }


    /**
     * 뷰를 재사용 하기위해 필요한 클래스
     * 클래스 자체를 view tag로 저장/불러오므로 재사용가능
     */
    private class PointerView {
        private View m_BaseView = null;

        private TextView m_s_m_info_content = null;
        private TextView m_s_m_info_date = null;
        private RelativeLayout m_s_m_info_rl = null;

        public PointerView(View BaseView) {
            this.m_BaseView = BaseView;
        }

        public TextView GetInfoContentView() {
            if (m_s_m_info_content == null) {
                m_s_m_info_content = (TextView) m_BaseView.findViewById(R.id.s_m_info_content);
                m_s_m_info_content.setTypeface(tf);
            }
            return m_s_m_info_content;
        }

        public TextView GetInfoDateView() {
            if (m_s_m_info_date == null) {
                m_s_m_info_date = (TextView) m_BaseView.findViewById(R.id.s_m_info_date);
                m_s_m_info_date.setTypeface(tf);
            }
            return m_s_m_info_date;
        }

//        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//        public RelativeLayout getM_s_m_info_rl(S_Message_Info_List_data custom_list_data) {
//            if (m_s_m_info_rl == null) {
//                m_s_m_info_rl = (RelativeLayout) m_BaseView.findViewById(R.id.s_m_info_rl);
//                m_s_m_info_rl.setBackgroundDrawable(getContext().getDrawable(R.drawable.s_m_info_textview_bg));
//                if (custom_list_data.getR_w().equals("Y")) {
//
//                    m_s_m_info_rl.setBackgroundDrawable(getContext().getDrawable(R.drawable.s_m_info_textview_checked_bg));
//                }
//            }
//
//            return m_s_m_info_rl;
//        }

    }
}
