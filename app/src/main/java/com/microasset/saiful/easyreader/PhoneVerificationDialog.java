package com.microasset.saiful.easyreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.util.Constants;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;
import com.microasset.saiful.web.FirebaseDbMeta;


public class PhoneVerificationDialog  {

	public static void show(Context context, final NotifyObserver observer){
		final EditText txtUrl = new EditText(context);

		// Set the default text to a link of the Queen
		txtUrl.setHint("123456");

		new AlertDialog.Builder(context)
				.setTitle("OTP window")
				.setMessage("Enter OTP send to your phone!")
				.setView(txtUrl)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String url = txtUrl.getText().toString();
						observer.update(new ResponseObject(0, url, null));
					}
				})
				.show();
	}

}
