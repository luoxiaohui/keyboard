package com.lxh.android.keyboard.listener;

import com.lxh.android.keyboard.core.KeyboardAction;

/**
 *
 * @author luoxiaohui
 * @createTime 2019/1/24 3:40 PM
 */
public interface OnActionListener {
    
    /**
     * onEnter
     * @author luoxiaohui
     * @createTime 2019/1/24 3:38 PM
     * @param input
     */
    void onEnter(String input);

    /**
     * onAction
     * @author luoxiaohui
     * @createTime 2019/1/24 3:39 PM
     * @param action
     */
    void onAction(KeyboardAction action);
}
