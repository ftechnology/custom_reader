package com.microasset.saiful.easyreader;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.ResponseObject;


public class DisclaimerActivty extends BaseActivity {
    private Toolbar mToolBar = null;


    @Override
    protected void createView(Bundle savedInstanceState) {
        this.setContentView(R.layout.disclaimer_layout);
        setupToolbar();

        WebView mEulaWebView = (WebView) findViewById(R.id.eula_webview);
        String eulaUrl = "file:///android_asset/legal.html";
        mEulaWebView.loadUrl(eulaUrl);

    }

    private void setupToolbar() {
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_DISCLAIMER);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Create the adapter
     */
    @Override
    protected void createAdapter() {
        // TODO Auto-generated method stub
    }

    /**
     * Directly call the select image menu
     */
    @Override
    protected void loadData() {
    }

    @Override
    public void doUpdateRequest(ResponseObject response) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
