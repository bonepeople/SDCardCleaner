package com.bonepeople.android.sdcardcleaner.basic;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import java.util.LinkedList;

/**
 * 集成对Handler控制的基类
 * <p>
 * Created by bonepeople on 2017/12/25.
 */

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    private LinkedList<BaseHandler> handlers = null;

    protected final BaseHandler createHandler() {
        if (handlers == null)
            handlers = new LinkedList<>();
        BaseHandler handler = new BaseHandler(this);
        handlers.add(handler);
        return handler;
    }

    protected void handleMessage(Message msg) {
    }

    @Override
    protected void onDestroy() {
        if (handlers != null) {
            for (BaseHandler handler : handlers) {
                handler.destroy();
            }
            handlers.clear();
            handlers = null;
        }
        super.onDestroy();
    }
}
