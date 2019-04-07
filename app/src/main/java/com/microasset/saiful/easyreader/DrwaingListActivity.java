package com.microasset.saiful.easyreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.microasset.saiful.adapter.DataListAdapter;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.drawings.DrawingCanvas;
import com.microasset.saiful.entity.BookEntity;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.model.DrawingObjectDbModel;
import com.microasset.saiful.util.Convert;

public class DrwaingListActivity extends BookmarkListActivity implements DataListAdapter.DeleteIconClickListener{

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
            mList = DrawingObjectDbModel.getInstance(mInstance).getDrawingdListNonDuplicateUsingPageNumber(book);
            if(mList == null || mList.size() == 0){
                ((TextView)findViewById(R.id.tv_no_data)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.tv_no_data)).setText(R.string.STR_NO_DRAWING);
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
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_PAINT_LIST);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void deleteItem(int position){
        final BookEntity entity = (BookEntity)mList.get(position);
        DrawingCanvas.getInstance().delteAllDrawingItemFromPage(Convert.toInt(entity.getPagenumber()));
        //BookmarkDbModel.getInstance(this).delete(entity.getId());
        mList.remove(position);
        mAdapter.setDataList(mList);
        mAdapter.notifyDataSetChanged();

        if(mList == null || mList.size() == 0){
            ((TextView)findViewById(R.id.tv_no_data)).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.tv_no_data)).setText(R.string.STR_NO_DRAWING);
            return;
        }
    }

    @Override
    public void onItemDelete(int pos) {
        showDeleteDialog(pos);
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
}
