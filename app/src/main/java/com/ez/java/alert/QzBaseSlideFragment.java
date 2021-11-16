package com.ez.java.alert;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ez.java.alert.databinding.FragmentBaseSlideAlertBinding;
import me.jessyan.autosize.utils.AutoSizeUtils;

/**
 * @author : ezhuwx
 * Describe :侧滑组件
 * Designed on 2021/9/28 0028
 * E-mail : ezhuwx@163.com
 * Update on 16:47 by ezhuwx
 */
public abstract class QzBaseSlideFragment extends DialogFragment {
    private static final String TAG = QzBaseSlideFragment.class.getSimpleName();
    private Window window;
    private OnDismissListener onDismissListener;
    private boolean isCanSlide = true;
    private int width;
    private float lastX = 0;
    private float offsetX = 0;
    private View rootView;
    private FragmentBaseSlideAlertBinding mBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SlideDialog);
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mBinding = FragmentBaseSlideAlertBinding.inflate(getLayoutInflater());
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        window = dialog.getWindow();
        rootView = mBinding.getRoot();
        //添加内容布局
        LinearLayout contentLl = rootView.findViewById(R.id.content_ll);
        View contentView = View.inflate(getContext(), getLayoutRes(), null);
        contentLl.addView(contentView);
        dialog.setContentView(rootView);
        initView();
        return dialog;
    }

    /**
     * 初始化内容布局
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        //关闭按钮
        mBinding.closeIv.setOnClickListener(v -> dismiss());
        //滑动事件处理
        if (isCanSlide) {
            rootView.setOnTouchListener((View v, MotionEvent event) -> {
                onSlideEvent(v, event);
                return true;
            });
        }
        //调用子类初始化方法
        bindView(rootView);
    }

    @Override
    public void onStart() {
        super.onStart();
        //默认全屏展开
        WindowManager.LayoutParams params = window.getAttributes();
        params.dimAmount = 0.4f;
        width = AutoSizeUtils.dp2px(requireContext(), 280);
        params.width = width;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.gravity = Gravity.TOP | Gravity.END;
        window.setAttributes(params);
        mBinding.statusIv.animate().rotation(360).setDuration(600).start();
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onCancel();
    }

    @LayoutRes
    public abstract int getLayoutRes();

    public abstract void bindView(View v);

    public void onCancel() {
    }

    /**
     * recyclerView滑动事件处理
     */
    protected void disposeRecyclerViewTouchEvent(RecyclerView... recyclerViews) {
        for (RecyclerView recyclerView : recyclerViews) {
            recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {
                    onSlideEvent(rootView, event);
                    return false;
                }

                @Override
                public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

                }

                @Override
                public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                }
            });
        }
    }

    /**
     * 滑动事件处理
     */
    protected void onSlideEvent(View view, MotionEvent event) {
        float x = event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                offsetX = x - lastX;
                if (offsetX > 0) {
                    mBinding.statusIv.setRotation(offsetX / width * 180);
                    view.setTranslationX(offsetX);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (offsetX > 0) {
                    //滑动不足三分之一回弹否则关闭
                    if (offsetX < width / 3f) {
                        mBinding.statusIv.setRotation(0);
                        view.setTranslationX(0);
                    } else {
                        dismiss();
                    }
                }
                break;
            default:
                break;
        }
    }

    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void setCanSlide(boolean canSlide) {
        isCanSlide = canSlide;
    }


    public interface OnSelFinishedListener {
        /**
         * 选择完成
         *
         * @param name 名称
         * @param id   ID
         */
        void onSelFinished(String name, String id);
    }

    public String getFragmentTag() {
        return TAG;
    }

    public interface OnDismissListener {
        /**
         * @param dialog dialog
         */
        void onDismiss(DialogInterface dialog);
    }

    public void show(FragmentManager fragmentManager) {
        show(fragmentManager, getFragmentTag());
    }

    public void show(FragmentManager fragmentManager, Bundle argument) {
        setArguments(argument);
        show(fragmentManager, getFragmentTag());
    }

}
