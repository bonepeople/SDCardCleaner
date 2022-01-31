package com.bonepeople.android.sdcardcleaner.adapter;

import android.animation.ArgbEvaluator;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 文件浏览界面的数据适配器
 *
 * @author bonepeople
 */
public class FileExplorerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String PART_CHECKBOX = "FileExplorerAdapter_checkBox";
    public static final String ACTION_CLICK = "FileExplorerAdapter_click";
    private static final int TYPE_EMPTY = -1;
    private static final int TYPE_LOADING = 0;
    private static final int TYPE_ITEM = 1;
    private static final int COLOR_START = 0xFFEAD799;
    private static final int COLOR_END = 0xFFE96E3E;
    private static final ArgbEvaluator evaluator = new ArgbEvaluator();
    private boolean loading = true;
    private ArrayList<SDFile> data;
    private View.OnClickListener listener_click;
    private View.OnLongClickListener listener_long_click;
    private boolean multiSelect = false;//是否处于多选状态中
    private HashSet<Integer> checkedSet = new HashSet<>();//已选项目集合

    public FileExplorerAdapter(View.OnClickListener listener_click, View.OnLongClickListener listener_long_click) {
        this.listener_click = listener_click;
        this.listener_long_click = listener_long_click;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void setData(ArrayList<SDFile> data) {
        this.data = data;
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    /**
     * 设置多选状态
     */
    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
        if (multiSelect)
            checkedSet.clear();
    }

    public HashSet<Integer> getCheckedSet() {
        return checkedSet;
    }

    /**
     * 设置已选项目
     *
     * @param position 已选项目在集合中的位置，-1为全选
     * @return 是否已经全选所有项目
     */
    public boolean setCheckedSet(int position) {
        if (position == -1) {//全选标记
            if (checkedSet.size() == data.size()) {//已全选
                checkedSet.clear();
            } else {//未全选
                for (int temp_i = 0; temp_i < data.size(); temp_i++)
                    checkedSet.add(temp_i);
            }
        } else {//普通位置序号
            if (checkedSet.contains(position)) {
                checkedSet.remove(position);
            } else {
                checkedSet.add(position);
            }
        }
        return checkedSet.size() == data.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case TYPE_ITEM:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_explorer, parent, false);
                holder = new ViewHolder_item(view);
                break;
            case TYPE_LOADING: {
                TextView textView = new TextView(parent.getContext());
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                textView.setGravity(Gravity.CENTER);
                textView.setText("正在加载");
                holder = new ViewHolder_loading(textView);
                break;
            }
            default: {
                TextView textView = new TextView(parent.getContext());
                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                textView.setGravity(Gravity.CENTER);
                textView.setText("没有任何文件");
                holder = new ViewHolder_empty(textView);
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM: {
                SDFile info = data.get(position);
                ViewHolder_item holder_item = (ViewHolder_item) holder;
                //设置文件大小比例条
                float percent = (float) NumberUtil.div(info.get_sizePercent(), 100, 2);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) holder_item.view_percent.getLayoutParams();
                params.matchConstraintPercentWidth = percent;
                holder_item.view_percent.setLayoutParams(params);
                int color = (int) evaluator.evaluate(percent, COLOR_START, COLOR_END);
                holder_item.view_percent.setBackgroundColor(color);
                //设置清理标志
                if (info.isRubbish())
                    holder_item.imageView_rubbish.setVisibility(ImageView.VISIBLE);
                else
                    holder_item.imageView_rubbish.setVisibility(ImageView.GONE);
                //设置类型图标
                if (info.isDirectory())
                    holder_item.imageView_type.setImageResource(R.drawable.icon_directory);
                else
                    holder_item.imageView_type.setImageResource(R.drawable.icon_file);
                //设置基本信息
                holder_item.textView_name.setText(info.getName());
                String str_size;
                if (info.isDirectory())
                    str_size = holder_item.itemView.getContext().getString(R.string.state_directory_size, Formatter.formatFileSize(holder_item.itemView.getContext(), info.getSize()), info.getFileCount());
                else
                    str_size = Formatter.formatFileSize(holder_item.itemView.getContext(), info.getSize());
                holder_item.textView_description.setText(str_size);
                holder_item.itemView.setTag(R.id.tags, new String[]{ACTION_CLICK, String.valueOf(position)});
                break;
            }
            case TYPE_LOADING:
            case TYPE_EMPTY:

                break;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        }
        switch (getItemViewType(position)) {
            case TYPE_ITEM: {
                ViewHolder_item holder_item = (ViewHolder_item) holder;
                //设置复选框状态
                if (multiSelect) {
                    if (holder_item.checkBox.getVisibility() == CheckBox.GONE)
                        holder_item.checkBox.setVisibility(CheckBox.VISIBLE);
                    if (checkedSet.contains(position))
                        holder_item.checkBox.setChecked(true);
                    else
                        holder_item.checkBox.setChecked(false);
                } else {
                    if (holder_item.checkBox.getVisibility() == CheckBox.VISIBLE)
                        holder_item.checkBox.setVisibility(CheckBox.GONE);
                }
                break;
            }
            case TYPE_LOADING:
            case TYPE_EMPTY:

                break;
        }
    }

    @Override
    public int getItemCount() {
        if (loading)
            return 1;
        if (data == null || data.isEmpty())
            return 1;
        else
            return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (loading)
            return TYPE_LOADING;
        if (data == null || data.isEmpty())
            return TYPE_EMPTY;
        else
            return TYPE_ITEM;
    }

    private static class ViewHolder_empty extends RecyclerView.ViewHolder {
        private ViewHolder_empty(View itemView) {
            super(itemView);
        }
    }

    private static class ViewHolder_loading extends RecyclerView.ViewHolder {
        private ViewHolder_loading(View itemView) {
            super(itemView);
        }
    }

    private class ViewHolder_item extends RecyclerView.ViewHolder {
        View view_percent;
        ImageView imageView_type, imageView_rubbish;
        CheckBox checkBox;
        TextView textView_name, textView_description;

        ViewHolder_item(View itemView) {
            super(itemView);
            view_percent = itemView.findViewById(R.id.view_percent);
            imageView_type = itemView.findViewById(R.id.imageView_type);
            imageView_rubbish = itemView.findViewById(R.id.imageView_rubbish);
            checkBox = itemView.findViewById(R.id.checkBox);
            textView_name = itemView.findViewById(R.id.textView_name);
            textView_description = itemView.findViewById(R.id.textView_description);
            itemView.setOnClickListener(listener_click);
            itemView.setOnLongClickListener(listener_long_click);
        }
    }
}
