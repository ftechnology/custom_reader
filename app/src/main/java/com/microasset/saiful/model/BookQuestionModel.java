package com.microasset.saiful.model;

import android.content.Context;

import com.microasset.saiful.appfrw.BaseModel;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.web.Firebase;

import java.io.File;

public class BookQuestionModel extends BaseModel {

    //
    static public BookQuestionModel mBookInfoModel;
    //
    private BookQuestionModel(Context context) {
        super(context);
    }


    public static BookQuestionModel getInstance(Context context) {
        if (mBookInfoModel == null)
            mBookInfoModel = new BookQuestionModel(context);

        mBookInfoModel.mContext = context;

        return mBookInfoModel;
    }

    @Override
    public ResponseObject execute() {
        download();
        return null;
    }

    @Override
    public synchronized ResponseObject executeAsyn(final NotifyObserver observer) {
        // Remove all from previous result..
        // DONT DO OVER TASK HERE...MAY BE THIS ITEMS WOULD BE REQUIRED IN IMPLEMENTED CLASS AS WELL.
        //this.clear(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mNotifyObserver = observer;
                mInstance.execute();

            }
        }).start();

        return null;
    }

    public void download(){
        Firebase firebase = new Firebase();
        DataObject object = new DataObject(0);
        String selectedClass = BookInfoModel.getInstance(mContext).getmSelectedClass();
        String selectedVersion = BookInfoModel.getInstance(mContext).getmSelectedVersion();
        String selectedBook = BookInfoModel.getInstance(mContext).getmSelectedBook();
        object.setValue("mSelectedBook", selectedBook + " - Q");
        object.setValue("mSelectedClass", selectedClass);
        object.setValue("mSelectedVersion", selectedVersion);
        firebase.setDownloadData(object);

        firebase.doDownloadFile(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(mNotifyObserver != null){
                    mNotifyObserver.update(new ResponseObject());
                }
            }
        });
    }

    public boolean loadQuestion(){
        if(hasQuestion()){
            return loadQuestionXml();
        }
        return false;
    }

    public boolean hasQuestion(){
        String selectedClass =  BookInfoModel.getInstance(mContext).getmSelectedClass();
        String selectedVersion =  BookInfoModel.getInstance(mContext).getmSelectedVersion();
        String selectedBook = BookInfoModel.getInstance(mContext).getmSelectedBook();

        final File file = new File(Constants.PATH_BOOK +"/"+selectedClass+selectedVersion+"/"+ selectedBook +" - Q/"+ selectedBook+ " - Q.xml");
        if(file.exists()){
            return true;
        }
        return false;
    }

    public boolean loadQuestionXml(){
        String selectedBook = BookInfoModel.getInstance(mContext).getmSelectedBook();
        BookInfoModel.getInstance(mContext).setmSelectedBook( selectedBook + " - Q");
        if( BookInfoModel.getInstance(mContext).getListItem().size() == 0){
            // As No questions we have to set Book Data..Again...
            BookInfoModel.getInstance(mContext).setmSelectedBook(selectedBook);
            return false;
        }
        return true;
    }
}
