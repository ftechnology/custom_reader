package com.microasset.saiful.appfrw;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.easyreader.TTSActivity;

public class TranslateManager extends Object {

    protected Activity mActivity;

    private TranslateManager(Activity activity) {
        mActivity = activity;
    }

    public static TranslateManager getInstance(Activity activity) {
        return new TranslateManager(activity);
    }

    public void installGoogleTranslator(Activity activity){
        final String appPackageName = "com.google.android.apps.translate";
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public boolean tryUsingGoogle(String text){

        try{
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.apps.translate");

            Uri uri = new Uri.Builder()
                    .scheme("http")
                    .authority("translate.google.com")
                    .path("/m/translate")
                    .appendQueryParameter("q", text)
                    .appendQueryParameter("tl", "bn") // target language
                    .appendQueryParameter("sl", "en") // source language
                    .build();
            intent.setData(uri);

            intent.setComponent(new ComponentName(
                    "com.google.android.apps.translate",
                    "com.google.android.apps.translate.TranslateActivity"));

            mActivity.startActivity(intent);
            return true;

        }catch (ActivityNotFoundException e){
            showNoGTDialog(mActivity);
            return false;
        }
    }

    private void showNoGTDialog(final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(R.string.STR_NO_GT_TITLE);
        alertDialogBuilder.setMessage(R.string.STR_NO_GOOGLE_TRANSLATION);
        alertDialogBuilder.setNegativeButton(R.string.STR_CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                activity.startActivity(new Intent(activity, TTSActivity.class));
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.STR_INSTALL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                installGoogleTranslator(activity);
            }
        });
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
