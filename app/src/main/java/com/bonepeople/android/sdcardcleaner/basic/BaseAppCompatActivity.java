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
    private LinkedList<BaseHandler> _handlers = null;

    protected final BaseHandler createHandler() {
        if (_handlers == null)
            _handlers = new LinkedList<>();
        BaseHandler _handler = new BaseHandler(this);
        _handlers.add(_handler);
        return _handler;
    }

    protected void handleMessage(Message _msg) {
    }

    @Override
    protected void onDestroy() {
        if (_handlers != null) {
            for (BaseHandler _handler : _handlers) {
                _handler.destroy();
            }
            _handlers.clear();
            _handlers = null;
        }
        super.onDestroy();
    }
}
