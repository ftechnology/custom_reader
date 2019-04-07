package com.microasset.saiful.easyreader;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.licence.PaymentManager;
import com.microasset.saiful.licence.RegistrationManager;
import com.microasset.saiful.util.Convert;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;
import com.microasset.saiful.web.FirebaseDbMeta;

import static com.microasset.saiful.licence.RegistrationManager.SUCCESS_USER_PAYMENT;

public class PaymentActivity extends BaseActivity {
    private Toolbar mToolBar = null;
    TextView tv_current_plan, tv_price, tv_expire_date,tv_payment_no, tv_call_center_number;
    RadioGroup rb_group;
    RadioButton rb_button_single, rb_button_family, rb_button_combo;
    String[] planArray = {"single_plan", "family_plan", "combo_plan"};
    String CURRENT_PLAN = planArray[0];


    @Override
    protected void createView(Bundle savedInstanceState) {
        this.setContentView(R.layout.payment_layout);
        setupToolbar();

        tv_current_plan = (TextView) findViewById(R.id.tv_current_plan);
        tv_price = (TextView) findViewById(R.id.tv_plan_price);
        tv_expire_date = (TextView) findViewById(R.id.tv_expire_date);
        rb_group = (RadioGroup) findViewById(R.id.rb_group);
        rb_button_single = (RadioButton) findViewById(R.id.rb_signle);
        rb_button_family = (RadioButton) findViewById(R.id.rb_family);
        rb_button_combo = (RadioButton) findViewById(R.id.rb_combo);
        tv_payment_no = (TextView) findViewById(R.id.tv_payment_no);
        tv_call_center_number = (TextView) findViewById(R.id.tv_call_center_number);

        if(!Utill.isNetworkAvailable(mInstance)){
            Toast.makeText(mInstance, getString(R.string.STR_NO_INTERNET), Toast.LENGTH_LONG).show();
           finish();
        }
        if(PaymentManager.getInstance(mInstance).hasPaymentGateway()){
            View manual_payment =  findViewById(R.id.manual_payment);
            manual_payment.setVisibility(View.GONE);
        }
        showExpireDate();

        FirebaseDbMeta.Meta meta =  PaymentManager.getInstance(mInstance).getFireBaseMeta();

        if(meta == null){
            Toast.makeText(mInstance, getString(R.string.STR_TRY_AGAIN),Toast.LENGTH_LONG).show();
            finish();
        }

        final String currentPlan = getResources().getString(R.string.STR_CURRENT_PLAN, SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_CURRENT_PLAN, "single_plan"));
        planArray = FirebaseDbMeta.getInstance().getplanTypes();

        final int price = Convert.toInt(meta.price);
        final int discount =  Convert.toInt(meta.discount);
        int dc = FirebaseDbMeta.getInstance().maxDeviceSupportedAsCurrentPlan(CURRENT_PLAN);
        updatePriceUI(price, discount, dc);
        tv_payment_no.setText(getResources().getString(R.string.STR_PAYMENT_NUMBER, meta.payment_no));
        tv_call_center_number.setText(getResources().getString(R.string.STR_BKASH_CALL_CENTER_NUMBER, meta.phone));

        tv_current_plan.setText(currentPlan);
        rb_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_signle) {
                    CURRENT_PLAN = planArray[0];
                    int dc = FirebaseDbMeta.getInstance().maxDeviceSupportedAsCurrentPlan(CURRENT_PLAN);
                    updatePriceUI(price, discount, dc);
                } else if (checkedId == R.id.rb_family) {
                    CURRENT_PLAN = planArray[1];
                    int dc = FirebaseDbMeta.getInstance().maxDeviceSupportedAsCurrentPlan(CURRENT_PLAN);
                    updatePriceUI(price, discount, dc);
                } else if (checkedId == R.id.rb_combo) {
                    CURRENT_PLAN = planArray[2];
                    int dc = FirebaseDbMeta.getInstance().maxDeviceSupportedAsCurrentPlan(CURRENT_PLAN);
                    updatePriceUI(price, discount, dc);
                }
                //updatePriceUI(price, discount, 1);
            }
        });

        Button tv_number = findViewById(R.id.btn_payment);
        tv_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               updateUserPlan();
            }
        });
    }

    public void updateUserPlan(){
        if(PaymentManager.getInstance(mInstance).hasPaymentGateway()){
            // TODO ..FIXME TOkEN need to get from the TRANSACTION...
            doUpdatePlan();
        } else{
            //MANUAL PROCESS FOR PAYMENT..
            RegistrationManager.getInstance(mInstance).hasPaymentAlready(new NotifyObserver() {
                @Override
                public void update(ResponseObject response) {
                    if(response.getResponseCode() == SUCCESS_USER_PAYMENT){
                        doUpdatePlanManual();
                    }else{
                        Toast.makeText(mInstance, getString(R.string.STR_PAYMENT_FIRST), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void doUpdatePlan(){
        RegistrationManager.getInstance(mInstance).payment("TID-007", new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                RegistrationManager.getInstance(mInstance).updateUserPlan(CURRENT_PLAN, new NotifyObserver() {
                    @Override
                    public void update(ResponseObject response) {
                        SharedPrefUtil.setSetting(mInstance,SharedPrefUtil.KEY_CURRENT_PLAN, CURRENT_PLAN);
                        Toast.makeText(mInstance,  getResources().getString(R.string.STR_PLAN_ACTIVATED, CURRENT_PLAN), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    public void doUpdatePlanManual(){
        RegistrationManager.getInstance(mInstance).updateUserPlan(CURRENT_PLAN, new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                SharedPrefUtil.setSetting(mInstance,SharedPrefUtil.KEY_CURRENT_PLAN, CURRENT_PLAN);
                Toast.makeText(mInstance,  getResources().getString(R.string.STR_PLAN_ACTIVATED, CURRENT_PLAN), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupToolbar() {
        mToolBar = (Toolbar) findViewById(R.id.toolBar);
        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        ((TextView) findViewById(R.id.tv_top)).setText(R.string.STR_PAYMENT_INSTRUCTION);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void showExpireDate(){
        boolean isAllow = RegistrationManager.getInstance(mInstance).isAllow(true);
        if(isAllow){
            tv_expire_date.setText(getResources().getString(R.string.STR_PLAN_EXPIRE_DATE, RegistrationManager.getInstance(mInstance).getSubscriptionValidDate()));
        } else {
            tv_expire_date.setText(getResources().getString(R.string.STR_PLAN_EXPIRE_DATE, "Please select your plan"));
        }
    }

    private void updatePriceUI(int price,int discout, int deviceCount){
        int actualPrice = price;
        if(deviceCount > 1){
            actualPrice = (price - ((discout * price ) / 100)) * deviceCount;
        }
        tv_price.setText(getResources().getString(R.string.STR_PLAN_PRICE,""+actualPrice));
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
