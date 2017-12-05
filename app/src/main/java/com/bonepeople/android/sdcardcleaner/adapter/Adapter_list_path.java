package com.bonepeople.android.sdcardcleaner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 用于展示保留列表和待清理列表的数据适配器
 * Created by bonepeople on 2017/12/5.
 */

public class Adapter_list_path extends RecyclerView.Adapter<Adapter_list_path.ViewHolder> {
    public static final String ACTION_CLICK_ITEM = "click_item";
    private ArrayList<String> _data;
    private View.OnClickListener _listener_click;

    public Adapter_list_path(View.OnClickListener _listener_click) {
        this._listener_click = _listener_click;
    }

    public void set_data(ArrayList<String> _data) {
        this._data = _data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView _textView = new TextView(parent.getContext());
        _textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        _textView.setPadding(20, 20, 20, 20);
        return new ViewHolder(_textView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder._text_name.setText(_data.get(position));
        holder._text_name.setTag(new String[]{ACTION_CLICK_ITEM, String.valueOf(position)});
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView _text_name;

        public ViewHolder(TextView itemView) {
            super(itemView);
            _text_name = itemView;
            _text_name.setOnClickListener(_listener_click);
        }
    }
}
