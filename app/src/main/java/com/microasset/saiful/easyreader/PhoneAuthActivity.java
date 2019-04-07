package com.microasset.saiful.easyreader;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.microasset.saiful.appfrw.BaseActivity;
import com.microasset.saiful.appfrw.NotifyObserver;
import com.microasset.saiful.appfrw.ResponseObject;
import com.microasset.saiful.licence.PaymentManager;
import com.microasset.saiful.licence.RegistrationManager;
import com.microasset.saiful.model.CountryCodeModel;
import com.microasset.saiful.util.SharedPrefUtil;
import com.microasset.saiful.util.Utill;
import com.microasset.saiful.web.Firebase;
import com.microasset.saiful.web.FirebaseDbMeta;

import java.util.concurrent.TimeUnit;

import static com.microasset.saiful.licence.RegistrationManager.SUCCESS_USER_PAYMENT;
import static com.microasset.saiful.licence.RegistrationManager.SUCCESS_USER_REGISTRATION;
import static com.microasset.saiful.licence.RegistrationManager.USER_EXISTS;
import static com.microasset.saiful.licence.RegistrationManager.USER_USED_MAX_DEVICE_USING_CURRENT_PLAN;

public class PhoneAuthActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    private ProgressDialog mDialog;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;

    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;

    private Spinner mSpinner;
    LinearLayout ln_registration, ln_verify;
    TextView tv_country_code;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        mPhoneNumberField = findViewById(R.id.fieldPhoneNumber);
        mVerificationField = findViewById(R.id.fieldVerificationCode);

        mStartButton = findViewById(R.id.buttonStartVerification);
        mVerifyButton = findViewById(R.id.buttonVerifyPhone);
        mResendButton = findViewById(R.id.buttonResend);
        mSpinner = findViewById(R.id.country_name);
        tv_country_code = findViewById(R.id.tv_mob_number);

        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);

        ln_registration = (LinearLayout)findViewById(R.id.ln_registration);
        ln_verify = (LinearLayout)findViewById(R.id.ln_verify);
        changeLanguage();

        fillCountryName();

        mAuth = FirebaseAuth.getInstance();

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]

        updateUI(STATE_INITIALIZED);
    }


    private void changeLanguage(){
        RadioGroup radioGroup = (RadioGroup)findViewById(R.id.rg_toggle);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.rb_bn){
                    SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE, "bn");
                    Utill.setLocale(SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE, "bn"), mInstance);
                    updateTextOnUI();

                }else if(checkedId == R.id.rb_en){
                    SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE, "en");
                    Utill.setLocale(SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE, "bn"), mInstance);
                    updateTextOnUI();
                }
            }
        });

        String version = SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_LANGUAGE_CODE, "bn");
        if(version.equals("en")){
            radioGroup.check(R.id.rb_en);
        }
    }

    private void updateTextOnUI(){
        ((TextView)findViewById(R.id.tv_instruction)).setText(R.string.STR_MOBILE_NUMBER_DES);
        mPhoneNumberField.setHint(R.string.STR_ENTER_MOBILE_NUMBER);
        mVerificationField.setHint(R.string.STR_WRITE_CODE);
        ((TextView)findViewById(R.id.tv_verify_title)).setText(R.string.STR_VERIFY_INSTRUCTION);
        mStartButton.setText(R.string.STR_REGISTRATION);
        mVerifyButton.setText(R.string.STR_VERIFY_CODE);
        mResendButton.setText(R.string.STR_RESEND_CODE);
    }

    public void fillCountryName(){
        String[] list = CountryCodeModel.getInstance(mInstance).getListitems();
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(PhoneAuthActivity.this, R.layout.spinner_row, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(CountryCodeModel.getInstance(mInstance).getPositionByName("Bangladesh"));
        String code = CountryCodeModel.getInstance(mInstance).getCodeByCountryName("Bangladesh");
        tv_country_code.setText(code);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
               //String name = (String) CountryCodeModel.getInstance(mInstance).getItem(position);
                String name = mSpinner.getSelectedItem().toString();
                String code = CountryCodeModel.getInstance(mInstance).getCodeByCountryName(name);
                tv_country_code.setText(code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {

        try {
            // [START start_phone_auth]
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks);        // OnVerificationStateChangedCallbacks
            // [END start_phone_auth]

            mVerificationInProgress = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        try {
            // [START verify_with_code]
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            // [END verify_with_code]
            signInWithPhoneAuthCredential(credential);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        try {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,        // Phone number to verify
                    60,                 // Timeout duration
                    TimeUnit.SECONDS,   // Unit of timeout
                    this,               // Activity (for callback binding)
                    mCallbacks,         // OnVerificationStateChangedCallbacks
                    token);             // ForceResendingToken from callbacks
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // [END resend_verification]

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        try{
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = task.getResult().getUser();
                                // [START_EXCLUDE]
                                updateUI(STATE_SIGNIN_SUCCESS, user);
                                signOut();
                                user.delete();
                                //
                                startRegistration();
                                //finish();
                                // [END_EXCLUDE]
                            } else {
                                // Sign in failed, display a message and update the UI
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    // The verification code entered was invalid
                                    // [START_EXCLUDE silent]
                                    mVerificationField.setError("Invalid code.");
                                    // [END_EXCLUDE]
                                }
                                // [START_EXCLUDE silent]
                                // Update UI
                                updateUI(STATE_SIGNIN_FAILED);
                                // [END_EXCLUDE]
                            }
                        }
                    });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                ln_registration.setVisibility(View.VISIBLE);
                ln_verify.setVisibility(View.GONE);
                break;
            case STATE_CODE_SENT:
                ln_registration.setVisibility(View.GONE);
                ln_verify.setVisibility(View.VISIBLE);
                break;
            case STATE_VERIFY_FAILED:
            case STATE_SIGNIN_FAILED:
                ln_registration.setVisibility(View.VISIBLE);
                ln_verify.setVisibility(View.GONE);
                break;
            case STATE_VERIFY_SUCCESS:
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }
    }

    private void signOut() {
        mAuth.signOut();
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = tv_country_code.getText().toString() + mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError(getString(R.string.STR_ENTER_MOBILE_NUMBER));
            return false;
        }

        return true;
    }

    @Override
    public void onClick(final View view) {
        Utill.isOnlineAsync(new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(response.getResponseCode() == -1){
                    Toast.makeText(mInstance, getString(R.string.STR_NO_INTERNET), Toast.LENGTH_LONG).show();
                    return;
                }
                doOnClick(view);
            }
        });
    }

    public void doOnClick(View view){
        final String phoneNumber = tv_country_code.getText().toString() + mPhoneNumberField.getText().toString();
        switch (view.getId()) {

            case R.id.buttonStartVerification:
                if (!validatePhoneNumber()) {
                    return;
                }
                startPhoneNumberVerification(phoneNumber);
                break;
            case R.id.buttonVerifyPhone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.buttonResend:
                resendVerificationCode(phoneNumber, mResendToken);
                break;
            //case R.id.signOutButton:
            //   signOut();
            // break;
        }
    }

    public void startRegistration(){
        final String mobNumber = tv_country_code.getText().toString() + mPhoneNumberField.getText().toString();
        showProgressDialog();
        // Current plan from Dialog.
        //
        String curren_plan = "single_plan";//DEFAULT PLAN INITIALLY FOR ALL..
        // The plan will be added when SUBSCRIBE USER FOR PAYMENT...
        RegistrationManager.getInstance(mInstance).registration(mobNumber, curren_plan, new NotifyObserver() {
            @Override
            public void update(ResponseObject response) {
                if(		response.getResponseCode() == SUCCESS_USER_REGISTRATION ||
                        response.getResponseCode() == SUCCESS_USER_PAYMENT){
                    SharedPrefUtil.setSetting(mInstance, SharedPrefUtil.KEY_MOBILE_NUMBER, mobNumber);
                    String user =  SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_USER_ID);
                    String password =  SharedPrefUtil.getSetting(mInstance, SharedPrefUtil.KEY_USER_PSW);

                    Firebase firebase = new Firebase();
                    firebase.login(user, password, new NotifyObserver() {
                        @Override
                        public void update(ResponseObject response) {
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
                            hideDialog();
                            startActivity(CategoryActivity.class);
                            finish();
                        }
                    });

                }else if(USER_EXISTS == response.getResponseCode()){
                    hideDialog();
                    Toast.makeText(mInstance, getString(R.string.STR_USER_EXISTS), Toast.LENGTH_LONG).show();
                }else if(USER_USED_MAX_DEVICE_USING_CURRENT_PLAN == response.getResponseCode()){
                    hideDialog();
                    Toast.makeText(mInstance, getString(R.string.STR_USER_USED_MAX_DEVICE_USING_CURRENT_PLAN), Toast.LENGTH_LONG).show();
                }
                else{
                    hideDialog();
                    Toast.makeText(mInstance, getString(R.string.STR_TRY_AGAIN), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void showProgressDialog(){
        if(mDialog == null){
            mDialog = new ProgressDialog(this);
        }
        mDialog.setMessage(getString(R.string.STR_LOADING));
        mDialog.setCancelable(false);
        mDialog.show();
    }

    private void hideDialog(){
        if(mDialog != null && mDialog.isShowing()){
            mDialog.dismiss();
            mDialog = null;
        }
    }
}