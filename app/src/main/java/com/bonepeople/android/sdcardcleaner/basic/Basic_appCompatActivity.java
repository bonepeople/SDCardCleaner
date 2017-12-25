package com.bonepeople.android.sdcardcleaner.basic;

import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import java.util.LinkedList;

/**
 * 集成对Handler控制的基类
 * <p>
 * Created by bonepeople on 2017/12/25.
 */

public abstract class Basic_appCompatActivity extends AppCompatActivity {
    private LinkedList<Basic_handler> _handlers = null;

    protected final Basic_handler createHandler() {
        if (_handlers == null)
            _handlers = new LinkedList<>();
        Basic_handler _handler = new Basic_handler(this);
        _handlers.add(_handler);
        return _handler;
    }

    protected void handleMessage(Message _msg) {
    }

    @Override
    protected void onDestroy() {
        if (_handlers != null) {
            for (Basic_handler _handler : _handlers) {
                _handler.destroy();
            }
            _handlers.clear();
            _handlers = null;
        }
        super.onDestroy();
    }
}
