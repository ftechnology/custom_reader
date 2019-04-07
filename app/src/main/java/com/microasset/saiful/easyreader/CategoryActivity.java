package com.microasset.saiful.easyreader;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.microasset.saiful.adapter.BookShelfAdapter;
import com.microasset.saiful.adapter.CategoryAdapter;
import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.model.DeviceDetailModel;
import com.microasset.saiful.util.SharedPrefUtil;

public class CategoryActivity extends BaseActivity {

    CategoryAdapter categoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void createView(Bundle savedInstanceState) {
        setContentView(R.layout.category_layout);
        setupToolbar();
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(categoryAdapter);
        categoryAdapter.setmNotifyObserver(this);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataObject object = (DataObject) categoryAdapter.getItem(i);
                String value = object.getString(CategoryAdapter.value);
                //WE WANT TO SAVE THE SELECTED VALUE..
                SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_CATEGORY_CLASS, value);
                SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_CATEGORY_POS, i);
                categoryAdapter.setmSelectedIndex(i);
                mInstance.startActivity(MainActivity.class);
                finish();
            }
        });
    }

    private void setupToolbar() {
        Toolbar mToolBar = (Toolbar) findViewById(R.id.toolBar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }
        getSupportActionBar().setTitle("");
        ((ImageView) findViewById(R.id.iv_logo)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.app_name);
    }
    @Override
    protected void createAdapter() {
        categoryAdapter = CategoryAdapter.getInstance(); //new CategoryAdapter(this);
        categoryAdapter.setContext(this);
        categoryAdapter.loadData();
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void doUpdateRequest(ResponseObject response) {

    }
}
