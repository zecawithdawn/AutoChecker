package com.example.kyh.real.S_F;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kyh.real.Beacon.RECOBackgroundMonitoringService;
import com.example.kyh.real.Library.RoundImage;
import com.example.kyh.real.Library.TaskListener;
import com.example.kyh.real.P_F.P_Total_MainActivity;
import com.example.kyh.real.R;
import com.example.kyh.real.S_TimeTable.S_F_TimeTable;

import org.json.JSONObject;


public class S_Total_MainActivity extends FragmentActivity implements TaskListener, View.OnClickListener, S_NavigationDrawerFragment.OnMenuListener {
    ImageView menuButton;

    Fragment drawerFragment;

    private S_NavigationDrawerFragment mPNavigationDrawerFragment;
    ImageView imv;
    RoundImage rim;
    TextView tx ;
    public static String RECO_UUID;// = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";
    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private CharSequence mTitle;

    private String uid;
    private String db_ver;
    private String id;
    private String mac_addr;
    private String level;

    public static Intent MonitoringIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.s_total_activity_main);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        db_ver = intent.getStringExtra("db_ver");
        id = intent.getStringExtra("id");
        mac_addr = intent.getStringExtra("mac_addr");
        level = intent.getStringExtra("level");
        //actionbar menu
        menuButton = (ImageView) findViewById(R.id.menu3);
        menuButton.setOnClickListener(this);

        //얼굴자르기
        imv = (ImageView) findViewById(R.id.img);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.shin);
        rim = new RoundImage(bm);
        imv.setImageDrawable(rim);
        //액션바
        tx = (TextView)findViewById(R.id.act_title);

        //hide Fragment
        drawerFragment = getSupportFragmentManager().findFragmentById(R.id.s_navigation_drawer);
        android.support.v4.app.FragmentManager fm = drawerFragment.getFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(drawerFragment);
        transaction.commit();

        //first show
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(1, uid, db_ver, id, mac_addr, level))
                .commit();

        //사용자가 블루투스를 켜도록 요청합니다.
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
        }

        MonitoringIntent = new Intent(this, RECOBackgroundMonitoringService.class);
        startService(MonitoringIntent);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.menu3) {

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
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1, uid, db_ver, id, mac_addr, level))
                .commit();



        onMenuClosed();

        switch ( position){
            case 0 :
                tx.setText("출석현황");
                break;
            case 1 :
                tx.setText("시간표");
                break;
            case 2 :
                tx.setText("메시지");
                break;
            case 3 :
                tx.setText("환경설정");
                break;
            default:
                tx.setText("출석현황");
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
                    fragment = new S_F_Home();

                    break;
                case 2:
                    fragment = new S_F_TimeTable();
                    break;
                case 3:
                    fragment = new S_F_Message();
                    break;
                case 4:
                    fragment = new S_F_Setting();
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
        stopService(MonitoringIntent);
        super.onDestroy();
    }
}

//
//public class S_Total_MainActivity extends ActionBarActivity
//        implements S_NavigationDrawerFragment.NavigationDrawerCallbacks {
//
//
//
//    /**
//     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
//     */
//    private S_NavigationDrawerFragment mPNavigationDrawerFragment;
//    ImageView imv;
//    RoundImage rim;
//
//    public static String RECO_UUID;// = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";
//    private static final int REQUEST_ENABLE_BT = 1;
//
//    private BluetoothManager mBluetoothManager;
//    private BluetoothAdapter mBluetoothAdapter;
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
//    public static Intent MonitoringIntent;
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
////        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
//        setContentView(R.layout.s_total_activity_main);
//
////      //사용자가 블루투스를 켜도록 요청합니다.
//        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = mBluetoothManager.getAdapter();
//
//        if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
//        }
//
//
//        MonitoringIntent = new Intent(this, RECOBackgroundMonitoringService.class);
////        startService(MonitoringIntent);
//
////
////
////        Toast toast = Toast.makeText(getApplicationContext(),
////                "service Ranging", Toast.LENGTH_SHORT);
////        toast.setGravity(Gravity.CENTER, 0, 0);
////        toast.show();
////        startService(new Intent(this, RECOBackgroundRangingService.class));
//
////        Typeface tf = Typeface.createFromAsset(getAssets(), "KoPubDotumBold.ttf");
////        try {
////            Integer titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
////            TextView title = (TextView) getWindow().findViewById(titleId);
////            // check for null and manipulate the title as see fit
//////                title.setTextColor(getResources().getColor(R.color.black));
////            title.setTypeface(tf);
////        } catch (Exception e) {
////            Log.e("씨발아", "Failed to obtain action bar title reference");
////        }
//
//        imv = (ImageView) findViewById(R.id.img);
//        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.shin);
//        rim = new RoundImage(bm);
//        imv.setImageDrawable(rim);
//
//        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
//        mPNavigationDrawerFragment = (S_NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.s_navigation_drawer);
//        mTitle = getTitle();
//
//        // Set up the drawer.
//        mPNavigationDrawerFragment.setUp(
//                R.id.s_navigation_drawer,
//                (DrawerLayout) findViewById(R.id.drawer_layout));
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
//            //If the request to turn on bluetooth is denied, the app will be finished.
//            //사용자가 블루투스 요청을 허용하지 않았을 경우, 어플리케이션은 종료됩니다.
//            finish();
//            return;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
////    public void startNewService(View view){
////        startService(new Intent(this, RECOBackgroundMonitoringService.class));
////    }
//
//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        stopService(MonitoringIntent);
//        super.onDestroy();
//    }
//
//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//        // update the main content by replacing fragments
//            FragmentManager fragmentManager = getSupportFragmentManager();
//            fragmentManager.beginTransaction()
//                    .replace(R.id.container, PlaceholderFragment.newInstance(position + 1 , uid, db_ver, id, mac_addr, level))
//                    .commit();
//
////        따로 커스텀 하는 방식을 사용하기 위해 쓴 코드
//
//
//        ActionBar actionBar = getSupportActionBar();
//
//        switch (position) {
//            case 0:
//                actionBar.setTitle("MAIN/출석현황                                   ");
//                break;
//            case 1:
//                actionBar.setTitle("시간표                                          ");
//                break;
//            case 2:
//                actionBar.setTitle("메시지                                          ");
//                break;
//            case 3:
//                actionBar.setTitle("환경설정                                        ");
//                break;
//        }
//    }
//
////    public void onSectionAttached(int number) {
////        switch (number) {
////            case 1:
////                mTitle = getString(R.string.title_section1);
////                break;
////            case 2:
////                mTitle = getString(R.string.title_section2);
////                break;
////            case 3:
////                mTitle = getString(R.string.title_section3);
////                break;
////            case 4:
////                mTitle = getString(R.string.title_section4);
////        }
////    }
//
////    public void restoreActionBar() {
////        ActionBar actionBar = getSupportActionBar();
////        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
////        actionBar.setDisplayShowTitleEnabled(true);
////        actionBar.setTitle(mTitle);
////    }
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
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
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
//                    fragment = new S_F_Home();
//
//                    break;
//                case 2:
//                    fragment = new S_F_TimeTable();
//                    break;
//                case 3:
//                    fragment = new S_F_Message();
//                    break;
//                case 4:
//                    fragment = new S_F_Setting();
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
//
//        //없에도 돌아가길래 없앰 일단
////        @Override
////        public View onCreateView(LayoutInflater inflater, ViewGroup container,
////                                 Bundle savedInstanceState) {
////
////
////            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
////            Typeface tf = Typeface.createFromAsset(container.getContext().getAssets(),"KoPubDotumBold.ttf");
////
////
////            return rootView;
////        }
////
////        @Override
////        public void onAttach(Activity activity) {
////            super.onAttach(activity);
////
////        }
//    }
//
//}
