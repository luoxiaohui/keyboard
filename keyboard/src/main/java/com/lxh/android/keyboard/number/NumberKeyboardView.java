package com.lxh.android.keyboard.number;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.GridLayout;
import android.text.TextUtils;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.lxh.android.keyboard.R;
import com.lxh.android.keyboard.core.IKeyboardView;
import com.lxh.android.keyboard.core.KeyboardAction;
import com.lxh.android.keyboard.listener.OnActionListener;

/**
 * 数字键盘
 * @author luoxiaohui
 * @createTime 2019/1/30 11:57 AM
 */
public class NumberKeyboardView implements IKeyboardView, View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {

    private Handler mHandler;
    private Runnable mDeleteAction;

    private View mKeyboardView;
    private View btnDelete;
    private View btnX1;
    private Button btnX2;
    private Button btnDone;

    private OnActionListener mOnActionListener;

    public NumberKeyboardView(Window window) {
        mHandler = new Handler();
        mDeleteAction = new DeleteKeyAction();

        initViews(window);
        setChildKeys();

        btnDelete.setOnLongClickListener(this);
        mKeyboardView.findViewById(R.id.buttonHide).setOnClickListener(this);
    }

    private void initViews(Window window) {
        LayoutInflater layoutInflater = (LayoutInflater) window.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (layoutInflater != null) {
            mKeyboardView = layoutInflater.inflate(R.layout.keyboard_number_layout, null);
        }

        btnX1 = mKeyboardView.findViewById(R.id.btnX1);
        btnX2 = mKeyboardView.findViewById(R.id.btnX2);
        btnDone = mKeyboardView.findViewById(R.id.btnDone);
        btnDelete = mKeyboardView.findViewById(R.id.btnDelete);
    }

    private void setChildKeys() {
        GridLayout gridLayout = mKeyboardView.findViewById(R.id.gl_keyboard);

        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View child = gridLayout.getChildAt(i);
            if (child.getTag() != null) {
                child.setOnClickListener(NumberKeyboardView.this);
                child.setOnTouchListener(NumberKeyboardView.this);
            }
        }
    }

    @Override
    public void setOnActionListener(OnActionListener listener) {
        this.mOnActionListener = listener;
    }

    @Override
    public void setKeyFlag(String flag) {

        if (flag == null) {
            flag = NumberInputType.INPUT_TYPE_DIGITAL;
        }

        setButtonX(flag);
    }


    private void setButtonX(String type) {

        if (type.equals(NumberInputType.INPUT_TYPE_ID_NUMBER)) {
            btnX1.setEnabled(false);
            btnX2.setEnabled(true);
            btnX2.setTag("X");
            btnX2.setText(R.string.keyboard_number_x);
        } else if (type.equals(NumberInputType.INPUT_TYPE_TELEPHONE)) {
            btnX1.setEnabled(false);
            btnX2.setEnabled(true);
            btnX2.setTag("+");
            btnX2.setText(R.string.keyboard_number_plus);
        } else {
            btnX1.setEnabled(true);
            btnX2.setEnabled(true);
            btnX2.setTag("00");
            btnX2.setText(R.string.keyboard_number_00);
        }

    }

    @Override
    public void setActionLabel(CharSequence actionLabel) {
        if (!TextUtils.isEmpty(actionLabel)) {
            btnDone.setText(actionLabel);
        } else {
            btnDone.setText(R.string.keyboard_confirm);
        }
    }

    @Override
    public View getKeyboardView() {
        return mKeyboardView;
    }

    @Override
    public void onClick(View view) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        performClick(view);
    }

    private void performClick(View view) {

        if (view.getId() == R.id.btnDelete) {
            mOnActionListener.onAction(KeyboardAction.ACTION_DELETE);
        } else if (view.getId() == R.id.btnDone) {
            mOnActionListener.onAction(KeyboardAction.ACTION_ENTER);
        } else if (view.getId() == R.id.buttonHide) {
            mOnActionListener.onAction(KeyboardAction.ACTION_HIDE);
        } else {
            String code = (String) view.getTag();
            mOnActionListener.onEnter(code);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mDeleteAction != null && view.getId() == R.id.btnDelete) {

                mHandler.removeCallbacks(mDeleteAction);
            }
        }
        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        mHandler.postDelayed(mDeleteAction, 60);
        return false;
    }

    private class DeleteKeyAction implements Runnable {
        @Override
        public void run() {

            if (btnDelete.isPressed()) {
                mOnActionListener.onAction(KeyboardAction.ACTION_DELETE);
                mHandler.postDelayed(mDeleteAction, 60);
            }
        }
    }
}
