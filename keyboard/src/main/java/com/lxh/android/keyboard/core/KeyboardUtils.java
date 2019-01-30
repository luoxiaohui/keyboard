package com.lxh.android.keyboard.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.lxh.android.keyboard.R;

import java.lang.reflect.Constructor;

/**
 *
 * @author luoxiaohui
 * @createTime 2019/1/24 3:40 PM
 */
public class KeyboardUtils {

    public static int getWindowHeight(Window window) {
        Rect outRect = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(outRect);
        return outRect.height();
    }

    @SuppressLint("NewApi")
    public static boolean isNavigationBarShow(Window window) {
        Display display = window.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);
        return realSize.y != size.y;
    }

    public static int getNavigationBarHeight(Window window) {
        if (!isNavigationBarShow(window)) {
            return 0;
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP
                && (window.getAttributes().flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION) != 0) {
            Resources resources = window.getContext().getResources();
            int resourceId = resources.getIdentifier("navigation_bar_height",
                    "dimen", "android");

            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }


    public static String getInputType(String rawTypes) {
        if (rawTypes == null) {
            return null;
        }

        String[] types = rawTypes.split("\\|");

        return types[0];
    }


    public static String getInputFlag(String rawTypes) {

        String[] types = rawTypes.split("\\|");
        String flag = null;

        if (types.length > 1) {
            flag = types[1];
        }

        return flag;
    }

    @NonNull
    public static <T> T getKeyboardViewFromInputType(String type, Window window) throws Exception {
        T view;
        Class<? extends T> clazz = (Class<? extends T>) Class.forName(type);

        Constructor<? extends T> c = clazz.getConstructor(Window.class);

        view = c.newInstance(window);
        return view;
    }

    public static int getKeyboardHeight(Window window) {
        return window.getContext().getResources().getDimensionPixelSize(R.dimen.keyboard_height);
    }


    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context){
        int result = 0;
        int resourceId = context.getApplicationContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getApplicationContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
