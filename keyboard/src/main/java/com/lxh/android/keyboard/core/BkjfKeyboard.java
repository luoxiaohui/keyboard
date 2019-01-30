package com.lxh.android.keyboard.core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.lxh.android.keyboard.listener.OnActionListener;
import com.lxh.android.keyboard.number.NumberInputType;
import com.lxh.android.keyboard.number.NumberKeyboardView;

import java.util.HashMap;

/**
 * 主键盘逻辑-使用PopupWindow来展示键盘
 *
 * @author luoxiaohui
 * @createTime 2019/1/25 3:21 PM
 */
public class BkjfKeyboard extends PopupWindow {

    private Window mWindow;
    private ViewGroup keyboardRootView;
    private HashMap<String, IKeyboardView> keyboardViews = new HashMap<>();
    private OnActionListener mOnActionListener;
    private CharSequence mActionLabel;
    /**
     * 数字键盘是否依附于Activity/Fragment上,
     * 默认是true
     */
    private boolean isAttachActivity = true;

    public BkjfKeyboard(Window window, ViewGroup container, int width, int height, boolean isAttachActivity) {
        super(container, width, height);
        keyboardRootView = container;
        this.isAttachActivity = isAttachActivity;
        this.mWindow = window;
    }

    public void display(EditText mEditText) {
        final IKeyboardView view = getKeyboardView(mEditText.getTag().toString());
        view.setActionLabel(mActionLabel);

        keyboardRootView.removeAllViews();
        keyboardRootView.addView(view.getKeyboardView());

        if (!isShowing()) {

            if (isAttachActivity) {

                showAtLocation(mWindow.getDecorView(), Gravity.BOTTOM, 0, KeyboardUtils.getNavigationBarHeight(mWindow));
            } else {
                /**
                 * 如果数字键盘不是依附于Activity/Fragment之上，
                 * 就需要处理下数字键盘的摆放位置
                 */
                IBinder windowToken = mEditText.getWindowToken();
                if (windowToken != null && windowToken.isBinderAlive()) {
                    Rect rect = new Rect();
                    mEditText.getRootView().getGlobalVisibleRect(rect);
                    Context editTextContext = mEditText.getContext();
                    Resources resources = editTextContext.getApplicationContext().getResources();
                    DisplayMetrics dm = resources.getDisplayMetrics();
                    int y = rect.bottom - dm.heightPixels;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                        setAttachedInDecor(false);
                    }
                    setTouchable(true);
                    showAtLocation(mEditText, Gravity.BOTTOM | Gravity.LEFT, 0, y);
                    update(getWidth(), getHeight());
                }
            }
        }
    }

    public void setImeActionLabel(CharSequence actionLabel) {
        this.mActionLabel = actionLabel;
    }

    public void setOnKeyboardActionListener(OnActionListener onActionListener) {
        this.mOnActionListener = onActionListener;
    }

    /**
     * 根据键盘的inputType获取相应的键盘View
     * 默认提供数字键盘
     */
    private IKeyboardView getKeyboardView(String rawTypes) {

        if (rawTypes == null) {
            return null;
        }
        String type = KeyboardUtils.getInputType(rawTypes);
        String flag = KeyboardUtils.getInputFlag(rawTypes);

        IKeyboardView keyboardView = keyboardViews.get(type);
        if (keyboardView == null) {
            switch (type) {
                case NumberInputType.INPUT_TYPE_NUMBER:
                    keyboardView = new NumberKeyboardView(mWindow);
                    keyboardViews.put(type, keyboardView);
                    break;
                default: {
                    try {
                        keyboardView = KeyboardUtils.getKeyboardViewFromInputType(type, mWindow);
                        keyboardViews.put(type, keyboardView);
                    } catch (Exception e) {
                        throw new RuntimeException("cannot recognize tag，please refer the doc", e);
                    }
                }
            }
        }

        keyboardView.setOnActionListener(mOnActionListener);
        keyboardView.setKeyFlag(flag);
        return keyboardView;
    }
}
