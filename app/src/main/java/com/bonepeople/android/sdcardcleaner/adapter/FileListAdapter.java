package com.bonepeople.android.sdcardcleaner.adapter;

import android.animation.ArgbEvaluator;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件列表的数据适配器
 * <p>
 * Created by bonepeople on 2017/6/26.
 */

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.ViewHolder> {
    public static final String PART_CHECKBOX = "checkBox";
    public static final String ACTION_CLICK_ITEM = "click_item";
    private static final int COLOR_START = 0xFFEAD799;
    private static final int COLOR_END = 0xFFE96E3E;
    private static final ArgbEvaluator evaluator = new ArgbEvaluator();
    private SDFile data;
    private View.OnClickListener listener_click;
    private View.OnLongClickListener listener_long;
    private boolean multiSelect = false;//是否处于多选状态中
    private ArrayList<Integer> checkList = new ArrayList<>();//已选项目集合

    public FileListAdapter(View.OnClickListener listener_click, View.OnLongClickListener listener_long) {
        this.listener_click = listener_click;
        this.listener_long = listener_long;
    }

    public void setData(SDFile data) {
        this.data = data;
    }

    public SDFile getData() {
        return data;
    }

    public ArrayList<Integer> getCheckList() {
        return checkList;
    }

    /**
     * 设置多选状态
     */
    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
        if (multiSelect)
            checkList.clear();
    }

    /**
     * 设置已选项目
     *
     * @param position 已选项目在集合中的位置，-1为全选
     * @return 是否已经全选所有项目
     */
    public boolean set_checkList(int position) {
        if (position == -1) {//全选标记
            if (checkList.size() == data.getChildren().size()) {//已全选
                checkList.clear();
            } else {//未全选
                checkList.clear();
                for (int temp_i = 0; temp_i < data.getChildren().size(); temp_i++)
                    checkList.add(temp_i);
            }
        } else {//普通位置序号
            if (checkList.contains(position)) {
                checkList.remove(Integer.valueOf(position));
            } else {
                checkList.add(position);
            }
        }
        return checkList.size() == data.getChildren().size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
        if (payloads.size() == 0) {
            onBindViewHolder(holder, position);
        }
        //设置复选框状态
        if (multiSelect) {
            if (holder.checkbox.getVisibility() == CheckBox.GONE)
                holder.checkbox.setVisibility(CheckBox.VISIBLE);
            if (checkList.contains(position))
                holder.checkbox.setChecked(true);
            else
                holder.checkbox.setChecked(false);
        } else {
            if (holder.checkbox.getVisibility() == CheckBox.VISIBLE) {
                holder.checkbox.setVisibility(CheckBox.GONE);
                holder.checkbox.setChecked(false);
            }
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SDFile temp_data = data.getChildren().get(position);
        //设置文件大小比例条
        float percent = (float) NumberUtil.div(temp_data.get_sizePercent(), 100, 2);
        PercentRelativeLayout.LayoutParams params = new PercentRelativeLayout.LayoutParams(0, 0);
        params.getPercentLayoutInfo().widthPercent = percent;
        holder.view_percent.setLayoutParams(params);
        int color = (int) evaluator.evaluate(percent, COLOR_START, COLOR_END);
        holder.view_percent.setBackgroundColor(color);
        //设置清理标志
        if (temp_data.isRubbish())
            holder.image_rubbish.setVisibility(ImageView.VISIBLE);
        else
            holder.image_rubbish.setVisibility(ImageView.GONE);
        //设置类型图标
        if (temp_data.isDirectory())
            holder.image_type.setImageResource(R.drawable.icon_directory);
        else
            holder.image_type.setImageResource(R.drawable.icon_file);
        //设置基本信息
        holder.text_name.setText(temp_data.getName());
        String str_size;
        if (temp_data.isDirectory())
            str_size = holder.view_click.getContext().getString(R.string.state_directory_size, Formatter.formatFileSize(holder.view_click.getContext(), temp_data.getSize()), temp_data.getFileCount());
        else
            str_size = Formatter.formatFileSize(holder.view_click.getContext(), temp_data.getSize());
        holder.text_size.setText(str_size);
        holder.view_click.setTag(new String[]{ACTION_CLICK_ITEM, String.valueOf(position)});
    }

    @Override
    public int getItemCount() {
        return data.getChildren().size();
    }

    public boolean isMultiSelect() {
        return multiSelect;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View view_percent;
        CheckBox checkbox;
        ImageView image_rubbish;
        ImageView image_type;
        TextView text_name;
        TextView text_size;
        View view_click;

        ViewHolder(View itemView) {
            super(itemView);
            view_percent = itemView.findViewById(R.id.view_percent);
            checkbox = itemView.findViewById(R.id.checkbox_item);
            image_rubbish = itemView.findViewById(R.id.imageview_rubbish);
            image_type = itemView.findViewById(R.id.imageview_type);
            text_name = itemView.findViewById(R.id.textview_name);
            text_size = itemView.findViewById(R.id.textview_size);
            view_click = itemView.findViewById(R.id.view_click);
            view_click.setOnClickListener(listener_click);
            view_click.setOnLongClickListener(listener_long);
        }
    }
}
