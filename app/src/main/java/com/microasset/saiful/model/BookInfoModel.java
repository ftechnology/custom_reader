package com.microasset.saiful.model;

import android.content.Context;
import android.util.Log;

import com.microasset.saiful.appfrw.BaseModel;
import com.microasset.saiful.appfrw.DataObject;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.FileUtil;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.XMLParser;
import com.microasset.saiful.web.Firebase;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.net.PortUnreachableException;
import java.util.ArrayList;

public class BookInfoModel extends BaseModel {

    //
    static public BookInfoModel mBookInfoModel;
    //
    protected String mSelectedBook = "";
    protected String mSelectedClass = "";
    protected String mSelectedVersion = "";
    protected String mImageLocation = "";
    //
    public static final String total = "total";
    public static final String meta = "meta";
    public static final String start = "start";
    public static final String page = "page";
    public static final String pageprefix = "pageprefix";
    public static final String index = "index";
    public static final String item = "item";
    public static final String link = "link";
    public static final String extention_value = ".er";
    public static final String free = "free";

    private BookInfoModel(Context context) {
        super(context);
    }
    //
    public class MetaData extends BaseModel{
        public MetaData(Context context){
            super(context);
            Free = "";
        }
        public  MetaData(){}
        public String TotalPage;
        public String StartPage;
        public String EndPage;
        public String PagePrefix;
        public String Free;

        @Override
        public ResponseObject execute() {

            TotalPage = "";
            StartPage = "";
            EndPage = "";
            PagePrefix = "";
            Free = "";
            return null;
        }
    }
    //
    public class BookIndex extends BaseModel{
        public BookIndex(Context context){
            super(context);

        }

        public  BookIndex(){}
        @Override
        public ResponseObject execute() {
            return null;
        }
    }
     //
    MetaData mMetaData = new MetaData(null);
    BookIndex mBookIndex = new BookIndex(null);

    public  MetaData getmMetaData(){
        return mMetaData;
    }
    public  BookIndex getmBookIndex(){return mBookIndex;}

    public static BookInfoModel getInstance(Context context) {
        if (mBookInfoModel == null)
            mBookInfoModel = new BookInfoModel(context);

        mBookInfoModel.mContext = context;

        return mBookInfoModel;
    }

    public void setmSelectedBook(String selectedBook) {
        this.mSelectedBook = selectedBook;
        // Load the Book info..
        this.execute();
    }

    public  String getmSelectedBook(){
        return this.mSelectedBook;
    }

    public void setmSelectedClass(String selectedClass){
        mSelectedClass = selectedClass;
    }

    public String getmSelectedClass(){
        return mSelectedClass;
    }

    public void setmSelectedVersion(String version){
        mSelectedVersion = version;
    }

    public String getmSelectedVersion(){
        return mSelectedVersion;
    }

    public  String getmImageLocation(){
        return this.mImageLocation;
    }

    public void setmImageLocation(String location){
        mImageLocation = location;
    }


    @Override
    public ResponseObject execute() {
        try{
            this.clear(false);
            //
            XMLParser parser = new XMLParser();
            if(getmSelectedBook().contains(" - Q")){
                parser.loadXmlContentFromFile(mContext, Constants.PATH_BOOK+"/"+mSelectedClass+mSelectedVersion+"/"+mSelectedBook+"/"+mSelectedBook+".xml");
            }else{
                parser.loadXmlContent(mContext, Constants.BOOK+"/"+mSelectedClass+mSelectedVersion+"/"+mSelectedBook +".xml");
            }

            //
            fillMeta(parser);
            fillIndex(parser);
            //
            int pageCnt = Convert.toInt(getmMetaData().TotalPage);
            //String prefix = getmMetaData().PagePrefix;
            constructPages(pageCnt);
            //String path = Constants.PATH_BOOK + "/"+ mBookName +"/"
            // TODO: download if book doesn't exist/ download by chapters, this is just a test
            //downloadBook();
            //BookSolutionModel.getInstance(mContext).execute();
        }catch (Exception e){
            LogUtil.d(e.getMessage());
            this.clear(false);
        }
        return null;
    }

    public void fillMeta(XMLParser parser){
        mMetaData.execute();
        mMetaData.clear(true);
        NodeList nodeList = parser.getElementsByTagName(meta);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String value = node.getNodeValue();

            NamedNodeMap attList = node.getAttributes();
            DataObject object = new DataObject(-1);
            //node.getNodeName()
            for (int a = 0; a < attList.getLength(); a++) {
                Node att = attList.item(a);
                //object.setValue(att.getNodeName(), att.getNodeValue());
                if(att.getNodeName().compareToIgnoreCase(page) == 0){
                    String val = att.getNodeValue();
                    mMetaData.StartPage = val.split(",")[0];
                    mMetaData.EndPage = val.split(",")[1];
                } else if(att.getNodeName().compareToIgnoreCase(pageprefix) ==0 ){
                    mMetaData.PagePrefix = att.getNodeValue();
                } else if(att.getNodeName().compareToIgnoreCase(total) ==0 ){
                    mMetaData.TotalPage = att.getNodeValue();
                }else if(att.getNodeName().compareToIgnoreCase(free) ==0 ){
                    mMetaData.Free = att.getNodeValue();
                }
            }

            if(mMetaData.Free == null ){
                mMetaData.Free = "";
            }
        }
    }

    public void fillIndex(XMLParser parser){
        mBookIndex.clear(true);
        //
        NodeList nodeList = parser.getElementsByTagName(item);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String value = node.getNodeValue();
            NamedNodeMap attList = node.getAttributes();
            DataObject obj = new DataObject(-1);
            for (int a = 0; a < attList.getLength(); a++) {
                Node att = attList.item(a);
                obj.setValue(att.getNodeName(), att.getNodeValue());
                //mListItem.add(obj);
            }
            mBookIndex.add(obj);
        }
    }

    public void fillLinks(XMLParser parser){
        //
        NodeList nodeList = parser.getElementsByTagName(link);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String value = node.getNodeValue();
            NamedNodeMap attList = node.getAttributes();
            DataObject obj = new DataObject(-1);
            for (int a = 0; a < attList.getLength(); a++) {
                Node att = attList.item(a);
                obj.setValue(att.getNodeName(), att.getNodeValue());
                //mListItem.add(obj);
            }
            //mBookIndex.add(obj);
        }
    }

    public  void constructPages(int pageCnt){
        /*1->001-9->009
        10->010-11->011
        100->100-110->110
        */
        // LET MAX PAGE 10000....
        int maxPrefix = 0;
        if(pageCnt > 10 && pageCnt < 100){
            maxPrefix = 1;
        } else if(pageCnt > 99 && pageCnt < 1000){
            maxPrefix = 2;
        }else if(pageCnt > 999 && pageCnt < 10000){
            maxPrefix = 3;
        }
        int pageNumber = 0;
        String pageImage = "";
        for (int i = 0; i<pageCnt; i++) {
            pageNumber = i;

            //String[] st = listItem.getName().split("\\.");
            DataObject obj = new DataObject(pageNumber);
            //obj.setValue("DESCRIPTION", st[0]);
            obj.setValue("IMAGE_FROM", "FILE");
            obj.setValue("PAGE_NUM",pageNumber);
            //Primary - 2018 - (B.Version.) - Class-5 Bangla PDF Web-001.er
            //pageImage = mBookName + i;
            // TODO FIXME page number issue ..
            pageImage = convertPageNumberToPageImage(pageNumber + 1, maxPrefix);

            pageImage = mSelectedBook+ "-"+pageImage + extention_value;
            obj.setValue("PAGE_IMAGE",pageImage);
            //
            String bookPath = mSelectedClass + mSelectedVersion +"/"+ mSelectedBook;
            String path = Constants.PATH_BOOK + "/"+ bookPath +"/"+pageImage;
            obj.setValue("PAGE_IMAGE_PATH",path);
            LogUtil.d(path);
            this.add(obj);
        }
    }

    public  String convertPageNumberToPageImage(int pageNumber, int maxPrefix){
        String pageImage = "";
        if(pageNumber < 10)
        {
            pageImage = Convert.toString(pageNumber);
            if(maxPrefix == 1){
                pageImage = "0" + Convert.toString(pageNumber);
            } else if(maxPrefix == 2){
                pageImage = "00" + Convert.toString(pageNumber);
            }else if(maxPrefix == 3){
                pageImage = "000" + Convert.toString(pageNumber);
            }

        } else if(pageNumber >9 && pageNumber < 100){
            pageImage = Convert.toString(pageNumber);
            if(maxPrefix == 2){
                pageImage = "0" + Convert.toString(pageNumber);
            } else if(maxPrefix == 3){
                pageImage = "00" + Convert.toString(pageNumber);
            }
        }else if(pageNumber >99 && pageNumber < 999){
            pageImage = Convert.toString(pageNumber);
        }else if(pageNumber >999 && pageNumber < 10000){
            pageImage = Convert.toString(pageNumber);
            if(maxPrefix == 3){
                pageImage = "00" + Convert.toString(pageNumber);
            }
        }

        return pageImage;
    }

    public boolean isSinglePgChapterAvailable(int index){

        try {
            DataObject object = (DataObject) this.getItem(index - 1);
            String path = (String) object.getValue("PAGE_IMAGE_PATH");
            if (path != null) {
                if (!FileUtil.exists(path)) {
                    return false;
                }
            }
        }catch (Exception e){
            return false;
        }

        return true;
    }

    public boolean isChapterAvailable(int startIndex, int endIndex){

        if(endIndex >= this.getCount()) {
            LogUtil.d(" if(startIndex < 0 || endIndex >= this.getCount())");
            return false;
        }
        // TODO: endIndex should be inclusive
        startIndex = startIndex + Convert.toInt(mMetaData.StartPage);
        endIndex = endIndex + Convert.toInt(mMetaData.StartPage);

        if(startIndex == endIndex){
            return isSinglePgChapterAvailable(startIndex);
        }

        // java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.Object com.microasset.saiful.appfrw.DataObject.getValue(java.lang.String)' on a null object reference
        //
        try {
            for (int i = startIndex; i < endIndex; i++) {
                DataObject object = (DataObject) this.getItem(i);
                String path = (String) object.getValue("PAGE_IMAGE_PATH");
                if (path != null) {
                    LogUtil.d(path);
                    if (!FileUtil.exists(path)) {
                        return false;
                    }
                }
            }
        }catch (Exception e){
            return false;
        }

        return true;
    }

    /**
     * download images for this chapter
     */
    public void downloadChapter(int startIndex, int endIndex, NotifyObserver notifyObserver) {
        // -1 because it is stored in databased using 1-based indexing
        startIndex = startIndex + Convert.toInt(mMetaData.StartPage) - 1;
        endIndex = endIndex + Convert.toInt(mMetaData.StartPage);
        Firebase firebase = new Firebase();
        DataObject object = new DataObject(0);
        object.setValue("mSelectedBook", mSelectedBook);
        object.setValue("mSelectedClass", mSelectedClass);
        object.setValue("mSelectedVersion", mSelectedVersion);
        firebase.setDownloadData(object);
        //
        String user =  SharedPrefUtil.getSetting(mContext, SharedPrefUtil.KEY_USER_ID);
        String password =  SharedPrefUtil.getSetting(mContext, SharedPrefUtil.KEY_USER_PSW);

        firebase.downloadFiles(user, password, mListItem, startIndex, endIndex, notifyObserver);
    }

    public boolean isAnyChapterAvailable(){

        try{
            ArrayList<DataObject> list = mBookIndex.getListItem();
            int startIndex =  Convert.toInt(mMetaData.StartPage) - 1;

            for (int i=0;i<list.size(); i++){
                DataObject object = (DataObject) mBookIndex.getItem(i);
                String index = (String)object.getValue("index");
                int start = Convert.toInt(index.split(",")[0]);
                int end = Convert.toInt(index.split(",")[1]);
                if(isChapterAvailable(start , end)){
                    return true;
                }
            }
        }
        catch (Exception e){
            LogUtil.d(e.getMessage());
        }
        return false;
    }

    public boolean isAllChapterAvailable(){
        try{
            ArrayList<DataObject> list = mBookIndex.getListItem();
            int startIndex =  Convert.toInt(mMetaData.StartPage) - 1;

            DataObject object = (DataObject) mBookIndex.getItem(0);
            String index = (String)object.getValue("index");
            int start = Convert.toInt(index.split(",")[0]);
            object = (DataObject) mBookIndex.getItem(list.size() - 1);
            index = (String)object.getValue("index");
            int end = Convert.toInt(index.split(",")[1]);
            if(isChapterAvailable(start , end)){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public String getChapterTitleByIndex(int pageIndex){
        String titile = "";
        int cpIndex = getChapterPosByIndex(pageIndex);
        if(cpIndex > -1){
            DataObject object = mBookIndex.getListItem().get(cpIndex);
            titile = (String)object.getValue("title");
        }
        return titile;
    }

    public int getChapterPosByIndex(int pageIndex){
        int startIndex = Convert.toInt(mMetaData.StartPage);
            for(int i = 0; i < mBookIndex.getListItem().size(); i++){
                DataObject object = mBookIndex.getListItem().get(i);
                String index = (String)object.getValue("index");
                String[] split = index.split(",");
                int sPos = Convert.toInt(split[0]);
                int ePos = Convert.toInt(split[1]);
                int actualIndex = pageIndex - startIndex;
                if (actualIndex >= sPos && actualIndex <= ePos ){
                    return i;
                }
            }
        return -1;
    }
}
