package com.venmo.android.pin;

import android.view.View;
import android.widget.Toast;

import com.venmo.android.pin.view.PinputView;
import com.venmo.android.pin.view.PinputView.OnCommitListener;

class ConfirmPinViewController<T extends PinFragmentImplement> extends BaseViewController<T> {
    private String mTruthString;

    ConfirmPinViewController(T f, View v, String truth) {
        super(f, v);
        mTruthString = truth;
    }

    @Override
    void initUI() {
        String confirm = String.format(mContext.getString(R.string.pinlibrary_confirm_passcode));
        mHeaderText.setText(confirm);
    }

    @Override
    OnCommitListener provideListener() {
        return new OnCommitListener() {
            @Override
            public void onPinCommit(PinputView view, String submission) {
                if (submission.equals(mTruthString)) {
                    handleSave(submission);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.pinlibrary_pin_mismatch),
                            Toast.LENGTH_SHORT).show();
                    resetToCreate();
                    view.showErrorAndClear();
                }
            }
        };
    }

    private void handleSave(final String submission) {
        PinSaver saver = getConfig().getPinSaver();
        if (saver instanceof AsyncSaver) {
            getOutAndInAnim(mPinputView, mProgressBar).start();
            mHeaderText.setText(R.string.pinlibrary_saving_pin);
            runAsync(new Runnable() {
                @Override
                public void run() {
                    try {
                        getConfig().getPinSaver().save(submission);
                        postToMain(new Runnable() {
                            @Override
                            public void run() {
                                onSaveComplete();
                            }
                        });
                    } catch (Exception e) {
                        generalErrorAsync(mPinFragment.getString(R.string.pinlibrary_async_save_error));
                    }
                }
            });
        } else {
            saver.save(submission);
            onSaveComplete();
        }
    }

    private void onSaveComplete() {
        mPinputView.getText().clear();
        mPinFragment.notifyCreated();
    }

    private void resetToCreate() {
        mPinFragment.setDisplayType(PinDisplayType.CREATE);
        mPinFragment.setViewController(new CreatePinViewController(mPinFragment, mRootView));
    }

}
