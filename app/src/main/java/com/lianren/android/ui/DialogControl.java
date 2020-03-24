package com.lianren.android.ui;

import android.app.ProgressDialog;

/**
 * @package: com.lianren.android.ui
 * @user:xhkj
 * @date:2019/12/16
 * @description:
 **/
public interface DialogControl {

    public abstract void hideWaitDialog();

    public abstract ProgressDialog showWaitDialog();

    public abstract ProgressDialog showWaitDialog(int resid);

    public abstract ProgressDialog showWaitDialog(String text);
}
