package com.test.viewsave;

import android.content.Context;

/**
 * Created by zhuangyufeng on 2018/9/21.
 */

public class FrameworkUtil {
    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    public static Context getContext() {
        return mContext;
    }
}
