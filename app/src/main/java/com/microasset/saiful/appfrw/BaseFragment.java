package com.microasset.saiful.appfrw;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BaseFragment extends Fragment implements NotifyObserver {

    //
    protected NotifyObserver mNotifyObserver;
    public FragmentActivity mInstance;
    protected float appliedFontScale;


    public void setmNotifyObserver(NotifyObserver notifyObserver){
        mNotifyObserver = notifyObserver;
    }

    /**
     * Gets the class name
     *
     * @param cls
     * @return Class name
     */
    public static String getClassName(Class<?> cls) {
        String fullName = cls.getName();
        int pos = fullName.lastIndexOf('.') + 1;
        if (pos > 0) {
            fullName = fullName.substring(pos);
        }
        return fullName;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createInstance(savedInstanceState);
    }

    protected void createInstance(@Nullable Bundle savedInstanceState){

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mInstance = (FragmentActivity) context;
    }

    /**
     * Views should be created here, set the listeners, observer etc..
     */
    protected abstract int getLayout();

    /**
     * Views should be created here, set the listeners, observer etc..
     */
    protected abstract void activityCreated(@Nullable Bundle savedInstanceState);

    /**
     * Actions should be created here
     */
    protected abstract void createAdapter();

    /**
     * Concrete class should know how to load the data by adapters
     */
    protected abstract void loadData();

    /**
     * Get the data from response, no need to create runOnUiThread its all ready maintained
     */
    public abstract void doUpdateRequest(ResponseObject response);


    @Override
    public synchronized void update(ResponseObject response) {
        final ResponseObject tmpResponse = response;

        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                doUpdateRequest(tmpResponse);
            }
        });
    }

    /**
     * Gets view from resource
     *
     * @param rViewId
     * @return
     */
    protected View getView(int rViewId) {
        return mInstance.findViewById(rViewId);
    }

    /**
     * Hide the requested view.
     *
     * @param rViewId
     */
    protected void hideView(int rViewId) {
        View view = mInstance.findViewById(rViewId);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * Show the requested view.
     *
     * @param rViewId
     */
    protected void showView(int rViewId) {
        View view = mInstance.findViewById(rViewId);
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Sets text from resource into view
     *
     * @param rViewId
     * @param rTextId
     */
    protected void setText(int rViewId, int rTextId) {
        View view = getView(rViewId);
        if (view instanceof TextView) {
            ((TextView) view).setText(rTextId);
        }
    }

    /**
     * Sets text from resource into view but from different thread/Cross Thread
     *
     * @param rIdText Resource id of text
     */
    protected void setTextCT(final int rViewId, final int rIdText) {
        mInstance.runOnUiThread(new Runnable() {
            public void run() {
                setText(rViewId, rIdText);
            }
        });
    }

    /**
     * Sets text into view
     *
     * @param rViewId
     * @param text
     */
    protected void setText(int rViewId, String text) {
        View view = getView(rViewId);
        if (view instanceof TextView) {
            ((TextView) view).setText(text);
        }
    }

    /**
     * Get EditText text value.
     *
     * @param rViewId
     * @return
     */
    protected String getTextEditText(int rViewId) {
        View view = getView(rViewId);

        if (view instanceof EditText) {
            return ((EditText) mInstance.findViewById(rViewId)).getText().toString().trim();
        }

        return "";
    }

    /**
     * @param rViewId
     * @param text
     */
    protected void setButtonText(int rViewId, String text) {
        View view = getView(rViewId);
        if (view instanceof Button) {
            ((Button) view).setText(text);
        }
    }

    /**
     * Sets background from resource
     *
     * @param rViewId
     * @param rBackgroundId
     */
    protected void setBackGround(int rViewId, int rBackgroundId) {
        View view = getView(rViewId);
        if (view != null) {
            view.setBackgroundResource(rBackgroundId);
        }
    }

    /**
     * @param bmp
     * @param rViewId
     */
    public void setImage(Bitmap bmp, int rViewId) {
        View view = getView(rViewId);
        if (view != null && view instanceof ImageView) {
            ((ImageView) view).setImageBitmap(bmp);
        }
    }

    @SuppressWarnings("rawtypes")
    public void startActivity(Class name) {

        Intent intent = new Intent(mInstance, name);
        startActivity(intent);
    }

    /**
     * Handle the back button click
     */
    public void onClickBack(View view) {
        mInstance.finish();
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(SettingsManager.getInstance().setFontScale(base));
//        appliedFontScale = SettingsManager.getInstance().getFontScale(mInstance);
//    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);

        createAdapter();
        createView(view);

        return view;
    }

    protected abstract void createView(View view);

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Take the Permission when loading...
        PermissionManager.getInstance().requestPermission(mInstance);
        // DONOT change this order may creates the problems
        activityCreated(savedInstanceState);
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //
    public void switchFragment(Fragment fragment, boolean addToBackStack) {
        //
    }
}
