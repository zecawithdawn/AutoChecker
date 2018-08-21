package com.example.kyh.real.P_F;

/**
 * Created by kyh on 2015. 1. 27..
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.kyh.real.Database.DbAdapter;
import com.example.kyh.real.R;


public class P_F_ClassManage extends P_Total_MainActivity.PlaceholderFragment {
    private static DbAdapter mHelper;

    private String uid;
    private int db_ver;
    private String id;
    private String mac_addr;
    private String level;

    RelativeLayout layout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        uid = bundle.getString("uid", "Default UID");
        db_ver = Integer.parseInt(bundle.getString("db_ver"));
        id = bundle.getString("id");
        mac_addr = bundle.getString("mac_addr");
        level = bundle.getString("level");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_p_home, container, false);
        mHelper = new DbAdapter(layout.getContext(), uid, db_ver, level);
        mHelper.getReadableDatabase();
        super.onCreate(savedInstanceState);

        layout.setOnClickListener((View.OnClickListener) this);
        return layout;
    }

}
