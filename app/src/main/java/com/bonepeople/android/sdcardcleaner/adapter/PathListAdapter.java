package com.bonepeople.android.sdcardcleaner.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bonepeople.android.sdcardcleaner.R;

import java.util.ArrayList;

/**
 * 用于展示保留列表和待清理列表的数据适配器
 * Created by bonepeople on 2017/12/5.
 */

public class PathListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String ACTION_CLICK_ITEM = "click_item";
    private ArrayList<String> _data;
    private View.OnClickListener _listener_click;

    public PathListAdapter(View.OnClickListener _listener_click) {
        this._listener_click = _listener_click;
    }

    public void set_data(ArrayList<String> _data) {
        this._data = _data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            TextView _textView = new TextView(parent.getContext());
            _textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            _textView.setGravity(Gravity.CENTER);
            _textView.setText(parent.getContext().getString(R.string.state_emptyView));
            return new ViewHolder_empty(_textView);
        } else {
            TextView _textView = new TextView(parent.getContext());
            _textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            _textView.setPadding(20, 20, 20, 20);
            return new ViewHolder_data(_textView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder_data) {
            ViewHolder_data _viewHolder = (ViewHolder_data) holder;
            _viewHolder._text_name.setText(_data.get(position));
            _viewHolder._text_name.setTag(new String[]{ACTION_CLICK_ITEM, String.valueOf(position)});
        }
    }

    @Override
    public int getItemCount() {
        if (_data.size() == 0)
            return 1;
        else
            return _data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (_data.size() == 0)
            return 1;
        else
            return super.getItemViewType(position);
    }

    class ViewHolder_data extends RecyclerView.ViewHolder {
        TextView _text_name;

        ViewHolder_data(TextView itemView) {
            super(itemView);
            _text_name = itemView;
            _text_name.setOnClickListener(_listener_click);
        }
    }

    class ViewHolder_empty extends RecyclerView.ViewHolder {
        TextView _text_title;

        ViewHolder_empty(TextView itemView) {
            super(itemView);
            _text_title = itemView;
        }
    }
}
