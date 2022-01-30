package com.bonepeople.android.sdcardcleaner.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bonepeople.android.sdcardcleaner.R;

/**
 * 自定义的标题栏控件
 * <p>
 * Created by bonepeople on 2018/1/8.
 */

public class TitleBar extends ConstraintLayout {
    TextView textView_title;

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

        textView_title = findViewById(R.id.textView_title);
    }

    public void setTitle(CharSequence title) {
        textView_title.setText(title);
    }

    public void setTitle(int titleId) {
        textView_title.setText(titleId);
    }
}
