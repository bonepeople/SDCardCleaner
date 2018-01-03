package com.bonepeople.android.sdcardcleaner.widget;

import android.content.Context;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.text.format.Formatter;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil;

/**
 * 用于显示SD卡中文件大小占比的控件
 * <p>
 * Created by bonepeople on 2017/12/30.
 */

public class SDCardPercent extends ConstraintLayout {
    private ConstraintLayout _line;
    private ConstraintSet _set_line = new ConstraintSet();
    private TextView _text_system, _text_blank, _text_file, _text_rubbish;
    private long _space_total = Environment.getExternalStorageDirectory().getTotalSpace();

    public SDCardPercent(Context context) {
        this(context, null);
    }

    public SDCardPercent(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SDCardPercent(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setContentView();
    }

    private void setContentView() {
        int _padding = NumberUtil.get_px(getContext(), 15);
        setPadding(_padding, _padding, _padding, _padding);
        inflate(getContext(), R.layout.widget_sdcard_percent, this);

        _line = (ConstraintLayout) findViewById(R.id.block_percent);
        _text_system = (TextView) findViewById(R.id.textView_count_system);
        _text_blank = (TextView) findViewById(R.id.textView_count_blank);
        _text_file = (TextView) findViewById(R.id.textView_count_file);
        _text_rubbish = (TextView) findViewById(R.id.textView_count_rubbish);

        for (int _temp_i = 0; _temp_i < _line.getChildCount() && _line.getChildAt(_temp_i).getId() == -1; _temp_i++) {
            _line.getChildAt(_temp_i).setId(_temp_i);
        }
        _set_line.clone(_line);
    }

    /**
     * 刷新数据
     */
    public void refresh() {
        long _space_free = Environment.getExternalStorageDirectory().getFreeSpace();
        long _fileCount_all = Global.get_fileCount_all();
        long _fileSize_all = Global.get_fileSize_all();
        long _fileCount_rubbish = Global.get_fileCount_rubbish();
        long _fileSize_rubbish = Global.get_fileSize_rubbish();

        TransitionManager.beginDelayedTransition(_line);
        _set_line.setGuidelinePercent(R.id.guideLine_rubbish, (float) NumberUtil.div(_fileSize_rubbish, _space_total, 3));
        _set_line.setGuidelinePercent(R.id.guideLine_file, (float) NumberUtil.div(_fileSize_all, _space_total, 3));
        _set_line.setGuidelinePercent(R.id.guideLine_system, (float) (1 - NumberUtil.div(_space_free, _space_total, 3)));
        _set_line.applyTo(_line);

        _text_system.setText(getContext().getString(R.string.state_fileCount_system, Formatter.formatFileSize(getContext(), _space_total - _space_free - _fileSize_all)));
        _text_blank.setText(getContext().getString(R.string.state_fileCount_blank, Formatter.formatFileSize(getContext(), _space_free)));
        _text_file.setText(getContext().getString(R.string.state_fileCount_file, Formatter.formatFileSize(getContext(), _fileSize_all), _fileCount_all));
        _text_rubbish.setText(getContext().getString(R.string.state_fileCount_rubbish, Formatter.formatFileSize(getContext(), _fileSize_rubbish), _fileCount_rubbish));
    }
}
