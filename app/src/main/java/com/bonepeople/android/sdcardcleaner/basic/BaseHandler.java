package com.bonepeople.android.sdcardcleaner.basic;

import android.os.Handler;
import android.os.Message;

/**
 * 公共的Handler类
 * <p>
 * Created by bonepeople on 2017/12/25.
 */

public class BaseHandler extends Handler {
    private BaseAppCompatActivity activity;

    BaseHandler(BaseAppCompatActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        if (activity != null && !activity.isDestroyed())
            activity.handleMessage(msg);
    }

    void destroy() {
        activity = null;
        removeCallbacksAndMessages(null);
    }
}
