package com.microasset.saiful.easyreader;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.microasset.saiful.adapter.ToturialListAdapter;
import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.model.BookSolutionModel;
import com.microasset.saiful.util.Constants;

import java.util.ArrayList;

public class TutorialListActivity extends BaseActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.PlayerStateChangeListener {

    ToturialListAdapter mAdapter;
    ListView mListView;
    YouTubePlayer mPlayer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ArrayList<DataObject> mList = null;

    @Override
    protected void createView(Bundle savedInstanceState) {
        setContentView(R.layout.tutorial_layout);
        setupToolbar();
        mListView = (ListView) findViewById(R.id.listview);
        mList = BookSolutionModel.getInstance(mInstance).getSelectedChapterLinks();
        String book = BookInfoModel.getInstance(mInstance).getmSelectedBook();

        if (mList == null || mList.size() == 0) {
            ((TextView) findViewById(R.id.tv_no_data)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.tv_no_data)).setText("Tutorials comming soon!!");
            return;
        }



        setupYouTubePlayer();

        mAdapter = new ToturialListAdapter(mInstance, mList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataObject dataObject = (DataObject) mList.get(i);
                //String youtube = "https://www.youtube.com/watch?v=";
                //String id = dataObject.getString(BookSolutionModel.href).replace(youtube, "");
                String id = dataObject.getString(BookSolutionModel.href);
                playVideo(id);
                //Utill.openYoutubeToturial(mInstance, id);
            }
        });

        prepareSpinner();
    }

    private void prepareSpinner() {
        String chapterName = mList.get(0).getString(BookSolutionModel.chapter);
        Spinner cp_spin = (Spinner) findViewById(R.id.cp_spinner);

        final ArrayList availableChapters = BookSolutionModel.getInstance(mInstance).getAvailableChapters();
        final ArrayList list = new ArrayList();
        for(int i = 0; i<availableChapters.size() + 1; i++){
            if( i < availableChapters.size()){
                list.add(getString(R.string.STR_CHAPTER) + " " + availableChapters.get(i));
            }else{
                list.add(getString(R.string.STR_ALL_CHAPTER));
            }
        }

        ArrayAdapter aa=new ArrayAdapter(this, R.layout.simple_spinner_item,list);
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        cp_spin.setAdapter(aa);

        cp_spin.setSelection(availableChapters.indexOf(chapterName));

        cp_spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i < availableChapters.size()){
                    mList = BookSolutionModel.getInstance(mInstance).getLinksByChapterIndex( (String) availableChapters.get(i));
                }else{
                    mList = BookSolutionModel.getInstance(mInstance).getAllLinks();
                }

                if(mList.size() > 0){
                    mAdapter.setDataList(mList);
                    mAdapter.notifyDataSetChanged();
                    DataObject dataObject = (DataObject) mList.get(0);
                    if (dataObject != null) {
                        String id = dataObject.getString(BookSolutionModel.href);
                        playVideo(id);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void setupYouTubePlayer() {
        YouTubePlayerFragment youTubePlayerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY,
                this);
    }

    private void setupToolbar() {
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        if (toolBar != null) {
            setSupportActionBar(toolBar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_TUTORIAL);

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

    private void playVideo(String videoId) {
        if (mPlayer != null) {
            mPlayer.loadVideo(videoId);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        mPlayer = player;
        if (null == player) return;

        // Start buffering
        if (!wasRestored) {
            DataObject dataObject = (DataObject) mList.get(0);
            if (dataObject != null) {
                String id = dataObject.getString(BookSolutionModel.href);
                player.loadVideo(id);
            }
        }
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {

    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {

    }
}
