package com.venmo.android.pin;

import android.animation.Animator;
import android.view.View;

import com.venmo.android.pin.view.PinputView;
import com.venmo.android.pin.view.PinputView.OnCommitListener;

class CreatePinViewController<T extends PinFragmentImplement> extends BaseViewController<T> {
    CreatePinViewController(T f, View v) {
        super(f, v);
    }

    @Override
    void initUI() {
        String create = String.format(mContext.getString(R.string.create_passcode));
        mHeaderText.setText(create);
    }

    @Override
    OnCommitListener provideListener() {
        return new OnCommitListener() {
            @Override
            public void onPinCommit(PinputView view, String submission) {
                Animator a = getOutAndInAnim(mPinputView, mPinputView);
                mPinFragment.onPinCreationEntered(submission);
                a.start();
            }
        };
    }
}
