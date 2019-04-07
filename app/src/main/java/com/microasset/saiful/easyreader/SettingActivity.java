package com.microasset.saiful.easyreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;

public class SettingActivity extends BaseActivity {
    private Toolbar mToolBar = null;
    private Spinner sp_language = null;
    String lanCode = "bn";
    public static String EXTRA_ISREADING = "EXTRA_ISREADING";

    @Override
    protected void createView(Bundle savedInstanceState) {
        this.setContentView(R.layout.settings_fragment);
        setupToolbar();
        setUpUIVisibility();
    }

    private void setUpUIVisibility(){
        boolean isFromReadingView = getIntent().getBooleanExtra(EXTRA_ISREADING, false);
        if(isFromReadingView){
            ((RelativeLayout)findViewById(R.id.rl_language)).setVisibility(View.GONE);
            ((View)findViewById(R.id.view_lan)).setVisibility(View.GONE);

            ((RelativeLayout)findViewById(R.id.rl_effect)).setVisibility(View.VISIBLE);
            ((View)findViewById(R.id.view_effect)).setVisibility(View.VISIBLE);
        }else{
            ((RelativeLayout)findViewById(R.id.rl_language)).setVisibility(View.VISIBLE);
            ((View)findViewById(R.id.view_lan)).setVisibility(View.VISIBLE);

            ((RelativeLayout)findViewById(R.id.rl_effect)).setVisibility(View.GONE);
            ((View)findViewById(R.id.view_effect)).setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_SETTINGS);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sp_language = (Spinner) findViewById(R.id.sp_language);

        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mInstance,
                R.array.language_arrays, R.layout.spinner_row);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_language.setAdapter(adapter);
        lanCode = SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE, "bn");
        if (lanCode.equals("bn")) {
            sp_language.setSelection(0);
        } else {
            sp_language.setSelection(1);
        }

        sp_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int position = sp_language.getSelectedItemPosition();
                if (position == 0) {
                    SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE, "bn");
                } else {
                    SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE, "en");
                }
                Utill.setLocale(SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE, "bn"), mInstance);
                updateUI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        initUpPageEffect();
    }

   private void initUpPageEffect(){
       final Spinner sp_effect = (Spinner) findViewById(R.id.sp_page_effect);

       final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mInstance,
               R.array.page_effect_arrays, R.layout.spinner_row);
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       sp_effect.setAdapter(adapter);
       int pageEffect = SharedPrefUtil.getIntSetting(mInstance, SharedPrefUtil.KEY_PAGE_EFFECT, 2);

       sp_effect.setSelection(pageEffect);


       sp_effect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               int position = sp_effect.getSelectedItemPosition();
               SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_PAGE_EFFECT, position);
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });
   }
    private void updateUI(){
        ((TextView)findViewById(R.id.tv_language)).setText(getString(R.string.STR_CHOOSE_LANGUAGE));
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_SETTINGS);
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
        if(!lanCode.equals(SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE,"bn"))){
            setResult(Activity.RESULT_OK, new Intent());
        }
        finish();
    }



}
