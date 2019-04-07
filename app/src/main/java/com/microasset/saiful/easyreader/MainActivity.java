package com.microasset.saiful.easyreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrinterId;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.microasset.saiful.adapter.BookShelfAdapter;
import com.microasset.saiful.adapter.CategoryAdapter;
import com.microasset.saiful.adapter.RecentlyOpenListAdapter;
import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.MLKit;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.appfrw.TranslateManager;
import com.microasset.saiful.licence.PaymentManager;
import com.microasset.saiful.licence.RegistrationManager;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.model.RecentlyOpenDbModel;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;

import java.util.ArrayList;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, DrawerLayout.DrawerListener{

    BookShelfAdapter mImageListAdapter;
    RadioGroup radioGroup;
    ImageView iv_classview;
    LinearLayout recently_open_panel;
    ToggleButton iv_recenltyopenview;
    RecyclerView recyclerView;
    RecentlyOpenListAdapter recentlyOpenListAdapter;
    public int REQUEST_CODE_LANGUAGE_CHANGE = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void createView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        setupToolbar();
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(mImageListAdapter);

        recently_open_panel = (LinearLayout)findViewById(R.id.recently_open_panel);

        iv_recenltyopenview = (ToggleButton) findViewById(R.id.iv_recently_open);
        iv_recenltyopenview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleRecentlyOpenPanel();
            }
        });

        setupRecentlyItemList();

        mImageListAdapter.setmNotifyObserver(this);

        radioGroup = (RadioGroup)findViewById(R.id.rg_toggle);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.rb_bn){
                    String version = BookInfoModel.getInstance(mInstance).getmSelectedVersion();
                    if(!version.isEmpty()){
                        BookInfoModel.getInstance(mInstance).setmSelectedVersion("");
                        SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_BOOK_VERSION, "");
                        loadData();
                    }
                }else if(checkedId == R.id.rb_en){
                    String version = BookInfoModel.getInstance(mInstance).getmSelectedVersion();
                    if(version.isEmpty()){
                        BookInfoModel.getInstance(mInstance).setmSelectedVersion(Constants.VALUE_VERSION_ENG);
                        SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_BOOK_VERSION, Constants.VALUE_VERSION_ENG);
                        loadData();
                    }
                }

                ((TextView)findViewById(R.id.tv_count)).setVisibility(View.VISIBLE);
                int count = mImageListAdapter.getTotalBooks();
                ((TextView)findViewById(R.id.tv_count)).setText(getString(R.string.STR_BOOK_COUNT, ""+mImageListAdapter.getTotalBooks()));
                initRecentData();
            }
        });

        String version = SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_BOOK_VERSION, "");
        if(version.equals(Constants.VALUE_VERSION_ENG)){
            radioGroup.check(R.id.rb_en);
        }
        //

        iv_classview = (ImageView)findViewById(R.id.iv_bookshelf);
        iv_classview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!RegistrationManager.getInstance(mInstance).isAllow(true)){
                    onClickPayment();
                }else {
                    MLKit.getInstance().clearData();
                    TranslateManager.getInstance(MainActivity.this).tryUsingGoogle("");
                    //startActivity(new Intent(MainActivity.this, TTSActivity.class));
                }
            }
        });
    }

    private void toggleRecentlyOpenPanel(){
        if(recently_open_panel != null && recently_open_panel.getVisibility() == View.VISIBLE){
            recently_open_panel.setVisibility(View.GONE);
            SharedPrefUtil.setSetting(this, SharedPrefUtil.KEY_RECENTLY_READ_STATE, false);
        }else {
            recently_open_panel.setVisibility(View.VISIBLE);
            SharedPrefUtil.setSetting(this, SharedPrefUtil.KEY_RECENTLY_READ_STATE, true);
        }
    }
    private void setupRecentlyItemList() {
        recyclerView = (RecyclerView) findViewById(R.id.recently_recycler_view);
        recentlyOpenListAdapter = new RecentlyOpenListAdapter(this);
        recentlyOpenListAdapter.setmNotifyObserver(this);
        recyclerView.setAdapter(recentlyOpenListAdapter);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        if(toolbar != null){
            getSupportActionBar().setTitle("");
        }
        String [] categoryArray = getResources().getStringArray(R.array.category_array);
        int position = SharedPrefUtil.getIntSetting(mInstance, SharedPrefUtil.KEY_CATEGORY_POS, 0);

        ((TextView)findViewById(R.id.tv_top)).setText(categoryArray[position]);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toolbar.setNavigationIcon(R.drawable.icon_nav);
        toggle.syncState();
        drawer.addDrawerListener(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.tv_mob);
        String mobNum = SharedPrefUtil.getSetting(this, SharedPrefUtil.KEY_MOBILE_NUMBER, "");
        navUsername.setText(getString(R.string.STR_REG_NUMBER, mobNum));
        updateExpireDateUI(navigationView);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void updateExpireDateUI(NavigationView navigationView){
        boolean isAllow = RegistrationManager.getInstance(mInstance).isAllow(true);
        String expireDate = RegistrationManager.getInstance(mInstance).getSubscriptionValidDate();
        if(!expireDate.isEmpty() && isAllow){
            Menu menu = navigationView.getMenu();
            MenuItem item = menu.findItem(R.id.item7);
            item.setVisible(true);
            item.getSubMenu().findItem(R.id.date).setTitle(" "+ expireDate);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView)findViewById(R.id.tv_count)).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.tv_count)).setText(getString(R.string.STR_BOOK_COUNT, ""+mImageListAdapter.getTotalBooks()));
        initRecentData();
    }

    private void initRecentData(){
        String className = BookInfoModel.getInstance(mInstance).getmSelectedClass();
        String classVersion = BookInfoModel.getInstance(mInstance).getmSelectedVersion();
        ArrayList<DataObject> recentList = RecentlyOpenDbModel.getInstance(mInstance).getRecentlyOpenList(className, classVersion);
        if(recentList != null && recentList.size() > 0){
            boolean isExpand = SharedPrefUtil.getBooleanSetting(this, SharedPrefUtil.KEY_RECENTLY_READ_STATE, false);
            iv_recenltyopenview.setVisibility(View.VISIBLE);
            if(isExpand){
                iv_recenltyopenview.setChecked(false);
                recently_open_panel.setVisibility(View.VISIBLE);
            }else {
                iv_recenltyopenview.setChecked(true);
                recently_open_panel.setVisibility(View.GONE);
            }

            recentlyOpenListAdapter.setDataList(recentList);
            recentlyOpenListAdapter.notifyDataSetChanged();
        }else {
            recently_open_panel.setVisibility(View.GONE);
            iv_recenltyopenview.setVisibility(View.GONE);
        }
    }

    @Override
    protected void createAdapter() {
        mImageListAdapter = new BookShelfAdapter(this);
        String className = SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_CATEGORY_CLASS, "");
        if(className.isEmpty()){
            int index = CategoryAdapter.getInstance().getmSelectedIndex();
            DataObject object = (DataObject) CategoryAdapter.getInstance().getItem(index);
            String value = object.getString(CategoryAdapter.value);
            className = value;
        }
        // WE NEED TO LOAD THE BOOK LATER..
        BookInfoModel.getInstance(mInstance).setmSelectedClass(className);
        // TODO only need to set this from Toggle Button.
        //BookInfoModel.getInstance(mInstance).setmSelectedVersion("E");
        String version = SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_BOOK_VERSION, "");
        BookInfoModel.getInstance(mInstance).setmSelectedVersion(version);
        mImageListAdapter.setClass(className);
    }

    @Override
    protected void loadData() {
        mImageListAdapter.loadData();
        mImageListAdapter.notifyDataSetInvalidated();
    }

    @Override
    public void doUpdateRequest(ResponseObject response) {
        if (response.getResponseMsg() == "BookShelfAdapter->setOnClickListener") {
            DataObject object = (DataObject) response.getDataObject();
            BookInfoModel.getInstance(mInstance).setmSelectedBook(object.getString("NAME"));
            BookInfoModel.getInstance(mInstance).setmImageLocation(object.getString("IMAGE_LOCATION"));
            if(BookInfoModel.getInstance(mInstance).getCount() == 0){
                Toast.makeText(this, "Book XML is not added!", Toast.LENGTH_LONG).show();
                return;
            }

            mInstance.startActivity(ReadingViewActivity.class);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.item1) {
            onClickPayment();

        } else if (id == R.id.item2) {
            //About
            startActivity(new Intent(this, AboutActivity.class));
        }else if (id == R.id.item3) {
            //Help
            Utill.openYoutubeLink(MainActivity.this, Constants.YOUTUBE_VIDEO_ID);

        }else if (id == R.id.item4) {
            Utill.rateApp(this);

        }else if (id == R.id.item5) {
            //Contact us
            Utill.sendEmail(this);
        }else if (id == R.id.item6) {
            //Bookshelf
            startActivity(new Intent(this, CategoryActivity.class));
            finish();
        }else if (id == R.id.item8) {
            //Settings
            startActivityForResult(SettingActivity.class, REQUEST_CODE_LANGUAGE_CHANGE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onClickPayment(){
        if(RegistrationManager.getInstance(mInstance).isAllow(true)){
            Toast.makeText(mInstance, getString(R.string.STR_PAID_ALREADY) + ": "+
                    RegistrationManager.getInstance(mInstance).getSubscriptionValidDate(), Toast.LENGTH_LONG).show();
            return;
        }

        Utill.isOnlineAsync(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == -1){
                    Toast.makeText(mInstance, getString(R.string.STR_NO_INTERNET), Toast.LENGTH_LONG).show();
                    return;
                }
                PaymentManager.getInstance(mInstance).doPayment();
            }
        });
    }


    @Override
    public void onDrawerSlide(@NonNull View view, float v) {

    }

    @Override
    public void onDrawerOpened(@NonNull View view) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView != null) {
            updateExpireDateUI(navigationView);
        }
    }

    @Override
    public void onDrawerClosed(@NonNull View view) {

    }

    @Override
    public void onDrawerStateChanged(int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_LANGUAGE_CHANGE ) {
                finish();
                startActivity(MainActivity.class);
            }
        }
    }
}
