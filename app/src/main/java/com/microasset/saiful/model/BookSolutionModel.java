package com.microasset.saiful.model;

import android.content.Context;

import com.microasset.saiful.appfrw.BaseModel;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.XMLParser;
import com.microasset.saiful.web.Firebase;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

public class BookSolutionModel extends BaseModel {

    //
    static public BookSolutionModel mBookInfoModel;
    //
    //
    public static final String id = "id";
    public static final String item = "item";
    public static final String link = "link";
    public static final String chapter = "chapter";
    public static final String href = "href";
    public static final String title = "title";

    private ArrayList mSelectedChapter;

    private BookSolutionModel(Context context) {
        super(context);
    }


    public static BookSolutionModel getInstance(Context context) {
        if (mBookInfoModel == null)
            mBookInfoModel = new BookSolutionModel(context);

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

    public void populateLinks(){
        try{
            XMLParser parser = new XMLParser();

            String selectedClass = BookInfoModel.getInstance(mContext).getmSelectedClass();
            String selectedVersion = BookInfoModel.getInstance(mContext).getmSelectedVersion();
            String selectedBook = BookInfoModel.getInstance(mContext).getmSelectedBook();
            //
            parser.loadXmlContentFromFile(mContext, Constants.PATH_BOOK+"/"+selectedClass+selectedVersion+"/"+selectedBook +"/"+selectedBook+" - link.xml");
            this.clear(false);
            //
            fillLinks(parser);
            // TODO: download if book doesn't exist/ download by chapters, this is just a test
            //downloadBook();
        }catch (Exception e){
            LogUtil.d(e.getMessage());
            this.clear(false);
        }
        if(mNotifyObserver != null){
            mNotifyObserver.update(new ResponseObject());
        }
    }

    public void download(){
        Firebase firebase = new Firebase();
        DataObject object = new DataObject(0);
        String selectedClass = BookInfoModel.getInstance(mContext).getmSelectedClass();
        String selectedVersion = BookInfoModel.getInstance(mContext).getmSelectedVersion();
        String selectedBook = BookInfoModel.getInstance(mContext).getmSelectedBook();
        object.setValue("mSelectedBook", selectedBook);
        object.setValue("mSelectedClass", selectedClass);
        object.setValue("mSelectedVersion", selectedVersion);
        firebase.setDownloadData(object);

        firebase.doDownloadSolution(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                //if(response.getResponseCode() == 0){
                    populateLinks();
                //}
            }
        });
    }

    public void fillLinks(XMLParser parser){
        //
        NodeList nodeList = parser.getElementsByTagName(link);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            Node nodeParent =   node.getParentNode();
            String link =  ((Element) node).getAttribute(href);
            String id =  ((Element) nodeParent).getAttribute(chapter);
            String ti =  ((Element) node).getAttribute(title);
            DataObject obj = new DataObject(-1);
            if(link != null && link.length() > 0){
                link = link.replaceAll("%","&");
            }
            obj.setValue(href, link );
            obj.setValue(chapter, id );
            obj.setValue(title, ti );
            this.add(obj);
        }

        //Test code...
        //getLinksByChapterIndex("3");
        //getLinksByChapterIndex("2");
        //getLinksByChapterIndex("1");
    }

    public void setmSelectedChapterList(ArrayList list) {
        this.mSelectedChapter = list;
    }

    public ArrayList<DataObject> getLinksByChapterIndex(String index){
        ArrayList<DataObject> list = new ArrayList<>();

        if(index == null || index.equals("")){
            return list;
        }

        for(int i = 0; i<this.getCount(); i++){

            DataObject object = (DataObject) this.getItem(i);
            if(object.getString(chapter).equals(index)){
                list.add(object);
            }
        }

        return list;
    }

    public ArrayList<DataObject> getAllLinks(){
        ArrayList<DataObject> list = new ArrayList<>();

        for(int i = 0; i<this.getCount(); i++){
            DataObject object = (DataObject) this.getItem(i);
            list.add(object);
        }

        return list;
    }

    public ArrayList<String> getAvailableChapters(){
        ArrayList<String> list = new ArrayList<>();
        String chapterName = null;

        for(int i = 0; i<this.getCount(); i++){
            DataObject object = (DataObject) this.getItem(i);
            if(chapterName == null){
                chapterName = object.getString(chapter);
                list.add(chapterName);
            }

            if(!chapterName.equals(object.getString(chapter))){
                chapterName = object.getString(chapter);
                list.add(chapterName);
            }
        }

        return list;
    }

    public ArrayList<DataObject> getSelectedChapterLinks(){
        return mSelectedChapter;
    }

    public ArrayList<DataObject> getLinksByChapterIndexOffline(String index){
        ArrayList<DataObject> list = new ArrayList<>();
        ArrayList<DataObject> listTotal = getLinksByChapterIndex(index);
        for (int i = 0; i<listTotal.size(); i++){
            DataObject object = listTotal.get(i);
            if(isVideoDownloaded(object.getString(href))){
                list.add(object);
            }
        }

        return list;
    }

    public boolean isVideoDownloaded(String videoId){
        final File file = new File(Constants.PATH_DOWNLOAD + "/" + videoId + ".mp4");
        return file.exists();

    }
}
