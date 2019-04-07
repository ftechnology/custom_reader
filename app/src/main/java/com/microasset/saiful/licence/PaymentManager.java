package com.microasset.saiful.licence;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import com.microasset.saiful.appfrw.LogUtil;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.easyreader.PaymentActivity;
import com.microasset.saiful.easyreader.R;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.web.FirebaseDbMeta;

import static com.microasset.saiful.licence.RegistrationManager.SUCCESS_USER_PAYMENT;
import static com.microasset.saiful.licence.RegistrationManager.USER_PAYMENT_NEEDED;

/**
 * Created by farukhossain on 2019/02/25.
 */

public class PaymentManager {

    static public PaymentManager paymentManager;

    protected Activity mActivity;
    static ProgressDialog mDialog;
    FirebaseDbMeta.Meta mMeta = null;

    //
    private PaymentManager(Activity activity) {
        mActivity = activity;
    }

    public static PaymentManager getInstance(Activity activity) {
        if (paymentManager == null) {
            paymentManager = new PaymentManager(activity);
        }
        return paymentManager;
    }

    public void doPayment(){
        showProgressDialog(mActivity);
        RegistrationManager.getInstance(mActivity).hasPaymentAlready(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == USER_PAYMENT_NEEDED){
                    final FirebaseDbMeta firebaseDbMeta = FirebaseDbMeta.getInstance();
                    firebaseDbMeta.getMeta(new NotifyObserver() {
                        @Override
                        public void update(ResponseObject response) {
                            if(response.getResponseMsg().equals(ResponseObject.mSuccess)){
                                mMeta =  (FirebaseDbMeta.Meta)response.getDataObject();
                                if(mMeta.payment_no.length() > 1){
                                    SharedPrefUtil.setSetting(mActivity, SharedPrefUtil.KEY_CALL_CENTER_NUMBER, mMeta.phone);
                                    SharedPrefUtil.setSetting(mActivity, SharedPrefUtil.KEY_CALL_CENTER_EMAIL, mMeta.email);
                                    SharedPrefUtil.setSetting(mActivity, SharedPrefUtil.KEY_PAYMENT_NUMBER, mMeta.payment_no);
                                    mActivity.startActivity(new Intent(mActivity, PaymentActivity.class));
                                }
                                if(hasPaymentGateway()){
                                    // TODO ..FIXME TOkEN need to get from the TRANSACTION...
                                    RegistrationManager.getInstance(mActivity).payment("TID-", new NotifyObserver() {
                                        @Override
                                        public void update(ResponseObject response) {
                                            //onSuccessUserPayment(response);
                                        }
                                    });
                                }
                                hideDialog();
                            }
                        }
                    });
                } else{
                    onSuccessUserPayment(response);
                }
            }
        });
    }

    public boolean hasPaymentGateway(){
        return false;
    }

    private void onSuccessUserPayment(ResponseObject response){
        hideDialog();
        if(response.getResponseCode() == SUCCESS_USER_PAYMENT){
            RegistrationManager.getInstance(mActivity).setAllow(true);
            Toast.makeText(mActivity, mActivity.getString(R.string.STR_SUBCRIBE_SUCCESS), Toast.LENGTH_LONG).show();
        }
    }

    public FirebaseDbMeta.Meta getFireBaseMeta(){
        return mMeta;
    }

    public static void showProgressDialog(Activity activity){

        try {
            if(mDialog != null && mDialog.isShowing()){
                mDialog.dismiss();;
                mDialog = null;
            }
            if (mDialog == null) {
                mDialog = new ProgressDialog(activity);
            }
            mDialog.setMessage(activity.getString(R.string.STR_LOADING));
            mDialog.setCancelable(false);
            mDialog.show();
        }catch (Exception e){
            LogUtil.d(e.getMessage());
        }
    }

    public static void hideDialog(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }

        mDialog = null;
    }

}
