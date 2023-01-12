package com.ez.java.alert;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.ez.java.alert.databinding.FragmentBaseAlertBinding;

import java.util.Objects;

import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @author : ezhuwx
 * Describe :通用提示框组件
 * Designed on 2018/8/15
 * E-mail : ezhuwx@163.com
 * Update on 10:04 by ezhuwx
 * version 1.0.0
 */

public class QzAlertFragment extends DialogFragment {
    private FragmentActivity context;
    private int titleId = -1;
    private String title = "";
    private int messageId = -1;
    private String message = "";
    private SpannableString spannableMessage;
    private int leftMessageId = -1;
    private String leftMessage = "";
    private int leftColor = -1;
    private OnLeftClickListener leftClickListener;
    private int rightMessageId = -1;
    private String rightMessage = "";
    private int rightColor = -1;
    private OnRightClickListener rightClickListener;
    private int middleMessageId = -1;
    private String middleMessage = "";
    private int middleColor = -1;
    private OnMiddleClickListener middleClickListener;
    private int editHintId = -1;
    private String editHint = "";
    private int editMaxLength = -1;
    private int editContentId = -1;
    private String editContent = "";
    private int gravity = Gravity.CENTER;
    private View contentView;
    private boolean isAnimation = true;
    private boolean isShowEdit = false;
    private boolean isShowTitle = false;
    private boolean isShowContent = false;
    private boolean cancelable = false;
    private boolean isCustomView = false;
    private boolean isKeyBoardShown = false;
    private boolean isNatureButtonShow = false;
    private boolean isNegativeButtonShow = false;
    private boolean isPositiveButtonShow = false;
    private int layoutId = R.layout.fragment_base_alert;
    private OnAlertShowListener showListener;
    private int height = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int width = 230;
    private boolean isFirstStart = false;
    private FragmentBaseAlertBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        isFirstStart = true;
        if (!isCustomView) {
            mBinding = FragmentBaseAlertBinding.inflate(getLayoutInflater());
            contentView = mBinding.getRoot();
            initView();
        } else if (showListener != null) {
            contentView = inflater.inflate(getLayoutId(), container);
            showListener.onShow(contentView, this);
        }
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isFirstStart) {
            isFirstStart = false;
            initDialog();
        }
    }

    /**
     * 初始化Dialog基本设置
     */
    private void initDialog() {
        Objects.requireNonNull(getDialog()).setCancelable(false);
        getDialog().setCanceledOnTouchOutside(cancelable);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            if (isAnimation) {
                window.setWindowAnimations(R.style.animate_dialog);
            }
            int realWidth = width > 0 ? AutoSizeUtils.dp2px(requireContext(), width) : width;
            int realHeight = height > 0 ? AutoSizeUtils.dp2px(requireContext(), height) : height;
            window.setLayout(realWidth, realHeight);
        }
        if (isShowEdit) {
            isKeyBoardShown = true;
        }
    }

    /**
     * 初始化MepAlert基本配置
     */
    private void initView() {
        if (getLayoutId() == R.layout.fragment_base_alert) {
            //默认参数
            title = title == null ? getString(R.string.warm_tip) : title;
            leftMessage = leftMessage == null ? getString(R.string.chance) : leftMessage;
            rightMessage = rightMessage == null ? getString(R.string.confirm_name) : rightMessage;
            middleMessage = middleMessage == null ? getString(R.string.known) : middleMessage;
            /*标题*/
            if (isShowTitle) {
                mBinding.alertTitleTv.setText(titleId == -1 ? title : context.getString(titleId));
                mBinding.alertTitleTv.setVisibility(View.VISIBLE);
            }
            /*显示文字提示*/
            if (isShowContent) {
                if (spannableMessage != null) {
                    mBinding.alertContentTv.setText(spannableMessage);
                } else {
                    mBinding.alertContentTv.setText(messageId == -1 ? message : context.getString(messageId));
                }
                mBinding.alertContentTv.setVisibility(View.VISIBLE);
            }
            //Gravity
            mBinding.alertContentTv.setGravity(gravity);
            /*显示编辑框*/
            if (isShowEdit) {
                if (isShowContent) {
                    RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) mBinding.alertContentEt.getLayoutParams();
                    rlp.setMargins(
                            rlp.leftMargin,
                            AutoSizeUtils.dp2px(requireContext(), 10),
                            rlp.rightMargin,
                            rlp.bottomMargin);
                    mBinding.alertContentEt.setLayoutParams(rlp);
                }
                mBinding.alertContentEt.setHint(editHintId == -1 ? editHint : context.getString(editHintId));
                if (editContent.trim().length() > 0) {
                    mBinding.alertContentEt.setText(editContentId == -1 ? editContent : context.getString(editContentId));
                    mBinding.alertContentEt.setSelection(editContent.length());
                }
                if (editMaxLength > 0) {
                    mBinding.alertContentEt.setFilters(new InputFilter.LengthFilter[]
                            {new InputFilter.LengthFilter(editMaxLength)});
                }
                mBinding.alertContentEt.setVisibility(View.VISIBLE);
            } else {
                mBinding.alertContentEt.setVisibility(View.GONE);
            }
            onViewClicked();
            setButtonShow();
        }
    }

    /**
     * 点击外部取消
     */
    public QzAlertFragment setCanceledOnTouchOutside(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    /**
     * 按键显示逻辑
     */
    private void setButtonShow() {
        if (isNegativeButtonShow && !isNatureButtonShow && !isPositiveButtonShow) {
            /*left*/
            mBinding.alertLeftButtonTv.setTextColor(leftColor != -1
                    ? getResources().getColor(leftColor) : getResources().getColor(R.color.positive_color));
            mBinding.alertLeftButtonTv.setText(leftMessageId == -1 ? leftMessage : context.getString(leftMessageId));
            mBinding.alertMiddleButtonTv.setVisibility(View.GONE);
            mBinding.alertRightButtonTv.setVisibility(View.GONE);
            mBinding.lineLeftV.setVisibility(View.GONE);
            mBinding.lineRightV.setVisibility(View.GONE);
        } else if (!isNegativeButtonShow && isNatureButtonShow && !isPositiveButtonShow) {
            /*middle*/
            mBinding.alertMiddleButtonTv.setTextColor(middleColor != -1
                    ? getResources().getColor(middleColor) : getResources().getColor(R.color.positive_color));
            mBinding.alertMiddleButtonTv.setText(middleMessageId == -1 ? middleMessage : context.getString(middleMessageId));
            mBinding.alertLeftButtonTv.setVisibility(View.GONE);
            mBinding.alertRightButtonTv.setVisibility(View.GONE);
            mBinding.lineLeftV.setVisibility(View.GONE);
            mBinding.lineRightV.setVisibility(View.GONE);
        } else if (!isNegativeButtonShow && !isNatureButtonShow && isPositiveButtonShow) {
            /*right*/
            mBinding.alertRightButtonTv.setTextColor(rightColor != -1
                    ? getResources().getColor(rightColor) : getResources().getColor(R.color.positive_color));
            mBinding.alertRightButtonTv.setText(rightMessageId == -1 ? rightMessage : context.getString(rightMessageId));
            mBinding.alertMiddleButtonTv.setVisibility(View.GONE);
            mBinding.alertLeftButtonTv.setVisibility(View.GONE);
            mBinding.lineLeftV.setVisibility(View.GONE);
            mBinding.lineRightV.setVisibility(View.GONE);
        } else if (isNegativeButtonShow && isNatureButtonShow && !isPositiveButtonShow) {
            /*left & middle*/
            mBinding.alertLeftButtonTv.setTextColor(leftColor != -1
                    ? getResources().getColor(leftColor) : getResources().getColor(R.color.negative_color));
            mBinding.alertLeftButtonTv.setText(leftMessageId == -1 ? leftMessage : context.getString(leftMessageId));
            mBinding.alertMiddleButtonTv.setTextColor(middleColor != -1
                    ? getResources().getColor(middleColor) : getResources().getColor(R.color.positive_color));
            mBinding.alertMiddleButtonTv.setText(middleMessageId == -1 ? middleMessage : context.getString(middleMessageId));
            mBinding.alertRightButtonTv.setVisibility(View.GONE);
            mBinding.lineRightV.setVisibility(View.GONE);
        } else if (isNegativeButtonShow && !isNatureButtonShow) {
            /*left & right*/
            mBinding.alertLeftButtonTv.setTextColor(leftColor != -1
                    ? getResources().getColor(leftColor) : getResources().getColor(R.color.negative_color));
            mBinding.alertLeftButtonTv.setText(leftMessageId == -1 ? leftMessage : context.getString(leftMessageId));
            mBinding.alertRightButtonTv.setTextColor(rightColor != -1
                    ? getResources().getColor(rightColor) : getResources().getColor(R.color.positive_color));
            mBinding.alertRightButtonTv.setText(rightMessageId == -1 ? rightMessage : context.getString(rightMessageId));
            mBinding.alertMiddleButtonTv.setVisibility(View.GONE);
            mBinding.lineRightV.setVisibility(View.GONE);
        } else if (!isNegativeButtonShow && isNatureButtonShow) {
            /*middle right*/
            mBinding.alertMiddleButtonTv.setTextColor(middleColor != -1
                    ? getResources().getColor(middleColor) : getResources().getColor(R.color.negative_color));
            mBinding.alertMiddleButtonTv.setText(middleMessageId == -1 ? middleMessage : context.getString(middleMessageId));
            mBinding.alertRightButtonTv.setTextColor(rightColor != -1
                    ? getResources().getColor(rightColor) : getResources().getColor(R.color.positive_color));
            mBinding.alertRightButtonTv.setText(rightMessageId == -1 ? rightMessage : context.getString(rightMessageId));
            mBinding.alertLeftButtonTv.setVisibility(View.GONE);
            mBinding.lineRightV.setVisibility(View.GONE);
        } else if (isNegativeButtonShow) {
            /*left middle right*/
            mBinding.alertLeftButtonTv.setTextColor(leftColor != -1
                    ? getResources().getColor(leftColor) : getResources().getColor(R.color.negative_color));
            mBinding.alertLeftButtonTv.setText(leftMessageId == -1 ? leftMessage : context.getString(leftMessageId));
            mBinding.alertMiddleButtonTv.setTextColor(middleColor != -1
                    ? getResources().getColor(middleColor) : getResources().getColor(R.color.positive_color));
            mBinding.alertMiddleButtonTv.setText(middleMessageId == -1 ? middleMessage : context.getString(middleMessageId));
            mBinding.alertRightButtonTv.setTextColor(rightColor != -1
                    ? getResources().getColor(rightColor) : getResources().getColor(R.color.positive_color));
            mBinding.alertRightButtonTv.setText(rightMessageId == -1 ? rightMessage : context.getString(rightMessageId));
        } else {
            /*none*/
            mBinding.alertLeftButtonTv.setVisibility(View.GONE);
            mBinding.alertMiddleButtonTv.setVisibility(View.GONE);
            mBinding.alertRightButtonTv.setVisibility(View.GONE);
            mBinding.lineLeftV.setVisibility(View.GONE);
            mBinding.lineRightV.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (isKeyBoardShown) {
            isKeyBoardShown = false;
        }
    }

    /**
     * 设置标题并显示
     */
    public QzAlertFragment setTitle(String title) {
        this.title = title;
        isShowTitle = true;
        return this;
    }

    /**
     * 设置标题并显示
     */
    public QzAlertFragment setTitle(int title) {
        this.titleId = title;
        isShowTitle = true;
        return this;
    }

    /**
     * 设置文字内容
     */
    public QzAlertFragment setMessage(String message) {
        this.message = message;
        isShowContent = true;
        return this;
    }

    /**
     * 设置文字内容
     */
    public QzAlertFragment setMessage(int message) {
        this.messageId = message;
        isShowContent = true;
        return this;
    }

    /**
     * 设置带格式的文字内容
     */
    public QzAlertFragment setMessage(SpannableString spannableMessage) {
        this.spannableMessage = spannableMessage;
        isShowContent = true;
        return this;
    }

    /**
     * 设置编辑框提示内容并显示编辑框
     */
    public QzAlertFragment setEditTextHint(String editHint) {
        this.editHint = editHint;
        isShowEdit = true;
        return this;
    }

    /**
     * 设置编辑框提示内容并显示编辑框
     */
    public QzAlertFragment setEditTextHint(int editHint) {
        this.editHintId = editHint;
        isShowEdit = true;
        return this;
    }

    /**
     * 设置编辑max length
     */
    public QzAlertFragment setEditTextMaxLength(int length) {
        this.editMaxLength = length;
        isShowEdit = true;
        return this;
    }

    /**
     * 设置编辑框内容并显示编辑框
     */
    public QzAlertFragment setEditText(String editContent) {
        this.editContent = editContent;
        isShowEdit = true;
        return this;
    }

    /**
     * 设置编辑框内容并显示编辑框
     */
    public QzAlertFragment setEditText(int editContent) {
        this.editContentId = editContent;
        isShowEdit = true;
        return this;
    }

    /**
     * 设置是否显示编辑框
     */
    public QzAlertFragment showEditText(boolean isShow) {
        isShowEdit = isShow;
        return this;
    }

    public void onViewClicked() {
        mBinding.alertLeftButtonTv.setOnClickListener(v -> {
            if (leftClickListener != null && FastClickUtil.isNotFastClick(v)) {
                leftClickListener.onLeftClick(this);
            }
        });
        mBinding.alertMiddleButtonTv.setOnClickListener(v -> {
            if (middleClickListener != null && FastClickUtil.isNotFastClick(v)) {
                middleClickListener.onMiddleClick(this);
            }
        });
        mBinding.alertRightButtonTv.setOnClickListener(v -> {
            if (rightClickListener != null && FastClickUtil.isNotFastClick(v)) {
                rightClickListener.onRightClick(this, mBinding.alertContentEt.getText().toString());
            }
        });
    }

    public QzAlertFragment setContentGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }


    /**
     * 显示
     */
    public void show(FragmentActivity context) {
        show(context.getSupportFragmentManager(), QzAlertFragment.class.getSimpleName());
        this.context = context;
    }

    /**
     * 显示
     */
    public void show(FragmentActivity context, int width, int height) {
        show(context.getSupportFragmentManager(), QzAlertFragment.class.getSimpleName());
        this.width = width;
        this.height = height;
        this.context = context;
    }


    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
        new Handler().postDelayed(() -> {
            onShowEdit((ViewGroup) contentView);
        }, 200);
    }

    @Override
    public void dismiss() {
        ViewGroup rootView = (ViewGroup) contentView;
        View focusView = null;
        for (int index = 0; index < rootView.getChildCount(); index++) {
            View child = rootView.getChildAt(index);
            if (child.hasFocus()) {
                focusView = child;
                break;
            }
        }
        if (focusView != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
        new Handler().postDelayed(super::dismiss, focusView != null ? 200 : 0);
    }


    /**
     * 显示
     */
    public void showWithOutAnim(FragmentActivity context) {
        show(context.getSupportFragmentManager(), QzAlertFragment.class.getSimpleName());
        this.isAnimation = false;
        this.context = context;
    }

    /**
     * 显示
     */
    public void showWithOutAnim(FragmentActivity context, int width, int height) {
        show(context.getSupportFragmentManager(), QzAlertFragment.class.getSimpleName());
        this.width = width;
        this.height = height;
        this.isAnimation = false;
        this.context = context;
    }

    /**
     * 左侧按钮点击接口设置
     */
    public QzAlertFragment addOnLeftClickListener(String message, OnLeftClickListener negativeClickListener) {
        isNegativeButtonShow = true;
        leftMessage = message;
        this.leftClickListener = negativeClickListener;
        return this;
    }

    /**
     * 左侧按钮点击接口设置
     */
    public QzAlertFragment addOnLeftClickListener(String message, @ColorRes int color, OnLeftClickListener negativeClickListener) {
        isNegativeButtonShow = true;
        leftMessage = message;
        leftColor = color;
        this.leftClickListener = negativeClickListener;
        return this;
    }

    /**
     * 左侧按钮点击接口设置
     */
    public QzAlertFragment addOnLeftClickListener(int message, OnLeftClickListener negativeClickListener) {
        isNegativeButtonShow = true;
        leftMessageId = message;
        this.leftClickListener = negativeClickListener;
        return this;
    }

    /**
     * 左侧按钮点击接口设置
     */
    public QzAlertFragment addOnLeftClickListener(int message, @ColorRes int color, OnLeftClickListener negativeClickListener) {
        isNegativeButtonShow = true;
        leftMessageId = message;
        leftColor = color;
        this.leftClickListener = negativeClickListener;
        return this;
    }

    /**
     * 中间按钮点击设置
     */
    public QzAlertFragment addOnMiddleClickListener(String message, OnMiddleClickListener natureClickListener) {
        isNatureButtonShow = true;
        middleMessage = message;
        this.middleClickListener = natureClickListener;
        return this;
    }

    /**
     * 中间按钮点击设置
     */
    public QzAlertFragment addOnMiddleClickListener(String message, @ColorRes int color, OnMiddleClickListener natureClickListener) {
        isNatureButtonShow = true;
        middleMessage = message;
        middleColor = color;
        this.middleClickListener = natureClickListener;
        return this;
    }

    /**
     * 中间按钮点击设置
     */
    public QzAlertFragment addOnMiddleClickListener(int message, OnMiddleClickListener natureClickListener) {
        isNatureButtonShow = true;
        middleMessageId = message;
        this.middleClickListener = natureClickListener;
        return this;
    }

    /**
     * 中间按钮点击设置
     */
    public QzAlertFragment addOnMiddleClickListener(int message, @ColorRes int color, OnMiddleClickListener natureClickListener) {
        isNatureButtonShow = true;
        middleMessageId = message;
        middleColor = color;
        this.middleClickListener = natureClickListener;
        return this;
    }

    /**
     * 右侧按钮点击设置
     */
    public QzAlertFragment addOnRightClickListener(String message, OnRightClickListener positiveClickListener) {
        isPositiveButtonShow = true;
        rightMessage = message;
        this.rightClickListener = positiveClickListener;
        return this;
    }

    /**
     * 右侧按钮点击设置
     */
    public QzAlertFragment addOnRightClickListener(String message, @ColorRes int color, OnRightClickListener positiveClickListener) {
        isPositiveButtonShow = true;
        rightMessage = message;
        rightColor = color;
        this.rightClickListener = positiveClickListener;
        return this;
    }

    /**
     * 右侧按钮点击设置
     */
    public QzAlertFragment addOnRightClickListener(int message, OnRightClickListener positiveClickListener) {
        isPositiveButtonShow = true;
        rightMessageId = message;
        this.rightClickListener = positiveClickListener;
        return this;
    }

    /**
     * 右侧按钮点击设置
     */
    public QzAlertFragment addOnRightClickListener(int message, @ColorRes int color, OnRightClickListener positiveClickListener) {
        isPositiveButtonShow = true;
        rightColor = color;
        rightMessageId = message;
        this.rightClickListener = positiveClickListener;
        return this;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public QzAlertFragment setContentView(View contentView) {
        this.isCustomView = true;
        this.contentView = contentView;
        return this;
    }

    public QzAlertFragment setContentView(int layoutId) {
        this.isCustomView = true;
        this.layoutId = layoutId;
        return this;
    }

    public QzAlertFragment setShowListener(OnAlertShowListener showListener) {
        this.showListener = showListener;
        return this;
    }

    public interface OnLeftClickListener {
        /**
         * Menu 左侧按钮点击回调
         */
        void onLeftClick(QzAlertFragment dialog);

    }

    public interface OnMiddleClickListener {
        /**
         * Menu 中间按钮点击回调
         */
        void onMiddleClick(QzAlertFragment dialog);

    }

    public interface OnRightClickListener {

        /**
         * Menu 右侧按钮点击回调
         *
         * @param content 输入内容
         */
        void onRightClick(QzAlertFragment dialog, String content);
    }

    public interface OnAlertShowListener {
        /**
         * 显示监听
         *
         * @param contentView
         * @param dialog
         */
        void onShow(View contentView, QzAlertFragment dialog);
    }

    private void onShowEdit(ViewGroup rootView) {
        View focusView = null;
        for (int index = 0; index < rootView.getChildCount(); index++) {
            View child = rootView.getChildAt(index);
            if (child instanceof EditText && child.getVisibility() == View.VISIBLE) {
                focusView = child;
                break;
            } else if (child instanceof ViewGroup) {
                onShowEdit((ViewGroup) child);
            }
        }
        if (focusView != null) {
            focusView.findFocus();
            focusView.requestFocus();
            focusView.requestFocusFromTouch();
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(focusView, InputMethodManager.SHOW_FORCED);
        }

    }


}
