package com.bonepeople.android.sdcardcleaner.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bonepeople.android.sdcardcleaner.R;

import java.util.ArrayList;

/**
 * 用于展示保留列表和待清理列表的数据适配器
 * Created by bonepeople on 2017/12/5.
 */

public class PathListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String ACTION_CLICK_ITEM = "click_item";
    private ArrayList<String> data;
    private View.OnClickListener listener_click;

    public PathListAdapter(View.OnClickListener listener_click) {
        this.listener_click = listener_click;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.CENTER);
            textView.setText(parent.getContext().getString(R.string.state_emptyView));
            return new ViewHolder_empty(textView);
        } else {
            TextView textView = new TextView(parent.getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setPadding(20, 20, 20, 20);
            return new ViewHolder_data(textView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder_data) {
            ViewHolder_data viewHolder = (ViewHolder_data) holder;
            viewHolder.textView_name.setText(data.get(position));
            viewHolder.textView_name.setTag(new String[]{ACTION_CLICK_ITEM, String.valueOf(position)});
        }
    }

    @Override
    public int getItemCount() {
        if (data.size() == 0)
            return 1;
        else
            return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.size() == 0)
            return 1;
        else
            return super.getItemViewType(position);
    }

    class ViewHolder_data extends RecyclerView.ViewHolder {
        TextView textView_name;

        ViewHolder_data(TextView itemView) {
            super(itemView);
            textView_name = itemView;
            textView_name.setOnClickListener(listener_click);
        }
    }

    class ViewHolder_empty extends RecyclerView.ViewHolder {
        TextView textView_title;

        ViewHolder_empty(TextView itemView) {
            super(itemView);
            textView_title = itemView;
        }
    }
}
