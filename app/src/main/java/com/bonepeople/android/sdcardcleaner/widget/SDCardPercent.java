package com.bonepeople.android.sdcardcleaner.widget;

import android.content.Context;
import android.os.Environment;
import android.text.format.Formatter;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil;

import java.util.Locale;

/**
 * 用于显示SD卡中文件大小占比的控件
 * <p>
 * Created by bonepeople on 2017/12/30.
 */

public class SDCardPercent extends ConstraintLayout {
    private ConstraintLayout line;
    private ConstraintSet set_line = new ConstraintSet();
    private TextView textView_system, textView_blank, textView_file, textView_rubbish;
    private long space_total = Environment.getExternalStorageDirectory().getTotalSpace();

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
        //将这个控件的Context设置为Locale.ENGLISH，这样在使用Formatter的时候格式化的单位就是MB、GB了，中文系统默认是中文单位
        getContext().getResources().getConfiguration().setLocale(Locale.ENGLISH);
        int padding = NumberUtil.get_px(getContext(), 15);
        setPadding(padding, padding, padding, padding);
        inflate(getContext(), R.layout.widget_sdcard_percent, this);

        line = findViewById(R.id.block_percent);
        textView_system = findViewById(R.id.textView_count_system);
        textView_blank = findViewById(R.id.textView_count_blank);
        textView_file = findViewById(R.id.textView_count_file);
        textView_rubbish = findViewById(R.id.textView_count_rubbish);

        for (int temp_i = 0; temp_i < line.getChildCount() && line.getChildAt(temp_i).getId() == -1; temp_i++) {
            line.getChildAt(temp_i).setId(temp_i);
        }
        set_line.clone(line);
    }

    /**
     * 刷新数据
     */
    public void refresh() {
        long space_free = Environment.getExternalStorageDirectory().getFreeSpace();
        long fileCount_all = Global.getFileCount_all();
        long fileSize_all = Global.getFileSize_all();
        long fileCount_rubbish = Global.getFileCount_rubbish();
        long fileSize_rubbish = Global.getFileSize_rubbish();

        TransitionManager.beginDelayedTransition(line);
        set_line.setGuidelinePercent(R.id.guideLine_rubbish, (float) NumberUtil.div(fileSize_rubbish, space_total, 3));
        set_line.setGuidelinePercent(R.id.guideLine_file, (float) NumberUtil.div(fileSize_all, space_total, 3));
        set_line.setGuidelinePercent(R.id.guideLine_system, (float) (1 - NumberUtil.div(space_free, space_total, 3)));
        set_line.applyTo(line);

        textView_system.setText(getContext().getString(R.string.state_fileCount_system, Formatter.formatFileSize(getContext(), space_total - space_free - fileSize_all)));
        textView_blank.setText(getContext().getString(R.string.state_fileCount_blank, Formatter.formatFileSize(getContext(), space_free)));
        textView_file.setText(getContext().getString(R.string.state_fileCount_file, Formatter.formatFileSize(getContext(), fileSize_all), fileCount_all));
        textView_rubbish.setText(getContext().getString(R.string.state_fileCount_rubbish, Formatter.formatFileSize(getContext(), fileSize_rubbish), fileCount_rubbish));
    }
}
