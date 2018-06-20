package com.example.kyh.real.S_F;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kyh.real.Library.RoundImage;
import com.example.kyh.real.LoginRegister.LoginActivity;
import com.example.kyh.real.R;

import java.io.IOException;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class S_NavigationDrawerFragment extends Fragment implements View.OnClickListener {

    OnMenuListener listener;
    ListView listview;
    ImageView profile;
    ImageView logout;
    private int mCurrentSelectedPosition = 0;
    RoundImage rim;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            Uri imgUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), imgUri);
                rim = new RoundImage(bitmap);
                profile.setImageDrawable(rim);

            } catch (IOException e) {
                e.printStackTrace();
            }
//            Bitmap bm = (Bitmap)data.getExtras().get("data");
//            img1.setImageBitmap(bm);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.s_fragment_navigation_drawer, container, false);
        listview = (ListView) view.findViewById(R.id.gg);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }

        });

        listview.setAdapter(new S_CustomListAdapter(
                view.getContext(),
                R.layout.list_listview,
                R.id.listTextView,
                new String[]{
                        getString(R.string.title_section1),
                        getString(R.string.title_section2),
                        getString(R.string.title_section3),
                        getString(R.string.title_section4)

                }));
        listview.setItemChecked(mCurrentSelectedPosition, true);


        profile = (ImageView) view.findViewById(R.id.img);
        ImageView backbutton = (ImageView) view.findViewById(R.id.back);
        backbutton.setOnClickListener(this);
        profile.setOnClickListener(this);
        logout = (ImageView)view.findViewById(R.id.logout);
        logout.setOnClickListener(this);
        return view;

    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (listview != null) {
            listview.setItemChecked(position, true);
        }

        if (listener != null) {
            listener.onMenuSelected(position);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        if (v.getId() == R.id.back) {

            listener.onMenuClosed();

        } else if (v.getId() == R.id.img) {

            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "사진골라"), 1);
        } else if (v.getId() == R.id.logout) {
            Log.v("ttl발시","ㅅㅂㅅㅂㅅㅂㅅ");
            Intent i = new Intent(getActivity(),LoginActivity.class);
            getActivity().startActivity(i);
            getActivity().finish();
        }

    }

    public interface OnMenuListener {
        public void onMenuClosed();

        public void onMenuSelected(int i);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (OnMenuListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }

    }


//    /**
//     * Remember the position of the selected item.
//     */
//    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
//
//    /**
//     * Per the design guidelines, you should show the drawer on launch until the user manually
//     * expands it. This shared preference tracks this.
//     */
//    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
//
//    /**
//     * A pointer to the current callbacks instance (the Activity).
//     */
//    private NavigationDrawerCallbacks mCallbacks;
//
//    /**
//     * Helper component that ties the action bar to the navigation drawer.
//     */
//    private ActionBarDrawerToggle mDrawerToggle;
//
//    private DrawerLayout mDrawerLayout;
//    private ListView mDrawerListView;
//    private View mFragmentContainerView;
//
//    private RelativeLayout mrl;
//
//    ImageView imv;
//    RoundImage rim;
//
//    //추가
//    OnMenuListener listener;
//    String[] titlename = {
//            "MAIN/출석현황",
//            "시간표",
//            "메시지",
//            "환경설정"
//    };
//
//
//    private int mCurrentSelectedPosition = 0;
//    private boolean mFromSavedInstanceState;
//    private boolean mUserLearnedDrawer;
//
//    public S_NavigationDrawerFragment() {
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        // Read in the flag indicating whether or not the user has demonstrated awareness of the
//        // drawer. See PREF_USER_LEARNED_DRAWER for details.
//        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
//
//        if (savedInstanceState != null) {
//            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
//            mFromSavedInstanceState = true;
//        }
//
//        // Select either the default item (0) or the last selected item.
//        selectItem(mCurrentSelectedPosition);
//    }
//
//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        // Indicate that this fragment would like to influence the set of actions in the action bar.
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//
//        mrl = (RelativeLayout) inflater.inflate(
//                R.layout.s_fragment_navigation_drawer, container, false);
//
//
////
////        mDrawerListView = (ListView) mDrawerLayout.findViewById(R.id.gg);
////        mDrawerListView = (ListView) inflater.inflate(
////                R.layout.p_fragment_navigation_drawer, container, false);
////        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                selectItem(position);
////
////            }
////        });
////        mDrawerListView.setAdapter(new ArrayAdapter<String>(
////                getActionBar().getThemedContext(),
////                android.R.layout.simple_list_item_activated_1,
////                android.R.id.text1,
////                new String[]{
////                        getString(R.string.title_section1),
////                        getString(R.string.title_section2),
////                        getString(R.string.title_section3),
////                        "환경설정"
////                }));
////        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
////
////
//
//
//        return mrl;
//    }
//
//    public boolean isDrawerOpen() {
//        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
//    }
//
//    /**
//     * Users of this fragment must call this method to set up the navigation drawer interactions.
//     *
//     * @param fragmentId   The android:id of this fragment in its activity's layout.
//     * @param drawerLayout The DrawerLayout containing this fragment's UI.
//     */
//    public void setUp(int fragmentId, final DrawerLayout drawerLayout) {
//        mFragmentContainerView = getActivity().findViewById(fragmentId);
//        mDrawerLayout = drawerLayout;
//
//        // set a custom shadow that overlays the main content when the drawer opens
//        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
//        // set up the drawer's list view with items and click listener
//        // 온클릭 드로워 하는거 로그아웃이랑
//        ImageView menu_back = (ImageView) drawerLayout.findViewById(R.id.back);
//        ImageView logout_btn = (ImageView) drawerLayout.findViewById(R.id.logout);
//
//        menu_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                mDrawerLayout.closeDrawers();
//            }
//        });
//
//
//        logout_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(getActivity(), LoginActivity.class);
//                startActivity(i);
//                getActivity().finish();
//            }
//        });
//        //Set Font
//        Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "KoPubDotumMedium.ttf");
//        TextView t1 = (TextView) mDrawerLayout.findViewById(R.id.s_id);
//        TextView t2 = (TextView) mDrawerLayout.findViewById(R.id.s_mail);
//        TextView t3 = (TextView) mDrawerLayout.findViewById(R.id.s_name);
//
//        t1.setTypeface(tf);
//        t2.setTypeface(tf);
//        t3.setTypeface(tf);
//
//
//        /*  ----------------------------  */
//        mDrawerListView = (ListView) mDrawerLayout.findViewById(R.id.gg);
//
//        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectItem(position);
//
//            }
//        });
//
////       android.R.layout.simple_list_item_activated_1,
////        android.R.id.text1
//        mDrawerListView.setAdapter(new S_CustomListAdapter(
//                getActionBar().getThemedContext(),
//                R.layout.list_listview,
//                R.id.listTextView,
//                titlename
//        ));
////
////        mDrawerListView.setAdapter(new ArrayAdapter<String>(
////                getActionBar().getThemedContext(),
////                R.layout.list_listview,
////                R.id.listTextView,
////                new String[]{
////                        getString(R.string.title_section1),
////                        getString(R.string.title_section2),
////                        getString(R.string.title_section3),
////                        getString(R.string.title_section4)
////                }));
////
//
//        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
//
//
//        final ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//        //
//
//        // ActionBarDrawerToggle ties together the the proper interactions
//        // between the navigation drawer and the action bar app icon.
//        mDrawerToggle = new ActionBarDrawerToggle(
//                getActivity(),                    /* host Activity */
//                mDrawerLayout,                    /* DrawerLayout object */
//                R.drawable.menu3,             /* nav drawer image to replace 'Up' caret */
//                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
//                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
//        ) {
//
//
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                super.onDrawerSlide(drawerView, slideOffset);
//
//                if (slideOffset > 0.5) {
//                    actionBar.setShowHideAnimationEnabled(false);
//                    actionBar.hide();
//                } else {
//                    actionBar.setShowHideAnimationEnabled(false);
//                    actionBar.show();
//
//
//                }
//            }
//
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
////                actionBar.setShowHideAnimationEnabled(false);
////
////                actionBar.show();
//                if (!isAdded()) {
//                    return;
//                }
//
//                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//
//                if (!isAdded()) {
//                    return;
//                }
//
//                if (!mUserLearnedDrawer) {
//                    // The user manually opened the drawer; store this flag to prevent auto-showing
//                    // the navigation drawer automatically in the future.
//                    mUserLearnedDrawer = true;
//                    SharedPreferences sp = PreferenceManager
//                            .getDefaultSharedPreferences(getActivity());
//                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
//                }
//
//                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
//            }
//        };
//
//        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
//        // per the navigation drawer design guidelines.
//        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
//            //yogi
////            actionBar.setShowHideAnimationEnabled(false);
////            actionBar.hide();
//            mDrawerLayout.openDrawer(mFragmentContainerView);
//        }
//
//        // Defer code dependent on restoration of previous instance state.
//        mDrawerLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                mDrawerToggle.syncState();
//            }
//        });
//
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//
//    }
//
//
//    private void selectItem(int position) {
//        mCurrentSelectedPosition = position;
//        if (mDrawerListView != null) {
//            mDrawerListView.setItemChecked(position, true);
//        }
//        if (mDrawerLayout != null) {
//            //dd
////            mDrawerLayout.closeDrawer(mFragmentContainerView);
//            mDrawerLayout.closeDrawer(mFragmentContainerView);
//        }
//        if (mCallbacks != null) {
//            mCallbacks.onNavigationDrawerItemSelected(position);
//        }
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mCallbacks = (NavigationDrawerCallbacks) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mCallbacks = null;
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
//
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        // Forward the new configuration the drawer toggle component.
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        // If the drawer is open, show the global app actions in the action bar. See also
//        // showGlobalContextActionBar, which controls the top-left area of the action bar.
//        if (mDrawerLayout != null && isDrawerOpen()) {
//
//            //드로워 열때 액션바에 아이콘 만드는 새키
//
////            inflater.inflate(R.menu.global, menu);
////            showGlobalContextActionBar();
//        }
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//
////        if (item.getItemId() == R.id.action_example) {
////            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
////            return true;
//
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * Per the navigation drawer design guidelines, updates the action bar to show the global app
//     * 'context', rather than just what's in the current screen.
//     */
//    private void showGlobalContextActionBar() {
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
//        actionBar.setTitle("");
//    }
//
//    private ActionBar getActionBar() {
//        return ((ActionBarActivity) getActivity()).getSupportActionBar();
//    }
//
//
//    // 새로만든거 imlement도
//    @Override
//    public void onClick(View v) {
//
//    }
//
//    /**
//     * Callbacks interface that all activities using this fragment must implement.
//     */
//    public static interface NavigationDrawerCallbacks {
//        /**
//         * Called when an item in the navigation drawer is selected.
//         */
//        void onNavigationDrawerItemSelected(int position);
//    }


}
