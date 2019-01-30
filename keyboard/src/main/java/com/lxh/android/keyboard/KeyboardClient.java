package com.lxh.android.keyboard;

import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.lxh.android.keyboard.core.BkjfKeyboard;
import com.lxh.android.keyboard.core.KeyboardAction;
import com.lxh.android.keyboard.core.KeyboardUtils;
import com.lxh.android.keyboard.listener.OnActionListener;

import java.lang.reflect.Method;

/**
 *
 * @author luoxiaohui
 * @createTime 2019/1/30 11:57 AM
 */
public class KeyboardClient {

    private static final String SET_SHOW_SOFT_INPUTS_ON_FOCUS = "setShowSoftInputOnFocus";
    private boolean mDismissWhenStart;
    private Handler mHandler = new Handler();
    private Window mWindow;
    private BkjfKeyboard mKeyboard;
    private EditText mEditText;

    /**
     * @param isAttachActivity 数字键盘是否依附于Activity/Fragment上,默认是true
     */
    private KeyboardClient(Window window, boolean isAttachActivity) {
        mWindow = window;
        initKeyboard(window, isAttachActivity);
    }

    public static KeyboardClient newKeyboard(Window window) {
        return new KeyboardClient(window, true);
    }

    public static KeyboardClient newKeyboard(Window window, boolean isAttachActivity) {
        return new KeyboardClient(window, isAttachActivity);
    }

    private void initKeyboard(Window window, boolean isAttachActivity) {

        ViewGroup keyboardRootView = (ViewGroup) LayoutInflater.from(window.getContext()).inflate(R.layout.keyboard_container, null);
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int height = KeyboardUtils.getKeyboardHeight(window);
        mKeyboard = new BkjfKeyboard(window, keyboardRootView, windowWidth, height, isAttachActivity);
        mKeyboard.setOnKeyboardActionListener(new DefaultActionListener());
        window.getDecorView().getViewTreeObserver().addOnGlobalFocusChangeListener(new DefaultOnGlobalFocusChangeListener());
        hideKeyboard();
    }

    private void showKeyboard(EditText mEditText) {
        if (mEditText != null) {
            mKeyboard.setImeActionLabel(mEditText.getImeActionLabel());
        }
        mKeyboard.display(mEditText);
    }

    /**
     * 如果默认就要显示键盘的话，必须要延时一段时间
     * 等待ViewTreeObserver.OnGlobalFocusChangeListener回调onGlobalFocusChanged方法
     * 从而获取到不为空的EditText的实例
     * @author luoxiaohui
     * @createTime 2019/1/28 7:35 PM
     */
    public void showKeyboard() {

        mDismissWhenStart = false;
        mHandler.postDelayed(task, 1000);
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            if (mEditText != null && mEditText.isFocused()) {
                String tag = (String) mEditText.getTag();
                if (tag != null) {
                    showKeyboard(mEditText);
                }
            }
        }
    };

    private void hideKeyboard() {
        mDismissWhenStart = true;
        mKeyboard.dismiss();
    }

    /**
     * 焦点变化监听
     */
    private class DefaultOnGlobalFocusChangeListener implements ViewTreeObserver.OnGlobalFocusChangeListener {

        /**
         * 是否是第一次进入
         */
        private boolean isFirstEnter = true;

        @Override
        public void onGlobalFocusChanged(View oldFocus, View newFocus) {

            if (mEditText != newFocus) {
                mEditText = null;
            }

            if (newFocus instanceof EditText) {

                mEditText = (EditText) newFocus;
                String tag = (String) mEditText.getTag();

                if (tag != null) {

                    showSoftInputs(mEditText, SET_SHOW_SOFT_INPUTS_ON_FOCUS);
                    setHideKeyboardListener();
                    setShowKeyboardListener();

                    if(isFirstEnter && mDismissWhenStart){
                        mKeyboard.dismiss();
                        isFirstEnter = false;
                        return ;
                    }

                    hideDefaultKeyboard();
                    showKeyboard(mEditText);
                    return;
                }
            }

            /**
             * 如果没有tag，就隐藏自定义键盘
             */
            hideKeyboard();
        }

        /**
         * 隐藏系统输入法
         */
        private void hideDefaultKeyboard() {
            InputMethodManager imm = (InputMethodManager) mWindow.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
            }
        }


        private void showSoftInputs(View view, String method) {
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod(method,
                        boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(view, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setShowKeyboardListener() {
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mKeyboard.isShowing()) {
                    showKeyboard(mEditText);
                }
            }
        });
    }

    private void setHideKeyboardListener() {
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mKeyboard.isShowing() && keyCode == KeyEvent.KEYCODE_BACK) {
                    mKeyboard.dismiss();
                    return true;
                }
                return false;
            }
        });
    }


    /**
     * 键盘行为监听-隐藏-删除-完成
     */
    private class DefaultActionListener implements OnActionListener {

        @Override
        public void onEnter(String inputText) {

            if (mEditText != null && mEditText.isFocused()) {
                handleInput(inputText);
            }
        }

        @Override
        public void onAction(KeyboardAction action) {
            if (action == KeyboardAction.ACTION_HIDE) {
                mKeyboard.dismiss();
            }

            if (mEditText != null && mEditText.isFocused()) {

                int selectionStart = mEditText.getSelectionStart();
                int selectionEnd = mEditText.getSelectionEnd();

                switch (action) {
                    case ACTION_DELETE:
                        handleActionDelete(selectionStart, selectionEnd);
                        break;
                    case ACTION_ENTER:
                        handleActionEnter(selectionStart, selectionEnd);
                        break;
                    case ACTION_HIDE:
                        hideKeyboard();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void handleActionDelete(int selectionStart, int selectionEnd) {
        if (mEditText.getText().length() > 0 && selectionStart != -1) {
            Editable editableText = mEditText.getEditableText();

            if (selectionEnd - selectionStart == 0) {
                if (selectionStart > 0) {
                    editableText.delete(selectionStart - 1, selectionStart);
                }
            } else {
                editableText.delete(selectionStart, selectionEnd);
            }
        }
    }

    private void handleActionEnter(int selectionStart, int selectionEnd) {
        if (isMultiLine()) {
            if (mEditText.getText().length() > 0 && selectionStart != -1 && selectionEnd - selectionStart > 0) {
                mEditText.getEditableText().delete(selectionStart, selectionEnd);
            }
            mEditText.getEditableText().insert(selectionStart, "\n");
        } else {
            mEditText.onEditorAction(EditorInfo.IME_ACTION_NEXT);
        }
    }

    private boolean isMultiLine() {
        return (mEditText.getMaxLines() > 1) && ((mEditText.getInputType() & InputType.TYPE_MASK_FLAGS) & InputType.TYPE_TEXT_FLAG_MULTI_LINE) == InputType
                .TYPE_TEXT_FLAG_MULTI_LINE;
    }

    private void handleInput(String inputText) {

        int selectionStart = mEditText.getSelectionStart();
        int selectionEnd = mEditText.getSelectionEnd();

        if (mEditText.getText().length() > 0 && selectionStart != -1 && selectionEnd - selectionStart > 0) {
            mEditText.getEditableText().delete(selectionStart, selectionEnd);
        }

        mEditText.getEditableText().insert(selectionStart, inputText);
    }
}
