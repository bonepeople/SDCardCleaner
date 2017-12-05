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

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.utils.NumberUtil;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 文件列表的数据适配器
 * <p>
 * Created by bonepeople on 2017/6/26.
 */

public class Adapter_list_file extends RecyclerView.Adapter<Adapter_list_file.ViewHolder> {
    public static final String ACTION_CLICK_ITEM = "click_item";
    private static final int COLOR_START = 0xFFEAD799;
    private static final int COLOR_END = 0xFFE96E3E;
    private static final ArgbEvaluator _evaluator = new ArgbEvaluator();
    private SDFile _data;
    private View.OnClickListener _listener_click;
    private View.OnLongClickListener _listener_long;
    private boolean _multiSelect = false;//是否处于多选状态中
    private HashSet<Integer> _checkedSet = new HashSet<>();//已选项目集合

    public Adapter_list_file(View.OnClickListener _listener_click, View.OnLongClickListener _listener_long) {
        this._listener_click = _listener_click;
        this._listener_long = _listener_long;
    }

    public void set_data(SDFile _data) {
        this._data = _data;
    }

    public SDFile get_data() {
        return _data;
    }

    /**
     * 设置多选状态
     */
    public void set_multiSelect(boolean _multiSelect) {
        this._multiSelect = _multiSelect;
        if (_multiSelect)
            _checkedSet.clear();
        notifyDataSetChanged();
    }

    /**
     * 设置已选项目
     *
     * @param _position 已选项目在集合中的位置，-1为全选
     * @return 是否已经全选所有项目
     */
    public boolean set_checkedSet(int _position) {
        if (_position == -1) {//全选标记
            if (_checkedSet.size() == _data.get_children().size()) {//已全选
                _checkedSet.clear();
                notifyDataSetChanged();
                return false;
            } else {//未全选
                for (int _temp_i = 0; _temp_i < _data.get_children().size(); _temp_i++)
                    _checkedSet.add(_temp_i);
                notifyDataSetChanged();
                return true;
            }
        } else {//普通位置序号
            if (_checkedSet.contains(_position)) {
                _checkedSet.remove(_position);
            } else {
                _checkedSet.add(_position);
            }
            notifyItemChanged(_position);
            return _checkedSet.size() == _data.get_children().size();
        }
    }

    /**
     * 删除所选文件
     */
    public void delete() {
        ArrayList<SDFile> _deleteList = new ArrayList<>(_checkedSet.size());
        for (int _position : _checkedSet) {
            _deleteList.add(_data.get_children().get(_position));
        }
        for (SDFile _file : _deleteList) {
            _file.delete(false);
        }
        notifyDataSetChanged();
    }

    /**
     * 将所选文件添加到保存列表中
     */
    public void save() {
        ArrayList<String> _saveList = new ArrayList<>(_checkedSet.size());
        for (int _position : _checkedSet) {
            _saveList.add(_data.get_children().get(_position).get_path());
        }
        Global.add_saveList(_saveList);
    }

    /**
     * 将所选文件添加到清理列表中
     */
    public void clean() {
        ArrayList<String> _cleanList = new ArrayList<>(_checkedSet.size());
        for (int _position : _checkedSet) {
            _cleanList.add(_data.get_children().get(_position).get_path());
        }
        Global.add_cleanList(_cleanList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View _view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_file, parent, false);
        return new ViewHolder(_view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SDFile _temp_data = _data.get_children().get(position);
        //设置文件大小比例条
        float _percent = (float) NumberUtil.div(_temp_data.get_sizePercent(), 100, 2);
        PercentRelativeLayout.LayoutParams _params = new PercentRelativeLayout.LayoutParams(0, 0);
        _params.getPercentLayoutInfo().widthPercent = _percent;
        holder._view_percent.setLayoutParams(_params);
        int _color = (int) _evaluator.evaluate(_percent, COLOR_START, COLOR_END);
        holder._view_percent.setBackgroundColor(_color);
        //设置复选框状态
        if (_multiSelect) {
            if (holder._checkbox.getVisibility() == CheckBox.GONE)
                holder._checkbox.setVisibility(CheckBox.VISIBLE);
            if (_checkedSet.contains(position))
                holder._checkbox.setChecked(true);
            else
                holder._checkbox.setChecked(false);
        } else {
            if (holder._checkbox.getVisibility() == CheckBox.VISIBLE)
                holder._checkbox.setVisibility(CheckBox.GONE);
        }

        //设置类型图标
        if (_temp_data.isDirectory())
            holder._image_type.setImageResource(R.drawable.icon_directory);
        else
            holder._image_type.setImageResource(R.drawable.icon_file);
        //设置基本信息
        holder._text_name.setText(_temp_data.get_name());
        holder._text_size.setText(Formatter.formatFileSize(Global.get_applicationContext(), _temp_data.get_size()));
        holder._view_click.setTag(new String[]{ACTION_CLICK_ITEM, String.valueOf(position)});
    }

    @Override
    public int getItemCount() {
        return _data.get_children().size();
    }

    public boolean is_multiSelect() {
        return _multiSelect;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View _view_percent;
        public CheckBox _checkbox;
        public ImageView _image_type;
        public TextView _text_name;
        public TextView _text_size;
        public View _view_click;

        public ViewHolder(View itemView) {
            super(itemView);
            _view_percent = itemView.findViewById(R.id.view_percent);
            _checkbox = (CheckBox) itemView.findViewById(R.id.checkbox_item);
            _image_type = (ImageView) itemView.findViewById(R.id.imageview_type);
            _text_name = (TextView) itemView.findViewById(R.id.textview_name);
            _text_size = (TextView) itemView.findViewById(R.id.textview_size);
            _view_click = itemView.findViewById(R.id.view_click);
            _view_click.setOnClickListener(_listener_click);
            _view_click.setOnLongClickListener(_listener_long);
        }
    }
}
