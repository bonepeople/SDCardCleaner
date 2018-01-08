package com.bonepeople.android.sdcardcleaner.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bonepeople.android.sdcardcleaner.R;

/**
 * 自定义的标题栏控件
 * <p>
 * Created by bonepeople on 2018/1/8.
 */

public class TitleBar extends ConstraintLayout {
    TextView _text_title;

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setContentView();
    }

    private void setContentView() {
        inflate(getContext(), R.layout.widget_title_bar, this);

        _text_title = (TextView) findViewById(R.id.textView_title);
    }

    public void setTitle(CharSequence _title) {
        _text_title.setText(_title);
    }

    public void setTitle(int _titleId) {
        _text_title.setText(_titleId);
    }
}
