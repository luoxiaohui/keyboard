package com.lxh.android.keyboard.core;

import android.view.View;
import com.lxh.android.keyboard.listener.OnActionListener;

/**
 * 所有键盘必须扩展此接口
 * @author luoxiaohui
 * @createTime 2019/1/30 11:57 AM
 */
public interface IKeyboardView {

    /**
     * 按钮点击事件
     * @author luoxiaohui
     * @createTime 2019/1/24 3:37 PM
     * @param listener
     */
    void setOnActionListener(OnActionListener listener);

    /**
     * 设置flag
     * @author luoxiaohui
     * @createTime 2019/1/24 3:37 PM
     * @param flag
     */
    void setKeyFlag(String flag);

    /**
     * 设置时间标签
     * @author luoxiaohui
     * @createTime 2019/1/24 3:37 PM
     * @param actionLabel 点击的字符串
     */
    void setActionLabel(CharSequence actionLabel);

    /**
     * getKeyboardView 获取键盘View
     * @author luoxiaohui
     * @createTime 2019/1/24 3:38 PM
     * @return
     */
    View getKeyboardView();
}