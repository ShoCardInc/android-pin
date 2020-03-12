package com.venmo.android.pin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class PinFragment extends DialogFragment implements PinFragmentImplement {

    public static final String TAG = PinFragment.class.getCanonicalName();
    private static final String KEY_FRAGMENT_VIEW_TYPE = "com.venmo.input_fragment_view_type";

    private PinListener mListener;
    private PinDisplayType mPinDisplayType;
    private BaseViewController mViewController;
    private PinFragmentConfiguration mConfig;
    private View mRootView;

    public static PinFragment newInstanceForVerification(PinListener listener) {
        return newInstanceForVerification(listener, null);
    }

    public static PinFragment newInstanceForVerification(PinListener listener, PinFragmentConfiguration config) {
        return newInstance(listener, PinDisplayType.VERIFY, config);
    }

    public static PinFragment newInstanceForCreation(PinListener listener) {
        return newInstanceForCreation(listener, null);
    }

    public static PinFragment newInstanceForCreation(PinListener listener, PinFragmentConfiguration config) {
        return newInstance(listener, PinDisplayType.CREATE, config);
    }

    private static PinFragment newInstance(PinListener listener, PinDisplayType type, PinFragmentConfiguration config) {
        PinFragment instance = new PinFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_FRAGMENT_VIEW_TYPE, type);
        instance.setArguments(bundle);
        instance.setConfig(config);
        instance.mListener = listener;
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(androidx.fragment.app.DialogFragment.STYLE_NORMAL, R.style.PinDialogWithTransparentBackground);
        Bundle args = getArguments();
        mPinDisplayType = (PinDisplayType) args.getSerializable(KEY_FRAGMENT_VIEW_TYPE);
    }

    public void start(FragmentActivity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        show(activity.getSupportFragmentManager(), TAG);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.layout_pin_view, container, false);
        if (mConfig == null) {
            setConfig(new PinFragmentConfiguration(getActivity()));
        }
        setDisplayType(mPinDisplayType);
        initViewController();
        return mRootView;
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public void setConfig(PinFragmentConfiguration config) {
        mConfig = config;
    }

    @Override
    public PinFragmentConfiguration getConfig() {
        return mConfig;
    }

    public void onPinCreationEntered(String pinEntry) {
        mPinDisplayType = PinDisplayType.CONFIRM;
        mViewController = new ConfirmPinViewController(this, mRootView, pinEntry);
    }

    public void setViewController(BaseViewController controller) {
        mViewController = controller;
    }

    public void notifyCancelled() {
        if (mListener != null) {
            mListener.onCancelled();
            mListener = null;
        }
    }

    public void notifyValid() {
        if (mListener != null) {
            mListener.onValidated();
            mListener = null;
        }
    }

    public void notifyCreated() {
        if (mListener != null) {
            mListener.onPinCreated();
            mListener = null;
        }
    }

    public void setDisplayType(PinDisplayType type) {
        mPinDisplayType = type;
    }

    private void initViewController() {
        switch (mPinDisplayType) {
            case VERIFY:
                setViewController(new VerifyPinViewController(this, mRootView));
                break;
            case CREATE:
                setViewController(new CreatePinViewController(this, mRootView));
                break;
            case CONFIRM:
                mViewController.refresh(mRootView);
                break;
            default:
                throw new IllegalStateException("Invalid DisplayType " + mPinDisplayType.toString());
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        notifyCancelled();
        super.onDismiss(dialog);
    }
}
