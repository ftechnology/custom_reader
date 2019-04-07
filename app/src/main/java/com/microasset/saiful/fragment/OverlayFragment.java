package com.microasset.saiful.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.color.PaletteBar;
import com.microasset.saiful.drawings.DrawingCanvas;
import com.microasset.saiful.easyreader.PaymentActivity;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.easyreader.ReadingViewActivity;
import com.microasset.saiful.entity.BookEntity;
import com.microasset.saiful.entity.HomeWorkEntity;
import com.microasset.saiful.licence.PaymentManager;
import com.microasset.saiful.licence.RegistrationManager;
import com.microasset.saiful.model.BookInfoModel;
import com.microasset.saiful.model.BookmarkDbModel;
import com.microasset.saiful.model.HomeWorkDbModel;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.InputFilterMinMax;
import com.microasset.saiful.util.Utill;
import com.microasset.saiful.view.NegativeSeekBar;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class OverlayFragment extends Fragment implements View.OnTouchListener, View.OnClickListener {

    private static final String TAG = OverlayFragment.class.getSimpleName();

    public OverlayFragment() {
        // Required empty public constructor
    }

    public static OverlayFragment getInstance() {
        OverlayFragment fragment = new OverlayFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private View mView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_overlay, container, false);
        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mView != null) {
            // Hide the Panel
            mView.setVisibility(View.GONE);
            mView = null;
        }
    }

    public View getOverlayView() {
        return mView;
    }

    private ToggleButton mBookmark, mHomework;
    private NegativeSeekBar mSeekBar;


    private TextView mTextPageNumber, mTextTotalPageNumber;
    private TextView mTextViewPageCalculation;
    private ViewGroup mPageNumberPanel;

    private TextView tv_chapter;

    RelativeLayout iv_toc_list, iv_bookmark_list, iv_paint, iv_history_back, iv_thumbnail,iv_drawing_list;
    RelativeLayout iv_paint_close;
    ImageView view_history, iv_settings;

    int mStartIndex = 0;

    private ThumbnailListAdapter mThumbnailListAdapter;
    private RecyclerView thumbnailRecyclerView;
    LinearLayout ln_thumbnail_list;

    RelativeLayout rl_main_overlay, rl_paint_overay;
    PaletteBar id_paletteBar;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnTouchListener(this);

        rl_main_overlay = (RelativeLayout)view.findViewById(R.id.rl_main_overlay);
        rl_paint_overay =  (RelativeLayout)view.findViewById(R.id.rl_paint_overay);
        view_history = (ImageView)view.findViewById(R.id.view_history);

        tv_chapter = (TextView) view.findViewById(R.id.tv_chapter);
        iv_toc_list = view.findViewById(R.id.iv_toclist);
        iv_toc_list.setOnClickListener(this);

        iv_bookmark_list = view.findViewById(R.id.iv_bookmark_list);
        iv_bookmark_list.setOnClickListener(this);

        iv_drawing_list = view.findViewById(R.id.iv_paint_list);
        iv_drawing_list.setOnClickListener(this);


        iv_paint_close = view.findViewById(R.id.iv_paint_close);
        iv_paint_close.setOnClickListener(this);

        iv_settings = view.findViewById(R.id.iv_settings);
        iv_settings.setOnClickListener(this);

        iv_paint = view.findViewById(R.id.iv_paint);
        iv_paint.setOnClickListener(this);

        iv_history_back = view.findViewById(R.id.iv_history);
        iv_history_back.setOnClickListener(this);

        iv_thumbnail = view.findViewById(R.id.iv_thumbnail);
        iv_thumbnail.setOnClickListener(this);
        //
        View id_btn_delete = view.findViewById(R.id.id_btn_delete);
        id_btn_delete.setOnClickListener(this);
        View id_btn_retangle = view.findViewById(R.id.id_btn_retangle);
        id_btn_retangle.setOnClickListener(this);
        View id_btn_cross = view.findViewById(R.id.id_btn_cross);
        id_btn_cross.setOnClickListener(this);
        View id_btn_yes = view.findViewById(R.id.id_btn_yes);
        id_btn_yes.setOnClickListener(this);
        View id_btn_color = view.findViewById(R.id.iv_color);
        id_btn_color.setOnClickListener(this);


        View page_num_view = view.findViewById(R.id.page_num_view);
        page_num_view.setOnClickListener(this);

        ln_thumbnail_list = view.findViewById(R.id.ln_thum);

        id_paletteBar = (PaletteBar) view.findViewById(R.id.id_paletteBar);
        DrawingCanvas.getInstance().setPaletteBar(id_paletteBar);

        View v;
        v = view.findViewById(R.id.bookmarkBtn);
        if (v != null && v instanceof ToggleButton) {
            mBookmark = (ToggleButton) v;
            mBookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(!RegistrationManager.getInstance(getActivity()).isAllow(false)){
                        doPayment();
                        mBookmark.setChecked(false);
                        return;
                    }
                    if (mReaderActivity != null) {
                        mReaderActivity.bookmarkButtonClick();
                    }
                }
            });
        }

        v = view.findViewById(R.id.homeworkBtn);
        if (v != null && v instanceof ToggleButton) {
            mHomework = (ToggleButton) v;
            mHomework.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mReaderActivity != null) {

                        if(!RegistrationManager.getInstance(mReaderActivity).isAllow(false)){
                            doPayment();
                            return;
                        }
                        mReaderActivity.homeWorkButtonClick();
                    }
                }
            });
        }

        mTextPageNumber = (TextView) view.findViewById(R.id.current_page_num);
        mTextTotalPageNumber = (TextView) view.findViewById(R.id.max_page_num);
        mTextViewPageCalculation = new TextView(view.getContext(), null, android.R.attr.textAppearanceMedium);
        mTextViewPageCalculation.setText("...");
        mTextViewPageCalculation.setPadding(0, 0, 4, 4);
        mTextViewPageCalculation.setGravity(Gravity.CENTER);
        mPageNumberPanel = (ViewGroup) view.findViewById(R.id.page_num_view);

        mStartIndex = Convert.toInt(BookInfoModel.getInstance(getContext()).getmMetaData().StartPage);

        v = view.findViewById(R.id.seekbar);
        if (v != null && v instanceof NegativeSeekBar) {
            mSeekBar = (NegativeSeekBar) v;

            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                    if (b && mTextPageNumber != null) {
                        mTextPageNumber.setText(String.valueOf(progress));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    setViewVisibility(mPageNumberPanel, true);
                    // update page num visibility
                    updatePageNumVisibility();
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mReaderActivity != null) {
                        int progress = seekBar.getProgress() - mStartIndex;
                        mReaderActivity.jumpToPageNumber(progress + mStartIndex, true);
                    }
                }
            });

        }

        //setup thumbnail list
        thumbnailRecyclerView = (RecyclerView) view.findViewById(R.id.thumbnail_recycler_view);
        if (thumbnailRecyclerView != null) {
            thumbnailRecyclerView.setVisibility(View.GONE);
            mThumbnailListAdapter = new ThumbnailListAdapter(getContext());
            thumbnailRecyclerView.setAdapter(mThumbnailListAdapter);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            thumbnailRecyclerView.setLayoutManager(horizontalLayoutManager);
        }

        if (mReaderActivity != null) {
            setTotalPageCounts(mReaderActivity.getTotalPage());
            updateUI();
        }

        if (DrawingCanvas.getInstance().getmSelected() != null) {
            togglePaintOverlay();
        }
    }

    public void togglePaintOverlay(){
        if(rl_paint_overay.getVisibility() == View.VISIBLE){
            rl_paint_overay.setVisibility(View.GONE);
            rl_main_overlay.setVisibility(View.VISIBLE);
        }else{
            rl_paint_overay.setVisibility(View.VISIBLE);
            rl_main_overlay.setVisibility(View.GONE);
        }
    }

    public void toggleColorPlate(){
        if(id_paletteBar.getVisibility() == View.VISIBLE){
            Utill.hideViewWithAnimation(getActivity(), R.anim.bottom_down, id_paletteBar);
        }else{
            Utill.showViewWithAnimation(getActivity(), R.anim.bottom_up, id_paletteBar);
        }
    }

    private void bringCurrentThumbnailAtCenter() {
        if (mReaderActivity == null || thumbnailRecyclerView == null) {
            return;
        }

        LinearLayoutManager layoutManager = ((LinearLayoutManager) thumbnailRecyclerView.getLayoutManager());
        int totalVisibleItems = layoutManager.findLastVisibleItemPosition() - layoutManager.findFirstVisibleItemPosition();
        int centeredItemPosition = totalVisibleItems / 2;
        int pagePosition = mReaderActivity.getCurrentPage() - 1;
        thumbnailRecyclerView.scrollToPosition(pagePosition);
        thumbnailRecyclerView.setScrollY(centeredItemPosition);
    }

    private void pageJumpDialog() {
        int min = 1 - mStartIndex;
        int max = mReaderActivity.getTotalPage() - mStartIndex;
        final EditText taskEditText = new EditText(getActivity());
        taskEditText.setHint(getActivity().getString(R.string.STR_ENTER_JUMP_NUMBER));
        taskEditText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);
        taskEditText.setFilters(new InputFilter[]{ new InputFilterMinMax(min, max)});
        String message = getActivity().getString(R.string.STR_JUMP_PAGE) + " : " + min + "/" + max;
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("")
               .setMessage(message)
                .setView(taskEditText)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pageNum = Convert.toInt(String.valueOf(taskEditText.getText()));
                        mReaderActivity.jumpToPageNumber((pageNum - 1) + mStartIndex, true);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        taskEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence c, int i, int i2, int i3) {}
            @Override public void onTextChanged(CharSequence c, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // Will be called AFTER text has been changed.
                if (editable.toString().length() == 0 || editable.toString().equals("-")){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });

        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    class ThumbnailListAdapter extends RecyclerView.Adapter<ThumbnailListAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        List<DataObject> mListItem = new ArrayList<>();

        // data is passed into the constructor
        public ThumbnailListAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
            mListItem = BookInfoModel.getInstance(context).getListItem();
        }

        // inflates the row layout from xml when needed
        @Override
        @NonNull
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.thumbnial_item_layout, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the view and textview in each row
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            holder.tvPageNumber.setText("" + ((position + 1) - mStartIndex));
            File files = new File(getPageImagePath(position));
            if(!files.exists()){
                holder.imgThumbnail.setImageResource(R.drawable.ic_empty_bookmark);
            }else{
                Picasso.get()
                        .load(new File(getPageImagePath(position)))
                        .resize(250, 300)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(holder.imgThumbnail);
            }

            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mReaderActivity != null) {
                        mReaderActivity.jumpToPageNumber(position, true);
                    }
                }
            });
        }

        public String getPageImagePath(int position) {
            return mListItem.get(position).getValue("PAGE_IMAGE_PATH").toString();
        }

        public String getImageFrom(int position) {
            return String.valueOf(mListItem.get(position).getValue("IMAGE_FROM"));
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mListItem.size();
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder {
            View rootView;
            TextView tvPageNumber;
            ImageView imgThumbnail;


            ViewHolder(View itemView) {
                super(itemView);
                rootView = itemView;
                tvPageNumber = itemView.findViewById(R.id.tvPageNumber);
                imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
            }
        }
    }

    private ViewGroup.MarginLayoutParams getLayoutParams(View view) {
        return (view != null) ? (ViewGroup.MarginLayoutParams) view.getLayoutParams() : null;
    }


    /*****
     * Toggle visibility of thumbnail recycler view
     */
    public void toggleThumbnailListVisibility() {
        if (thumbnailRecyclerView == null) {
            return;
        }
        if (isThumbnailListVisible()) {
            hideThumbnailList();
        } else {
            Utill.showViewWithAnimation(getActivity(), R.anim.bottom_up, ln_thumbnail_list);
            thumbnailRecyclerView.setVisibility(View.VISIBLE);
            thumbnailRecyclerView.post(new Runnable() {
                @Override
                public void run() {
                    bringCurrentThumbnailAtCenter();
                }
            });
        }
    }

    public boolean isThumbnailListVisible() {
        return thumbnailRecyclerView != null && thumbnailRecyclerView.getVisibility() == View.VISIBLE;
    }

    public void hideThumbnailList() {
        Utill.hideViewWithAnimation(getActivity(), R.anim.bottom_down, thumbnailRecyclerView);
    }

    public void updateUI() {
        updateBookmarkButton();
        updateHomeworkButton();
        if (mReaderActivity.hasHistory()) {
            view_history.setImageResource(R.drawable.ic_history_active);
        } else {
            view_history.setImageResource(R.drawable.ic_history_inactive);
        }
        int currentPage = mReaderActivity.getCurrentPage();
        setCurrentPageNumber(currentPage);

        String chapterTitle = BookInfoModel.getInstance(mReaderActivity).getChapterTitleByIndex(currentPage);
        if (chapterTitle.isEmpty()) {
            tv_chapter.setVisibility(View.GONE);
        } else {
            tv_chapter.setVisibility(View.VISIBLE);
            tv_chapter.setText(chapterTitle);
        }

        if (isThumbnailListVisible()) {
            bringCurrentThumbnailAtCenter();
        }
    }

    private void updateBookmarkButton() {
        if (mBookmark != null) {
            int pos = mReaderActivity.getCurrentPage() - 1;
            if (hasBookMark(pos)) {
                mBookmark.setChecked(true);
            } else {
                mBookmark.setChecked(false);
            }
        }
    }

    private void updateHomeworkButton() {
        if (mHomework != null) {
            int pos = mReaderActivity.getCurrentPage() - 1;
            if (hasHomework(pos)) {
                mHomework.setChecked(true);
            } else {
                mHomework.setChecked(false);
            }
        }
    }

    public boolean hasBookMark(int position) {
        //return (boolean)mListItem.get(position).getValue("HAS_BOOKMARKED");
        BookEntity entity = BookmarkDbModel.getInstance(mReaderActivity).hasBookMark(BookInfoModel.getInstance(mReaderActivity).getmSelectedBook(), String.valueOf(position));
        if (entity != null) {
            return true;
        }
        return false;
    }

    public boolean hasHomework(int position) {
        HomeWorkEntity entity = HomeWorkDbModel.getInstance(mReaderActivity).hasHomework(BookInfoModel.getInstance(mReaderActivity).getmSelectedBook(), String.valueOf(position));
        if (entity != null) {
            return true;
        }
        return false;
    }

    static void setViewVisibility(View view, boolean visibility) {
        if (null == view) {
            return;
        }

        if (visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private ReadingViewActivity mReaderActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof ReadingViewActivity) {
            mReaderActivity = (ReadingViewActivity) context;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                if (mReaderActivity != null) {
//                    mReaderActivity.showOverlayUx(false);
//                }
                break;
        }

        return false;
    }

    private int mTotalPageCounts = 0;

    private void setTotalPageCounts(int totalPageCounts) {
        mTotalPageCounts = totalPageCounts;

        if (mTextTotalPageNumber != null) {
            if (0 < totalPageCounts) {
                mTextTotalPageNumber.setText(String.valueOf(totalPageCounts - mStartIndex));
            }
        }

        if (mSeekBar != null) {
            mSeekBar.setMax(totalPageCounts - mStartIndex);
            mSeekBar.setMin(-(mStartIndex - 1));
        }
    }

    private void setCurrentPageNumber(int pageNumber) {

        if (mTextPageNumber != null) {
            mTextPageNumber.setText(String.valueOf(pageNumber - mStartIndex));
        }

        if (mSeekBar != null) {
            mSeekBar.setProgress(pageNumber);
        }
    }

    private boolean isLandscape() {
        return (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
    }

    private static boolean isChildView(ViewGroup parent, View child) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i).equals(child)) {
                return true;
            }
        }
        return false;
    }

    private void updatePageNumVisibility() {

        boolean isPageCalculating = false;
        if (mTotalPageCounts < 0) {
            isPageCalculating = true;
        } else {
            isPageCalculating = false;
        }

        if (null != mPageNumberPanel) {

            if (mPageNumberPanel.getVisibility() != View.VISIBLE) {
                return;
            }

            for (int i = 0; i < mPageNumberPanel.getChildCount(); i++) {
                // update page num text (xx/xx) by page calculation status
                mPageNumberPanel.getChildAt(i).setVisibility(isPageCalculating ? View.GONE : View.VISIBLE);
            }

            // add/remove page calculation text view
            if (isPageCalculating) {
                mTextViewPageCalculation.setVisibility(View.VISIBLE);
                if (!isChildView(mPageNumberPanel, mTextViewPageCalculation)) {
                    mPageNumberPanel.addView(mTextViewPageCalculation);
                }
            } else {
                if (isChildView(mPageNumberPanel, mTextViewPageCalculation)) {
                    mPageNumberPanel.removeView(mTextViewPageCalculation);
                }
            }
        }
    }

    private void doPayment(){
        Utill.isOnlineAsync(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == -1){
                    Toast.makeText(getActivity(), getString(R.string.STR_NO_INTERNET), Toast.LENGTH_LONG).show();
                    return;
                }
                PaymentManager.getInstance(getActivity()).doPayment();
            }
        });
    }

    @Override
    public void onClick(View view) {

        if(!RegistrationManager.getInstance(mReaderActivity).isAllow(false)){

            if(     view.getId()== R.id.iv_color ||
                    view.getId()== R.id.iv_paint ||
                    view.getId()== R.id.iv_thumbnail ||
                    view.getId()== R.id.iv_paint_list ||
                    view.getId()== R.id.iv_history
                    /*view.getId() == R.id.iv_OCR*/
                    ){
                doPayment();
                return;
            }
        }

        switch (view.getId()) {
            case R.id.iv_toclist:
                if (mReaderActivity != null) {
                    mReaderActivity.clickTocList();
                }
                break;
            case R.id.id_btn_delete:
                if (mReaderActivity != null) {
                    mReaderActivity.clickBtnDelete();
                }
                break;
            case R.id.id_btn_retangle:
                if (mReaderActivity != null) {
                    mReaderActivity.clickBtnRectangle();
                }
                break;
            case R.id.id_btn_cross:
                if (mReaderActivity != null) {
                    mReaderActivity.clickBtnCross();
                }
                break;
            case R.id.id_btn_yes:
                if (mReaderActivity != null) {
                    mReaderActivity.clickBtnYes();
                }
                break;

            case R.id.iv_color:
                toggleColorPlate();
                break;

            case R.id.page_num_view:
                if (mReaderActivity != null) {
                   pageJumpDialog();
                }
                break;
            case R.id.iv_bookmark_list:
                if (mReaderActivity != null) {
                    mReaderActivity.clickBookmarkList();
                }
                break;

            case R.id.iv_settings:
                if (mReaderActivity != null) {
                    mReaderActivity.clickSettings();
                }
                break;

            case R.id.iv_paint:
                if (mReaderActivity != null) {
                    togglePaintOverlay();
                    mReaderActivity.clickPaint();
                }
                break;

            case R.id.iv_paint_close:
                togglePaintOverlay();
                break;

            case R.id.iv_history:
                if (mReaderActivity != null && mReaderActivity.hasHistory()) {
                    mReaderActivity.clickHistoryBack();
                }
                break;

            case R.id.iv_thumbnail:
                toggleThumbnailListVisibility();
                break;

            case R.id.iv_paint_list:
                if (mReaderActivity != null) {
                    mReaderActivity.clickDrawingList();
                }
                break;

            default:
                break;
        }
    }
}
