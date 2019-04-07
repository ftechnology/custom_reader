package com.microasset.saiful.easyreader;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.microasset.saiful.adapter.AvailableToturialListAdapter;
import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.model.BookSolutionModel;
import com.microasset.saiful.util.Convert;

import java.util.ArrayList;

public class AvailableTutorialListActivity extends BaseActivity {

    AvailableToturialListAdapter mAdapter;
    ListView mListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ArrayList<String> mList = null;

    @Override
    protected void createView(Bundle savedInstanceState) {
        setContentView(R.layout.available_tutorial_layout);
        setupToolbar();
        mListView = (ListView) findViewById(R.id.listview);
        mList = BookSolutionModel.getInstance(mInstance).getAvailableChapters();

        mAdapter = new AvailableToturialListAdapter(mInstance, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String index = (String) mList.get(i);
                ArrayList list = BookSolutionModel.getInstance(mInstance).getLinksByChapterIndex(index);
                BookSolutionModel.getInstance(mInstance).setmSelectedChapterList(list);
                if(list.size() > 0){
                    startActivity(TutorialListActivity.class);
                }
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        if (toolBar != null) {
            setSupportActionBar(toolBar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_AVAILABLE_TUTORIAL);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void createAdapter() {
    }

    @Override
    protected void loadData() {

    }

    @Override
    public void doUpdateRequest(ResponseObject response) {

    }
}
