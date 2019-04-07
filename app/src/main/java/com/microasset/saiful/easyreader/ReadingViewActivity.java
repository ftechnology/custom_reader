package com.microasset.saiful.easyreader;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.microasset.saiful.adapter.ReadingViewAdapter;
import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.ImageLoader;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.MLKit;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.appfrw.TranslateManager;
import com.microasset.saiful.drawings.DrawingCanvas;
import com.microasset.saiful.drawings.OCRSelection;
import com.microasset.saiful.drawings.Point;
import com.microasset.saiful.drawings.ShapeFactory;
import com.microasset.saiful.entity.BookEntity;
import com.microasset.saiful.entity.HomeWorkEntity;
import com.microasset.saiful.entity.RecentlyOpenEntity;
import com.microasset.saiful.fragment.OverlayFragment;
import com.microasset.saiful.licence.PaymentManager;
import com.microasset.saiful.licence.RegistrationManager;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.model.BookQuestionModel;
import com.microasset.saiful.model.BookSolutionModel;
import com.microasset.saiful.model.BookmarkDbModel;
import com.microasset.saiful.model.HomeWorkDbModel;
import com.microasset.saiful.model.RecentlyOpenDbModel;
import com.microasset.saiful.pageeffect.BookFlipPageTransformer;
import com.microasset.saiful.pageeffect.CubeInRotationTransformation;
import com.microasset.saiful.pageeffect.ForegroundToBackgroundTransformer;
import com.microasset.saiful.pageeffect.HingeTransformation;
import com.microasset.saiful.pageeffect.StackTransformer;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;
import com.microasset.saiful.view.HackyViewPager;
import com.microasset.saiful.view.ZoomImageView;

import android.support.v7.widget.Toolbar;

import java.util.ArrayList;


public class ReadingViewActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    ReadingViewAdapter mReadingViewAdapter;
    //String BookID = "Primary - 2018 - (B.Version.) - Class-5 Bangla PDF Web";
    private Toolbar mToolBar = null;
    private Handler mHandler = null;
    ArrayList<Integer> mHistoryList = null;
    ImageView iv_left, iv_right;
    int mCurrentPageNumber = 0;
    int REQUEST_CODE_BOOKMARK = 1;
    int REQUEST_CODE_TOC = 2;
    ActionMode mActionMode = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.readingview);

        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_left.setOnClickListener(this);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        iv_right.setOnClickListener(this);

        setupToolbar();

        mViewPager = (HackyViewPager) findViewById(R.id.hackyViewpager);
        mViewPager.setAdapter(mReadingViewAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                mCurrentPageNumber = i;
                pageChange();
                BookmarkDbModel.getInstance(mInstance).saveLastReadingPosition(BookInfoModel.getInstance(mInstance).getmSelectedBook(), Convert.toString(i + 1));
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        if (BookInfoModel.getInstance(mInstance).isAnyChapterAvailable()) {
            // TODO if we have last reading position saved then JumpTo that position.....
            BookEntity entity = BookmarkDbModel.getInstance(mInstance).getLastReadingPosition(BookInfoModel.getInstance(mInstance).getmSelectedBook());
            if (entity != null) {
                int pageNumber = Convert.toInt(entity.getPagenumber());
                jumpToPageNumber(pageNumber - 1, false);
            } else {
                jumpToPageNumber(0, false);
            }
        } else {
            // Show TOC to download the Book or Chapters...
            clickTocList();
        }

        addToRecentlyOpenList();
    }

    private void addToRecentlyOpenList() {
        String bookId = BookInfoModel.getInstance(mInstance).getmSelectedBook();
        if((bookId.lastIndexOf(" - Q") > 3)){
            if(bookId.lastIndexOf(" - Q") < bookId.length() - 3 ){
                return;
            }
        }

        String className = BookInfoModel.getInstance(mInstance).getmSelectedClass();
        String classVersion = BookInfoModel.getInstance(mInstance).getmSelectedVersion();
        String date = Utill.getFormattedDateWithoutDay();
        String imgLocation = BookInfoModel.getInstance(mInstance).getmImageLocation();
        RecentlyOpenEntity entity = RecentlyOpenDbModel.getInstance(mInstance).hasRecentlyOpen(bookId);
        if (entity == null) {
            //Add
            long id = RecentlyOpenDbModel.getInstance(mInstance).insert(new RecentlyOpenEntity(bookId, className, classVersion, imgLocation, date));
        } else {
            //Update
            entity.setInsertDate(date);
            RecentlyOpenDbModel.getInstance(mInstance).update(entity);

        }
    }


    private void updatePageEffect() {
        int pageEffect = SharedPrefUtil.getIntSetting(mInstance, SharedPrefUtil.KEY_PAGE_EFFECT, 2);
        switch (pageEffect) {
            case Constants.PAGE_EFFECT_SLIDE:
                mViewPager.setPageTransformer(false, null);
                break;

            case Constants.PAGE_EFFECT_BOOK_FLIP:
                BookFlipPageTransformer bookFlipPageTransformer = new BookFlipPageTransformer();
                mViewPager.setPageTransformer(true, bookFlipPageTransformer);
                break;

            case Constants.PAGE_EFFECT_STAKE:
                StackTransformer stackPageTransformer = new StackTransformer();
                mViewPager.setPageTransformer(true, stackPageTransformer);
                break;

            case Constants.PAGE_EFFECT_CUBE_IN:
                CubeInRotationTransformation cubeInRotationTransformation = new CubeInRotationTransformation();
                mViewPager.setPageTransformer(true, cubeInRotationTransformation);
                break;

            case Constants.PAGE_EFFECT_GRIP:
                HingeTransformation hingeTransformation = new HingeTransformation();
                mViewPager.setPageTransformer(true, hingeTransformation);
                break;

            case Constants.PAGE_EFFECT_FOREGROUND_BACKGROUND:
                ForegroundToBackgroundTransformer foregroundToBackgroundTransformer = new ForegroundToBackgroundTransformer();
                mViewPager.setPageTransformer(true, foregroundToBackgroundTransformer);
                break;

            default:
                break;

        }
    }

    @Override
    protected void createView(Bundle savedInstanceState) {
    }

    private void setupToolbar() {
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_READER);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePageEffect();
    }

    public int getTotalPage() {
        if (mReadingViewAdapter != null)
            return mReadingViewAdapter.getCount();
        return 1;
    }

    public int getCurrentPage() {
        if (mViewPager != null) {
            return (mViewPager.getCurrentItem()) + 1;
        }

        return 1;
    }

    public void jumpToPageNumber(int pageNum, boolean addHistory) {
        hideThumbList();
        if (mHistoryList == null) {
            mHistoryList = new ArrayList<>();
        }
        if (mViewPager != null) {
            if (addHistory && (mCurrentPageNumber != pageNum)) {
                mHistoryList.add(mViewPager.getCurrentItem());
            }
            mViewPager.setPageTransformer(false, null);
            mViewPager.setCurrentItem(pageNum);

            Thread timer = new Thread() {
                public void run() {
                    try {
                        sleep(300);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        updatePageEffect();
                    }
                }
            };
            timer.start();
        }
    }

    public boolean hasHistory() {
        if (mHistoryList != null && mHistoryList.size() > 0) {
            return true;
        }
        return false;
    }

    private void hideOverlay() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.utility_view);
        if (f != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(f)
                    .commit();
        }
    }

    private void showOverlay() {
        Fragment fragment = OverlayFragment.getInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.utility_view, fragment).commit();
    }

    public void showOverlayUx() {
        if (mActionMode != null) {
            return;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.utility_view);
        if (f == null) {
            showOverlay();
        } else {
            if (DrawingCanvas.getInstance().getmSelected() == null) {
                hideOverlay();
            }
        }
    }

    public void gotoNextPage() {
        int totalPage = mReadingViewAdapter.getCount();
        int currentPage = mViewPager.getCurrentItem();
        if (currentPage < totalPage) {
            mViewPager.setCurrentItem(currentPage + 1, true);
        }
    }

    public void gotoPrevPage() {
        int currentPage = mViewPager.getCurrentItem();
        if (currentPage > 0) {
            mViewPager.setCurrentItem(currentPage - 1, true);
        }
    }


    public void bookmarkButtonClick() {
        int index = mViewPager.getCurrentItem();
        String pageNumber = mReadingViewAdapter.getPageNumber(index);
        BookEntity entity = BookmarkDbModel.getInstance(mInstance).hasBookMark(BookInfoModel.getInstance(mInstance).getmSelectedBook(), pageNumber);
        if (entity == null) {
            BookmarkDbModel.getInstance(mInstance).insert(new BookEntity(BookInfoModel.getInstance(mInstance).getmSelectedBook(), pageNumber, Utill.getFormattedDate(), BookEntity.Type_Bookmark));
            Toast.makeText(this, getString(R.string.STR_BOOKMARK_ADDED), Toast.LENGTH_LONG).show();
        } else {
            long id = entity.getId();
            BookmarkDbModel.getInstance(mInstance).delete(id);
            Toast.makeText(this, getString(R.string.STR_BOOKMARK_DELETED), Toast.LENGTH_LONG).show();
        }
    }

    public void onClickSolution() {
        Utill.isOnlineAsync(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == -1){
                    if(!onCheckOffline()){
                        Toast.makeText(mInstance, getString(R.string.STR_NO_INTERNET), Toast.LENGTH_LONG).show();
                    }
                    return;
                }
                doOnClickSolution();
            }
        });
    }

    public boolean onCheckOffline(){
        int currentPage = this.getCurrentPage();
        final int chapterPosByIndex = BookInfoModel.getInstance(mInstance).getChapterPosByIndex(currentPage);
        BookSolutionModel.getInstance(mInstance).populateLinks();
        ArrayList list = BookSolutionModel.getInstance(mInstance).getLinksByChapterIndexOffline(Convert.toString(chapterPosByIndex));
        BookSolutionModel.getInstance(mInstance).setmSelectedChapterList(list);
        if(list.size() > 0){
            startActivity(TutorialListActivity.class);
            return true;
        }else{
            Toast.makeText(mInstance, getString(R.string.STR_NO_TUTORIAL), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    public void doOnClickSolution(){

        int currentPage = this.getCurrentPage();
        final int chapterPosByIndex = BookInfoModel.getInstance(mInstance).getChapterPosByIndex(currentPage);
        PaymentManager.showProgressDialog(this);
        BookSolutionModel.getInstance(mInstance).executeAsyn(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                PaymentManager.hideDialog();
                ArrayList list = BookSolutionModel.getInstance(mInstance).getLinksByChapterIndex(Convert.toString(chapterPosByIndex));
                BookSolutionModel.getInstance(mInstance).setmSelectedChapterList(list);
                if(list.size() > 0){
                    startActivity(TutorialListActivity.class);
                }else{
                    ArrayList chapterList = BookSolutionModel.getInstance(mInstance).getAvailableChapters();
                    if(chapterList.size() > 0){
                        startActivity(AvailableTutorialListActivity.class);
                    }else{
                        Toast.makeText(mInstance, getString(R.string.STR_NO_TUTORIAL), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void homeWorkButtonClick() {
        int index = mViewPager.getCurrentItem();
        String pageNumber = mReadingViewAdapter.getPageNumber(index);
        HomeWorkEntity entity = HomeWorkDbModel.getInstance(mInstance).hasHomework(BookInfoModel.getInstance(mInstance).getmSelectedBook(), pageNumber);
        if (entity == null) {
            HomeWorkDbModel.getInstance(mInstance).insert(new HomeWorkEntity(BookInfoModel.getInstance(mInstance).getmSelectedBook(), "", Utill.getFormattedDate(), pageNumber));
            Toast.makeText(this, getString(R.string.STR_HOMEWORK_ADDED), Toast.LENGTH_LONG).show();
        } else {
            long id = entity.getId();
            HomeWorkDbModel.getInstance(mInstance).delete(id);
            Toast.makeText(this, getString(R.string.STR_HOMEWORK_DELETED), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void createAdapter() {
        //mViewPager = new HackyViewPager(this);
        mReadingViewAdapter = new ReadingViewAdapter(this);
        //Primary - 2018 - (B.Version.) - Class-5 Math PDF Web
        //String BookFolder = "Primary - 2018 - (B.Version.) - Class-5 Math PDF Web";
        //String BookFolder = "test";

        mReadingViewAdapter.setBookName(BookInfoModel.getInstance(mInstance).getmSelectedBook());
        // mViewPager.getCurrentItem()
    }

    @Override
    protected void loadData() {
        mReadingViewAdapter.loadData();
        DrawingCanvas.getInstance().loadItems(this);
    }

    @Override
    public void doUpdateRequest(ResponseObject response) {
    }

    public void clickTocList() {
        Intent intent = new Intent(this, TocListActivity.class);
        startActivityForResult(intent, REQUEST_CODE_TOC);
    }

    public void clickOCR(ArrayList<Point> points) {
        if (!RegistrationManager.getInstance(this).isAllow(true)) {
            //Toast.makeText(this, R.string.STR_PAY_DUE, Toast.LENGTH_LONG).show();
            doPayment();
            return;
        }

        MLKit.getInstance().clearData();
        int x = (int) points.get(0).x;
        int y = (int) points.get(0).y;
        int w = Math.abs((int) points.get(1).x - x);
        int h = Math.abs((int) points.get(1).y - y);
        ImageLoader imageLoader = new ImageLoader(mInstance);
        ZoomImageView zoomImageView = (ZoomImageView) DrawingCanvas.getInstance().getmView();
        if (zoomImageView != null) {
            Bitmap bitmap = imageLoader.cropBitmap(zoomImageView.getImageViewReference(), x, y, w, h);
            final MLKit mlKit = MLKit.getInstance();
            mlKit.getTextFromImage(this, bitmap, new NotifyObserver() {
                @Override
                public void update(ResponseObject response) {
                    if (response.getResponseMsg().equals(ResponseObject.mSuccess)) {
                        //mInstance.startActivity(TTSActivity.class);
                        TranslateManager.getInstance(ReadingViewActivity.this).tryUsingGoogle(mlKit.getFbVisionText().getText());
                    } else {
                        Toast.makeText(mInstance, "Failed to Get Text from Selected area!", Toast.LENGTH_LONG).show();
                        //mInstance.startActivity(TTSActivity.class);
                    }
                }
            });
        }
    }

    public void clickBookmarkList() {
        if (!RegistrationManager.getInstance(this).isAllow(false)) {
            //Toast.makeText(this, R.string.STR_PAY_DUE, Toast.LENGTH_LONG).show();
            doPayment();
            return;
        }

        Intent intent = new Intent(this, BookmarkListActivity.class);
        intent.putExtra(Constants.KEY_DATA_TYPE, Constants.TYPE_BOOKMARK);
        startActivityForResult(intent, REQUEST_CODE_BOOKMARK);
    }

    public void clickDrawingList() {
        Intent intent = new Intent(this, DrwaingListActivity.class);
        intent.putExtra(Constants.KEY_DATA_TYPE, Constants.TYPE_DRAWING_OBJ);
        startActivityForResult(intent, REQUEST_CODE_BOOKMARK);

    }

    public void clickSettings() {

    }

    public void clickQuestionsList() {
        String bookId = BookInfoModel.getInstance(mInstance).getmSelectedBook();
        if((bookId.lastIndexOf(" - Q") > 3)){
            if(bookId.lastIndexOf(" - Q") < bookId.length() - 3 ){
                return;
            }
        }

        if(BookQuestionModel.getInstance(mInstance).loadQuestion()){
            mInstance.startActivity(ReadingViewActivity.class);
            finish();
            return;
        }

        BookQuestionModel.getInstance(mInstance).executeAsyn(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(BookQuestionModel.getInstance(mInstance).loadQuestion()){
                    mInstance.startActivity(ReadingViewActivity.class);
                    finish();
                } else {
                    Toast.makeText(mInstance, getString(R.string.STR_NO_QUESTION), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void clickPaint() {
        // Paint move state....
        DrawingCanvas.getInstance().setState(DrawingCanvas.State.DRAW_OBJECT);
    }

    //
    public void clickBtnDelete() {
        DrawingCanvas.getInstance().setState(DrawingCanvas.State.NONE);
        DrawingCanvas.getInstance().deleteSelectedItem();
    }

    public void clickBtnRectangle() {
        DrawingCanvas.getInstance().setState(DrawingCanvas.State.DRAW_OBJECT);
        DrawingCanvas.getInstance().setSelectedShape(ShapeFactory.RECTANGLE);
    }

    public void clickBtnCross() {
        DrawingCanvas.getInstance().setState(DrawingCanvas.State.DRAW_OBJECT);
        DrawingCanvas.getInstance().setSelectedShape(ShapeFactory.SYMBOL_CROSS);
    }

    public void clickBtnYes() {
        DrawingCanvas.getInstance().setState(DrawingCanvas.State.DRAW_OBJECT);
        DrawingCanvas.getInstance().setSelectedShape(ShapeFactory.SYMBOL_YES);
    }


    public void clickHistoryBack() {
        if (mHistoryList != null && mHistoryList.size() > 0) {
            int pageNumber = mHistoryList.get(mHistoryList.size() - 1);
            mHistoryList.remove(mHistoryList.size() - 1);
            if (mViewPager != null) {
                mViewPager.setPageTransformer(false, null);
                mViewPager.setCurrentItem(pageNumber);
            }

            Thread timer = new Thread() {
                public void run() {
                    try {
                        sleep(300);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        updatePageEffect();
                    }
                }
            };
            timer.start();
        }
    }

    private void pageChange() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.utility_view);
        if (f != null && f instanceof OverlayFragment) {
            ((OverlayFragment) f).updateUI();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu_main, menu);
        MenuItem settings = menu.findItem(R.id.item_setting);
        Drawable drawable = settings.getIcon(); // change 0 with 1,2 ...
        drawable.mutate();
        drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_more) {
            showOverlayUx();
        } else if (item.getItemId() == R.id.item_ocr) {
            if (!RegistrationManager.getInstance(this).isAllow(true)) {
                //Toast.makeText(this, R.string.STR_PAY_DUE, Toast.LENGTH_LONG).show();
                doPayment();
            } else {
                mToolBar.startActionMode(new ActionModeCallback());
                DrawingCanvas.getInstance().setmNotifyObserver((NotifyObserver) getActiveView());
                DrawingCanvas.getInstance().createOCR(100, 100, mViewPager.getCurrentItem());
                MLKit.getInstance().clearData();
            }
        } else if (item.getItemId() == R.id.item_setting) {
            Intent intent = new Intent(mInstance, SettingActivity.class);
            intent.putExtra(SettingActivity.EXTRA_ISREADING, true);
            startActivity(intent);
        } else if (item.getItemId() == R.id.item_youtube) {
            onClickSolution();
        } else if (item.getItemId() == R.id.item_questions) {
            clickQuestionsList();
        }
        return super.onOptionsItemSelected(item);
    }

    private void doPayment() {
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

    public View getActiveView() {
        int pos = mViewPager.getCurrentItem();
        View activeView = mViewPager.findViewWithTag(mViewPager.getCurrentItem());
        ZoomImageView reading_view = activeView.findViewById(R.id.reading_view);
        return reading_view;

        /*
        for(int i = 0; i<2; i++){
            View activeView  = mViewPager.getChildAt(i);
            int position = (int) activeView.getTag();
            if(position == pos){
                ZoomImageView reading_view =  activeView.findViewById(R.id.reading_view);
                if(reading_view.getmPageIndex() == pos){
                    return reading_view;
                }
            }
        }
        */

        //return null;
    }


    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.utility_view);
        if (f != null && f instanceof OverlayFragment) {
            if (((OverlayFragment) f).isThumbnailListVisible()) {
                ((OverlayFragment) f).hideThumbnailList();
                return;
            }
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            pageChange();
            invalidateOptionsMenu();
        } else {
            super.onBackPressed();
        }
    }

    private void hideThumbList() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.utility_view);
        if (f != null && f instanceof OverlayFragment) {
            if (((OverlayFragment) f).isThumbnailListVisible()) {
                ((OverlayFragment) f).hideThumbnailList();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHistoryList != null) {
            mHistoryList.clear();
            mHistoryList = null;
        }
    }

    @Override
    public void onClick(View view) {
        if (OCRSelection.getOCRSelection().ismVisible()) {
            return;
        }
        switch (view.getId()) {

            case R.id.iv_left:
                gotoPrevPage();
                break;

            case R.id.iv_right:
                gotoNextPage();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_BOOKMARK || requestCode == REQUEST_CODE_TOC) {
                String result = data.getStringExtra("result");
                int pageNum = Convert.toInt(result);
                jumpToPageNumber(pageNum, true);
            }

        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            mActionMode = actionMode;
            actionMode.getMenuInflater().inflate(R.menu.action_mode_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            hideOverlay();
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.item_done) {
                try {
                    clickOCR(OCRSelection.getOCRSelection().getPoints());
                    OCRSelection.getOCRSelection().clearPoints();
                    OCRSelection.getOCRSelection().setmVisible(false);
                    DrawingCanvas.getInstance().updateView();
                } catch (Exception e) {
                    LogUtil.d(e.getMessage());
                }
                actionMode.finish();
                mActionMode = null;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;
            OCRSelection.getOCRSelection().clearPoints();
            OCRSelection.getOCRSelection().setmVisible(false);
            DrawingCanvas.getInstance().updateView();
        }
    }
}
