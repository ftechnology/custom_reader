package com.microasset.saiful.easyreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;
import com.microasset.saiful.web.FirebaseDbMeta;


public class AboutActivity extends BaseActivity {
	private Toolbar mToolBar = null;
	TextView tv_version, tv_number;

	@Override
	protected void createView(Bundle savedInstanceState) {
		this.setContentView(R.layout.about_layout);
		setupToolbar();
		tv_version = (TextView)findViewById(R.id.tv_version);
		tv_version.setText(BuildConfig.VERSION_NAME);

		FirebaseDbMeta.getInstance().getMeta(new NotifyObserver() {
			@Override
			public void update(ResponseObject response) {
				FirebaseDbMeta.Meta meta =  (FirebaseDbMeta.Meta)response.getDataObject();
				SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_CALL_CENTER_NUMBER, meta.phone);
				SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_CALL_CENTER_EMAIL, meta.email);
				SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_PAYMENT_NUMBER, meta.payment_no);
				SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_PRICE, meta.price);
			}
		});

		tv_number = (TextView)findViewById(R.id.tv_call_center_number);
		tv_number.setText(SharedPrefUtil.getSetting(this, SharedPrefUtil.KEY_CALL_CENTER_NUMBER, "01712965528"));
	}

	private void setupToolbar() {
		mToolBar = (Toolbar) findViewById(R.id.toolBar);
		if (mToolBar != null) {
			setSupportActionBar(mToolBar);
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("");
		((TextView) findViewById(R.id.tv_top)).setText(R.string.drawer_item_2);

		mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	public void clickHelp(View v){
		Utill.openYoutubeLink(this, Constants.YOUTUBE_VIDEO_ID);
	}

	public void clickFaceBookPage(View v){
		Utill.openFacebookPage(this, Constants.FACEBOOK_PAGE_ID);
	}

	public void clickWebSite(View v){
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.WEBSITE_LINK));
		startActivity(browserIntent);
	}

	public void clickShareApp(View v){
		ShareCompat.IntentBuilder.from(this)
				.setType("text/plain")
				.setSubject(getString(R.string.app_name))
				.setChooserTitle("Share via")
				.setText("http://play.google.com/store/apps/details?id=" + this.getPackageName())
				.startChooser();

	}

	public void clickDisclaimer(View v){
		startActivity(new Intent(this, DisclaimerActivty.class));
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
