package com.microasset.saiful.easyreader;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.microasset.saiful.adapter.TocAdapter;
import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.licence.PaymentManager;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.Utill;

public class TocListActivity extends BaseActivity {

    private Toolbar mToolBar = null;
    TocAdapter mTocAdapter;
    private int mHighlighPos = -1;
    private Button bt_download_full;
    BookInfoModel mBookInfoModel = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void createView(Bundle savedInstanceState) {
        setContentView(R.layout.toc_layout);
        setupToolbar();

        mBookInfoModel = BookInfoModel.getInstance(this);

        bt_download_full = (Button) findViewById(R.id.bt_download);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(mTocAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataObject object = (DataObject) mTocAdapter.getItem(position);

                String str = object.getString(ResponseObject.mStatus);
                if (str != null && str.equalsIgnoreCase(ResponseObject.mContinue)) {
                    return;
                }

                String index = (String) object.getValue("index");
                final int start = Convert.toInt(index.split(",")[0]);
                final int end = Convert.toInt(index.split(",")[1]);

                if (!mBookInfoModel.isChapterAvailable(start, end)) {
                    // TODO FIXME download the pages...
                    downloadPages(object, mBookInfoModel, start, end, position);
                } else {
                    int pageNumber = Convert.toInt(BookInfoModel.getInstance(mInstance).getmMetaData().StartPage) + start - 1;
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", ""+ pageNumber);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

        if (mBookInfoModel.isAllChapterAvailable()) {
            bt_download_full.setVisibility(View.GONE);
        } else {
            bt_download_full.setVisibility(View.VISIBLE);
        }

        bt_download_full.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Utill.isOnlineAsync(new NotifyObserver() {
                    @Override
                    public void update(ResponseObject response) {
                        if(response.getResponseCode() == -1){
                            Toast.makeText(mInstance, getString(R.string.STR_NO_INTERNET), Toast.LENGTH_LONG).show();
                            return;
                        }
                        doOnClick(v);
                    }
                });
            }
        });
    }

    public void doOnClick(View v){
        //TODO Click action
        if (mTocAdapter != null) {
            for (int i = 0; i < mTocAdapter.getCount(); i++) {
                DataObject object = (DataObject) mTocAdapter.getItem(i);
                String index = (String) object.getValue("index");
                final int start = Convert.toInt(index.split(",")[0]);
                final int end = Convert.toInt(index.split(",")[1]);

                String str = object.getString(ResponseObject.mStatus);
                if (str != null && str.equalsIgnoreCase(ResponseObject.mContinue)) {
                    continue;
                }

                if (!mBookInfoModel.isChapterAvailable(start, end)) {
                    downloadPages(object, mBookInfoModel, start, end, i);
                }
            }
        }
    }

    private void setupToolbar() {
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_TOC);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void downloadPages(final DataObject object, final BookInfoModel bookInfoModel, final int start, final int end, final int itemPos) {
        Utill.isOnlineAsync(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == -1){
                    Toast.makeText(mInstance, getString(R.string.STR_NO_INTERNET), Toast.LENGTH_LONG).show();
                    return;
                }
                doDownloadPages(object, bookInfoModel, start, end, itemPos);
            }
        });
    }

    public void doDownloadPages(DataObject object, BookInfoModel bookInfoModel, int start, int end, int itemPos){

        object.setValue(ResponseObject.mStatus, ResponseObject.mContinue);
        object.setValue(ResponseObject.mItemPosition, itemPos);
        mTocAdapter.notifyDataSetChanged();

        bookInfoModel.downloadChapter(start, end, new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                try {
                    int pos = (int) response.getContentDownloaded();
                    int chapterPosByIndex = BookInfoModel.getInstance(mInstance).getChapterPosByIndex(pos); // FIXME TODO, why need +1 as mTocAdapter use this..
                    DataObject object = (DataObject) BookInfoModel.getInstance(mInstance).getmBookIndex().getItem(chapterPosByIndex);
                    object.setValue(ResponseObject.mStatus, response.getResponseMsg());
                    mTocAdapter.notifyDataSetChanged();
                    if (response.getResponseMsg().equals(ResponseObject.mFailed)) {
                        Toast.makeText(mInstance, getString(R.string.STR_DOWNLOAD_FAILE), Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    Toast.makeText(mInstance, getString(R.string.STR_DOWNLOAD_FAILE), Toast.LENGTH_LONG).show();
                    LogUtil.d(e.getMessage());
                }
            }
        });
    }

    @Override
    protected void createAdapter() {
        mTocAdapter = new TocAdapter(this, mHighlighPos);
        mTocAdapter.loadData();
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void doUpdateRequest(ResponseObject response) {

    }

    protected void showDownloadCancelDialog (){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mInstance);
        alertDialogBuilder.setTitle(R.string.STR_WARNING);
        alertDialogBuilder.setMessage(R.string.STR_DOWNLAOD_CANCEL_MSG);
        alertDialogBuilder.setNegativeButton(R.string.STR_NO,null);
        alertDialogBuilder.setPositiveButton(R.string.STR_YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               finish();
            }
        });
        alertDialogBuilder.setCancelable(true);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if(mTocAdapter != null && mTocAdapter.isDownloadOngoing()){
            showDownloadCancelDialog();
        }else{
            super.onBackPressed();
        }
    }
}
