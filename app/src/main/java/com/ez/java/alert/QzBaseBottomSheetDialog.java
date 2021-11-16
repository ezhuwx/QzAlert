package com.ez.java.alert;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * com.fantian.mep.base
 *
 * @author : ezhuwx
 * Describe :底部弹出组件
 * Designed on 2018/6/7
 * E-mail : ezhuwx@163.com
 * @ on 17:21 by ezhuwx
 * version 2.0.3
 */
public abstract class QzBaseBottomSheetDialog extends BottomSheetDialogFragment {
    private static final String TAG = BottomSheetDialogFragment.class.getSimpleName();
    protected BottomSheetBehavior<View> behavior;
    private Window window;
    private float dimAmount = 0.4f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomDialog);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        window = dialog.getWindow();
        View view = View.inflate(getContext(), getLayoutRes(), null);
        dialog.setContentView(view);
        dialog.setOnShowListener(dialog1 -> QzBaseBottomSheetDialog.this.onShow());
        dialog.setOnDismissListener(dialog1 -> QzBaseBottomSheetDialog.this.onDismiss());
        behavior = BottomSheetBehavior.from((View) view.getParent());
        behavior.setSkipCollapsed(false);
        bindView(view);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        //默认全屏展开
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = dimAmount;
        window.setAttributes(params);
    }

    @LayoutRes
    public abstract int getLayoutRes();
    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onCancel();
    }
    public abstract void bindView(View v);

    public void onShow() {
    }

    public void onCancel() {
    }

    public void onDismiss() {
    }

    public String getFragmentTag() {
        return TAG;
    }

    public BottomSheetBehavior<View> getBehavior() {
        return behavior;
    }

    public float getDimAmount() {
        return dimAmount;
    }

    public void setDimAmount(float dimAmount) {
        this.dimAmount = dimAmount;
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, getFragmentTag());
    }

    public void show(FragmentManager fragmentManager, Bundle argument) {
        setArguments(argument);
        show(fragmentManager, getFragmentTag());
    }


}
