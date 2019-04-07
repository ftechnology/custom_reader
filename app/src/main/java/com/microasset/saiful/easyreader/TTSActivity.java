package com.microasset.saiful.easyreader;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;

import android.view.View;
import android.webkit.WebView;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.MLKit;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.model.DictionaryEngToBanglaModel;
import com.microasset.saiful.model.TTTModel;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;
import com.microasset.saiful.web.FirebaseDbMeta;

public class TTSActivity extends BaseActivity {

    /**
     * The visible WebView instance
     */
    private WebView webView;
    private ProgressDialog mDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tts_activity);
        setupToolbar();

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);

        // Add the custom WebViewClient class
        webView.setWebViewClient(new CustomWebViewClient());

        // Add the javascript interface
        webView.addJavascriptInterface(new JavaScriptInterface(), "interface");

        // Load the example html file to the WebView
        webView.loadUrl("file:///android_asset/index.html");
        //
        MLKit mlKit = MLKit.getInstance();
        EditText editText = this.findViewById(R.id.edittext);
        String text = "";

        if(mlKit.getFbVisionText() != null){
            editText.setText(mlKit.getFbVisionText().getText());
            text = mlKit.getFbVisionText().getText();
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Utill.setLocale(SharedPrefUtil.getSetting(this, SharedPrefUtil.KEY_LANGUAGE_CODE, "bn"), this);
        updateUI();
    }

    private void updateUI(){
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_TRANSLATION);
        ((TextView) findViewById(R.id.tv_1)).setText(R.string.STR_TRANSLATE);
        ((TextView) findViewById(R.id.tv_2)).setText(R.string.STR_VOICE);
    }

    private void setupToolbar() {
        Toolbar mToolBar = (Toolbar) findViewById(R.id.toolBar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_TRANSLATION);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    @Override
    protected void createView(Bundle savedInstanceState) {

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

    /**
     * Onclick callback method for Button.
     *
     * @param view
     */
    public void onButtonClick(final View view) {
        Utill.isOnlineAsync(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == -1){
                    Toast.makeText(mInstance, getString(R.string.STR_NO_INTERNET), Toast.LENGTH_LONG).show();
                    return;
                }
                doOnButtonClick(view);
            }
        });
    }

    public void doOnButtonClick(View view){
        EditText editText = this.findViewById(R.id.edittext);
        String text = editText.getText().toString();;

        if (text.isEmpty()) {
            Toast.makeText(this, getString(R.string.STR_INPUT_TEXT), Toast.LENGTH_LONG).show();
            return;
        }
        text = text.replace("'", "").trim();
        String script = "javascript:speak(" +"'"+ text+"'" + ");";
        webView.loadUrl(script);
    }

    public void onButtonTranslate(View v){
        Utill.hideKeyboard(this);
        EditText editText = this.findViewById(R.id.edittext);
        final String text = editText.getText().toString().trim();

        if (text.isEmpty()) {
            Toast.makeText(this, getString(R.string.STR_INPUT_TEXT), Toast.LENGTH_LONG).show();
            return;
        }

        try {
            //showProgressDialog();
            String key = SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_API_KEY);
            TTTModel ttsModel = new TTTModel(key, mInstance);
            ttsModel.translateText(text.trim(), "bn", new TTTModel.TTSListener() {
                @Override
                public void onTranslation(String nativeText, String translatedText) {
                    ((TextView)findViewById(R.id.tv_translate)).setText(translatedText);
                    //hideDialog();
                }
            });

        }catch (Exception e){
            //hideDialog();
            LogUtil.d(e.getMessage());
            //Toast.makeText(mInstance, getString(R.string.STR_NO_INTERNET), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Onclick callback method for Button.
     */
    public void onCallJSFunc(String function) {
        //function = "javascript:callFromAppWithReturn();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(function, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String s) {
                    //Toast.makeText(TTSActivity.this, s, Toast.LENGTH_LONG).show();
                    LogUtil.d(s);
                }
            });
        } else {
            Toast.makeText(TTSActivity.this, getString(R.string.STR_TRY_AGAIN), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * CustomWebViewClient is used to add a custom hook to the url loading.
     */
    private class CustomWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // If the url to be loaded starts with the custom protocol, skip
            // loading and do something else
            if (url.startsWith("tanelikorri://")) {
                //Toast.makeText(TTSActivity.this, "Custom protocol call", Toast.LENGTH_LONG).show();
                return true;
            }
            return false;
        }
    }

    /**
     * JavaScriptInterface is the interface class for the application code calls. All public methods
     * annotated with {@link android.webkit.JavascriptInterface JavascriptInterface } in this class
     * can be called from JavaScript.
     */
    private class JavaScriptInterface {

        @JavascriptInterface
        public void callFromJS() {
            //Toast.makeText(TTSActivity.this, "JavaScript interface call", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onCallJSFunc("javascript:cancel();");
    }

    private void showProgressDialog() {
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();;
            mDialog = null;
        }
        if (mDialog == null) {
            mDialog = new ProgressDialog(this);
        }
        mDialog.setMessage(getString(R.string.STR_LOADING));
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void hideDialog() {
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }

        mDialog = null;
    }
}
