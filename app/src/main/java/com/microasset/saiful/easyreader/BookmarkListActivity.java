package com.microasset.saiful.easyreader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.microasset.saiful.adapter.DataListAdapter;
import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.entity.BookEntity;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.model.BookmarkDbModel;
import com.microasset.saiful.model.DrawingObjectDbModel;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.SharedPrefUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class BookmarkListActivity extends BaseActivity implements DataListAdapter.DeleteIconClickListener{

    protected ListView mListView = null;
    protected ArrayList<DataObject> mList = null;
    protected DataListAdapter mAdapter = null;
    protected Toolbar mToolBar = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void createView(Bundle savedInstanceState) {
        setContentView(R.layout.data_listview);
        setupToolbar();
        mListView = (ListView) findViewById(R.id.listview);
        if(mInstance != null){
            String book = BookInfoModel.getInstance(mInstance).getmSelectedBook();
            mList =  BookmarkDbModel.getInstance(mInstance).getBookMarkList(book);
            if(mList == null || mList.size() == 0){
                ((TextView)findViewById(R.id.tv_no_data)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.tv_no_data)).setText(R.string.STR_NO_BOOKMARK);
                return;
            }
            sortData(false);
            mAdapter = new DataListAdapter(mInstance,mList);
            mAdapter.setDelListener(this);
            mListView.setAdapter(mAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    BookEntity dataObject = (BookEntity) mList.get(i);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result",dataObject.getPagenumber());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            });
        }
    }

    private void setupToolbar() {
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_BOOKMARK_LIST);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void deleteItem(int position){
        final BookEntity entity = (BookEntity)mList.get(position);
        BookmarkDbModel.getInstance(this).delete(entity.getId());
        mList.remove(position);
        mAdapter.setDataList(mList);
        mAdapter.notifyDataSetChanged();

        if(mList == null || mList.size() == 0){
            ((TextView)findViewById(R.id.tv_no_data)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tv_no_data)).setText(R.string.STR_NO_BOOKMARK);
            return;
        }
    }

    protected void showDeleteDialog (final int pos){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mInstance);
        alertDialogBuilder.setTitle(R.string.STR_REMOVE);
        alertDialogBuilder.setNegativeButton(R.string.STR_NO,null);
        alertDialogBuilder.setPositiveButton(R.string.STR_YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem(pos);
            }
        });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onItemDelete(int pos) {
        showDeleteDialog(pos);
    }


    protected class SortByPage implements Comparator<DataObject> {

        @Override
        public int compare(DataObject obj1, DataObject obj2) {
            int page1 = Convert.toInt(((BookEntity)obj1).getPagenumber());
            int page2 = Convert.toInt(((BookEntity)obj2).getPagenumber());
            return page1 - page2;
        }
    }

    protected class SortByDate implements Comparator<DataObject>{

        @Override
        public int compare(DataObject obj1, DataObject obj2) {
            String str1 = ((BookEntity)obj1).getInsertDate();
            String str2 = ((BookEntity)obj2).getInsertDate();
            Date date1 = null;
            Date date2 = null;
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.US);
            try {
                date1 = format.parse(str1);
                date2 = format.parse(str2);
                if (date1 != null && date2 != null){
                    return date2.compareTo(date1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    public void sortData(boolean needRefresh){
        int sortID = SharedPrefUtil.getIntSetting(this, SharedPrefUtil.KEY_BOOKMARK_SORT, Constants.SORT_BY_PAGE);
        if(sortID == Constants.SORT_BY_PAGE){
            Collections.sort(mList, new SortByPage());
        }else{
            Collections.sort(mList, new SortByDate());
        }

        if(needRefresh && mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }
    @Override
    protected void createAdapter() {
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void doUpdateRequest(ResponseObject response) {
        if (response.getResponseMsg() == "BookShelfAdapter->setOnClickListener") {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_sort, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_sort) {
            updateOptionMenu(item);
        } else if (item.getItemId() == R.id.sort_page) {
            SharedPrefUtil.setSetting(this, SharedPrefUtil.KEY_BOOKMARK_SORT, Constants.SORT_BY_PAGE);
            sortData(true);
        } else if (item.getItemId() == R.id.sort_date) {
            SharedPrefUtil.setSetting(this, SharedPrefUtil.KEY_BOOKMARK_SORT, Constants.SORT_BY_DATE);
            sortData(true);
        }
        return super.onOptionsItemSelected(item);
    }

    protected void updateOptionMenu(MenuItem sortMenuItem) {
        if (sortMenuItem != null) {
            SubMenu viewOptionsSubMenu = sortMenuItem.getSubMenu();
            if (viewOptionsSubMenu != null) {
                int sortID = SharedPrefUtil.getIntSetting(this, SharedPrefUtil.KEY_BOOKMARK_SORT, Constants.SORT_BY_PAGE);
                if (sortID == Constants.SORT_BY_PAGE) {
                    viewOptionsSubMenu.getItem(0).setChecked(true);
                } else {
                    viewOptionsSubMenu.getItem(1).setChecked(true);
                }
            }
        }
    }
}
