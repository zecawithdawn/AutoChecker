package com.example.kyh.real.P_F;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kyh.real.Library.RoundImage;
import com.example.kyh.real.Library.TaskListener;
import com.example.kyh.real.R;
import com.example.kyh.real.S_F.S_NavigationDrawerFragment;

import org.json.JSONObject;


public class P_Total_MainActivity extends FragmentActivity implements TaskListener, View.OnClickListener, P_NavigationDrawerFragment.OnMenuListener {
    ImageView menuButton;
    TextView tx;
    Fragment drawerFragment;
    private P_NavigationDrawerFragment mPNavigationDrawerFragment;
    ImageView imv;
    RoundImage rim;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private String uid;
    private String db_ver;
    private String id;
    private String mac_addr;
    private String level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.p_total_activity_main);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        db_ver = intent.getStringExtra("db_ver");
        id = intent.getStringExtra("id");
        mac_addr = intent.getStringExtra("mac_addr");
        level = intent.getStringExtra("level");

        //atctionbar title이다
        tx = (TextView)findViewById(R.id.p_act_title);

        menuButton = (ImageView) findViewById(R.id.p_menu3);
        menuButton.setOnClickListener(this);

        imv = (ImageView) findViewById(R.id.p_img);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.son);
        rim = new RoundImage(bm);
        imv.setImageDrawable(rim);
        //hide Fragment
        drawerFragment = getSupportFragmentManager().findFragmentById(R.id.p_navigation_drawer);
        android.support.v4.app.FragmentManager fm = drawerFragment.getFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(drawerFragment);
        transaction.commit();

        //first show
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.p_container, PlaceholderFragment.newInstance(1, uid, db_ver, id, mac_addr, level))
                .commit();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.p_menu3) {

            android.support.v4.app.FragmentManager fm = drawerFragment.getFragmentManager();
            android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_left, 0, 0, 0);
            transaction.show(drawerFragment);
            transaction.commit();

        }
    }

    public void closeDrawer() {
        android.support.v4.app.FragmentManager fm = drawerFragment.getFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(0, R.anim.slide_out_left);
        transaction.hide(drawerFragment);
        transaction.commit();
    }

    @Override
    public void onMenuClosed() {
        closeDrawer();
    }

    @Override
    public void onMenuSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.p_container, PlaceholderFragment.newInstance(position + 1, uid, db_ver, id, mac_addr, level))
                .commit();



        onMenuClosed();

        switch ( position){
            case 0 :
                tx.setText("수업시간표");
                break;
            case 1 :
                tx.setText("학생검색");
                break;
            case 2 :
                tx.setText("수업관리");
                break;
            case 3 :
                tx.setText("환경설정");
                break;
            default:
                tx.setText("수업시간표");
                break;
        }
    }

    @Override
    public void onReceived(JSONObject jsonData) {

    }

    @Override
    public void onCanceled() {

    }
    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, String uid, String db_ver, String id, String mac_addr, String level) {
            PlaceholderFragment fragment = null;
            Bundle args = new Bundle();
            args.putString("uid", uid);
            args.putString("db_ver", db_ver);
            args.putString("id", id);
            args.putString("mac_addr", mac_addr);
            args.putString("level", level);

            switch (sectionNumber) {
                case 1:
                    fragment = new P_F_Home();

                    break;
                case 2:
                    fragment = new P_F_StudentSearch();
                    break;
                case 3:
                    fragment = new P_F_ClassManage();
                    break;
                case 4:
                    fragment = new P_F_Setting();
                    break;
            }

            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}


//public class P_Total_MainActivity extends ActionBarActivity
//        implements P_NavigationDrawerFragment.NavigationDrawerCallbacks {
//
//    /**
//     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
//     */
//    private P_NavigationDrawerFragment mPNavigationDrawerFragment;
//    ImageView imv;
//    RoundImage rim;
//
//    /**
//     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
//     */
//    private CharSequence mTitle;
//
//    private String uid;
//    private String db_ver;
//    private String id;
//    private String mac_addr;
//    private String level;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        //actionbar
//
//        Intent intent = getIntent();
//        uid = intent.getStringExtra("uid");
//        db_ver = intent.getStringExtra("db_ver");
//        id = intent.getStringExtra("id");
//        mac_addr = intent.getStringExtra("mac_addr");
//        level = intent.getStringExtra("level");
//
//        setContentView(R.layout.p_total_activity_main);
////
//
//
//        imv = (ImageView)findViewById(R.id.p_img);
//        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.son);
//        rim = new RoundImage(bm);
//        imv.setImageDrawable(rim);
//
//
//
//        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//        mPNavigationDrawerFragment = (P_NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.p_navigation_drawer);
//        mTitle = getTitle();
//
//        // Set up the drawer.
//        mPNavigationDrawerFragment.setUp(
//                R.id.p_navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));
//    }
//
//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1, uid, db_ver, id, mac_addr, level))
//                .commit();
//        ActionBar actionBar = getSupportActionBar();
//        switch (position) {
//            case 0:
//
//                actionBar.setTitle("출석관리                                       ");
//
//                break;
//            case 1:
//                actionBar.setTitle("수업관리                                        ");
//
//                break;
//            case 2:
//                actionBar.setTitle("학생검색                                        ");
//
//                break;
//            case 3:
//                actionBar.setTitle("환경설정                                        ");
//
//                break;
//
//        }
//    }
//
//    public void onSectionAttached(int number) {
//        switch (number) {
//            case 1:
//                mTitle = getString(R.string.ptitle_section1);
//                break;
//            case 2:
//                mTitle = getString(R.string.ptitle_section2);
//                break;
//            case 3:
//                mTitle = getString(R.string.ptitle_section3);
//                break;
//            case 4:
//                mTitle = getString(R.string.ptitle_section4);
//        }
//    }
//
//    public void restoreActionBar() {
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
////        if (!mNavigationDrawerFragment.isDrawerOpen()) {
////            // Only show items in the action bar relevant to this screen
////            // if the drawer is not showing. Otherwise, let the drawer
////            // decide what to show in the action bar.
////            //getMenuInflater().inflate(R.menu.main, menu);
////            //개시발 이거 새끼 때문
////            //restoreActionBar();
////            return true;
////        }
////        getMenuInflater().inflate(R.menu.quit_button, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
////        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * A placeholder fragment containing a simple view.
//     */
//    public static class PlaceholderFragment extends Fragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        private static final String ARG_SECTION_NUMBER = "section_number";
//
//        /**
//         * Returns a new instance of this fragment for the given section
//         * number.
//         */
//        public static PlaceholderFragment newInstance(int sectionNumber, String uid, String db_ver, String id, String mac_addr, String level) {
//            PlaceholderFragment fragment = null;
//            Bundle args = new Bundle();
//            args.putString("uid", uid);
//            args.putString("db_ver", db_ver);
//            args.putString("id", id);
//            args.putString("mac_addr", mac_addr);
//            args.putString("level", level);
//
//            switch (sectionNumber) {
//                case 1:
//                    fragment = new P_F_Home();
//                    break;
//                case 2:
//                    fragment = new P_F_StudentSearch();
//                    break;
//                case 3:
//                    fragment = new P_F_ClassManage();
//                    break;
//                case 4:
//                    fragment = new P_F_Setting();
//                    break;
//            }
//
//            fragment.setArguments(args);
//            return fragment;
//        }
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
////            Typeface tf = Typeface.createFromAsset(container.getContext().getAssets(),"KoPubDotumBold.ttf");
//
//
//            return rootView;
//        }
//
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
////           ((S_Total_MainActivity) activity).onSectionAttached(
////                   getArguments().getInt(ARG_SECTION_NUMBER));
//        }
//    }
//
//}
