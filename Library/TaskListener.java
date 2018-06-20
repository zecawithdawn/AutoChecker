package com.example.kyh.real.Library;


import org.json.JSONObject;

/**
 * Created by seoseongho on 15. 3. 10..
 */
public interface TaskListener {

    public void onReceived(JSONObject jsonData);

    public void onCanceled();
}
