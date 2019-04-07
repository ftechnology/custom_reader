package com.microasset.saiful.util;

import android.os.Environment;

public class Constants {
    public static String ROOT = "EasyReader";
    public static String DOWNLOAD_FOLDER = "Download";
    public static String BOOK = "book";
    public static String FOLDER_HOME_WORK = "homework";
    //
    public static String ABSPATH = "ABSPATH";
    public static String ACTIVITY_CONTEXT = "ActivityContext";
    public static final String SERVER_LINK = "";
    public static final String DOWNLOADED_APK_NAME = "base.apk";
    //
    public static final String PATH_ASSET = "asset";
    public static String PATH_SD = "memory";
    //
    public static final String PATH_APPLICAITON = Environment.getExternalStorageDirectory() + "/" + ROOT;
    public static final String PATH_BOOK = PATH_APPLICAITON + "/" + BOOK;
    public static final String PATH_DOWNLOAD = Environment.getExternalStorageDirectory() + "/" + ROOT + "/" + DOWNLOAD_FOLDER;
    public static final String PATH_STORAGE = Environment.getExternalStorageDirectory() + "";

    public static String KEY_PAGE_INDEX = "KEY_PAGE_INDEX";
    public static int SORT_BY_PAGE = 0;
    public static int SORT_BY_DATE = 1;
    public static String KEY_DATA_TYPE = "KEY_DATA_TYPE";
    public static String TYPE_BOOKMARK = "TYPE_BOOKMARK";
    public static String TYPE_DRAWING_OBJ = "TYPE_DRAWING_OBJ";
    //
    public static String COVER_IMAGES = "cover_images";
    public static String VALUE_VERSION_ENG = "E";
    //
    public static float SHAPE_SCALE_TO_IMAGE = 0.05f;

    public static int RECENTLY_OPEN_MAX_COUNT = 5;
    public static String YOUTUBE_VIDEO_ID = "jtZJ0XWIUOk";
    public static String WEBSITE_LINK = "https://www.google.com/";
    public static String FACEBOOK_PAGE_ID = "329116411068865";

    public static String YOUTUBE_API_KEY = "AIzaSyC1jpC-l5wyJgXyVFGgvmLDp4ZkqlLMmWs";
    //

    public static final int PAGE_EFFECT_SLIDE = 0;
    public static final int PAGE_EFFECT_BOOK_FLIP = 1;
    public static final int PAGE_EFFECT_STAKE = 2;
    public static final int PAGE_EFFECT_CUBE_IN = 3;
    public static final int PAGE_EFFECT_GRIP = 4;
    public static final int PAGE_EFFECT_FOREGROUND_BACKGROUND = 5;
}