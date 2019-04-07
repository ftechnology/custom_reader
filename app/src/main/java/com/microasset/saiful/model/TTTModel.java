package com.microasset.saiful.model;
import android.content.Context;
import android.os.Handler;
import com.google.cloud.translate.*;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.Utill;

public class TTTModel extends Object {
    final Handler textViewHandler = new Handler();
    TTSListener mTranlationListener;
    private String API_KEY = "AIzaSyDcHIiMfsrT1ntZBjuykppT2j7Z_LyYd1c";
    private Context mContext;

    public interface TTSListener {
        public void onTranslation(String nativeText, String translatedText);
    }

    public TTTModel(String api, Context context) {
        if(api != null && api.length() > 1){
            API_KEY = api;
        }
        mContext = context;
    }

    public void translateText(final String text, final String tgtLang, TTSListener listener) {
        mTranlationListener = listener;
        //Check Dictionary...
        if(text.split(" ").length == 1){
            String newtxt = DictionaryEngToBanglaModel.getInstance(null).searchItem(text.trim());
            if(newtxt.length() > 0){
                updateView(text, newtxt);
                return;
            }
        }

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TranslateOptions options = TranslateOptions.newBuilder()
                            .setApiKey(API_KEY)
                            .build();
                    // Instantiates a client
                    Translate translate = options.getService();
                    try {
                        final Translation translation =
                                translate.translate(
                                        text,
                                        Translate.TranslateOption.sourceLanguage("en"),
                                        Translate.TranslateOption.targetLanguage(tgtLang));
                                updateView(text, translation.getTranslatedText());

                    } catch (Exception e) {
                        updateView(text, null);
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        catch (Exception e){
            updateView(text, null);
            LogUtil.d(e.getMessage());
        }
    }

    public void updateView(final String text, final String translation){
        textViewHandler.post(new Runnable() {
            @Override
            public void run() {
                if(translation == null || translation.length() == 0){
                    mTranlationListener.onTranslation(text, "");
                } else {
                    mTranlationListener.onTranslation(text, translation);
                }
            }
        });
    }
}
