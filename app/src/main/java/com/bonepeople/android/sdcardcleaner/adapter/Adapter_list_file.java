package com.bonepeople.android.sdcardcleaner.adapter;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil;

/**
 * 文件列表的数据适配器
 * <p>
 * Created by bonepeople on 2017/6/26.
 */

public class Adapter_list_file extends RecyclerView.Adapter<Adapter_list_file.ViewHolder> {
    private static final int COLOR_START = 0xFFEAD799;
    private static final int COLOR_END = 0xFFE96E3E;
    private static final ArgbEvaluator _evaluator = new ArgbEvaluator();
    private Context _context;
    private SDFile _data;

    public Adapter_list_file(Context _context) {
        this._context = _context;
    }

    public void set_data(SDFile _data) {
        this._data = _data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View _view = LayoutInflater.from(_context).inflate(R.layout.item_list_file, parent, false);
        return new ViewHolder(_view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SDFile _temp_data = _data.get_children().get(position);
        float _percent = (float) NumberUtil.div(_temp_data.get_sizePercent(), 100, 2);
        PercentRelativeLayout.LayoutParams _params = new PercentRelativeLayout.LayoutParams(0, 0);
        _params.getPercentLayoutInfo().widthPercent = _percent;
        holder._view_percent.setLayoutParams(_params);
        int _color = (int) _evaluator.evaluate(_percent,COLOR_START,COLOR_END);
        holder._view_percent.setBackgroundColor(_color);
        if (_temp_data.isDirectory())
            holder._image_type.setImageResource(R.drawable.icon_directory);
        else
            holder._image_type.setImageResource(R.drawable.icon_file);
        holder._text_name.setText(_temp_data.get_name());
        holder._text_size.setText(Formatter.formatFileSize(_context, _temp_data.get_size()));
    }

    @Override
    public int getItemCount() {
        return _data.get_children().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View _view_percent;
        public ImageView _image_type;
        public TextView _text_name;
        public TextView _text_size;

        public ViewHolder(View itemView) {
            super(itemView);
            _view_percent = itemView.findViewById(R.id.view_percent);
            _image_type = (ImageView) itemView.findViewById(R.id.imageview_type);
            _text_name = (TextView) itemView.findViewById(R.id.textview_name);
            _text_size = (TextView) itemView.findViewById(R.id.textview_size);
        }
    }
}
