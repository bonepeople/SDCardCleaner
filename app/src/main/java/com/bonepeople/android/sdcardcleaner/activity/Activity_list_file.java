package com.bonepeople.android.sdcardcleaner.activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.adapter.Adapter_list_file;
import com.bonepeople.android.sdcardcleaner.models.SDFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

public class Activity_list_file extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_CLEAN = "clean";
    public static final String ACTION_HOLD = "hold";
    public static final String ACTION_CHECK = "checkAll";
    public static final String ACTION_CLOSE = "close";
    private EditText _text_path;
    private LinearLayoutManager _layoutManager;
    private RecyclerView _list;
    private LinearLayout _buttonbar;
    private CheckBox _checkbox_all;
    private Adapter_list_file _adapter;
    private String _basic_path = Global.get_rootFile().get_path();
    private Stack<SDFile> _files = new Stack<>();
    private Stack<Integer> _positions = new Stack<>();
    private Stack<Integer> _offsets = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_file);
        setTitle("文件列表");

        _text_path = (EditText) findViewById(R.id.edittext_path);
        _list = (RecyclerView) findViewById(R.id.recyclerview);
        _buttonbar = (LinearLayout) findViewById(R.id.linearlayout_buttonbar);
        View _button_delete = findViewById(R.id.textView_delete);
        View _button_clean = findViewById(R.id.textView_clean);
        View _button_hold = findViewById(R.id.textView_hold);
        View _button_close = findViewById(R.id.imageview_close);
        _checkbox_all = (CheckBox) findViewById(R.id.checkbox_all);

        _text_path.setText(Global.get_rootFile().get_path().replace(_basic_path, "SD卡"));
        _layoutManager = new LinearLayoutManager(this);
        _layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _list.setLayoutManager(_layoutManager);
        _adapter = new Adapter_list_file(this, this);
        _adapter.set_data(Global.get_rootFile());
        _list.setAdapter(_adapter);
        _list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        _button_delete.setTag(new String[]{ACTION_DELETE});
        _button_clean.setTag(new String[]{ACTION_CLEAN});
        _button_hold.setTag(new String[]{ACTION_HOLD});
        _checkbox_all.setTag(new String[]{ACTION_CHECK});
        _button_close.setTag(new String[]{ACTION_CLOSE});
        _button_delete.setOnClickListener(this);
        _button_clean.setOnClickListener(this);
        _button_hold.setOnClickListener(this);
        _checkbox_all.setOnClickListener(this);
        _button_close.setOnClickListener(this);
    }

    private void setMultiSelect(boolean _selecting) {
        _adapter.set_multiSelect(_selecting);
        //经测试，只刷新部分条目会出现bug
        _adapter.notifyItemRangeChanged(0, _adapter.getItemCount(), Adapter_list_file.PART_CHECKBOX);
        if (_selecting) {
            _buttonbar.setVisibility(LinearLayout.VISIBLE);
        } else {
            _buttonbar.setVisibility(LinearLayout.GONE);
            _checkbox_all.setChecked(false);
        }
    }

    /**
     * 删除所选文件
     */
    private void deleteFiles() {
        AlertDialog.Builder _builder = new AlertDialog.Builder(this);
        _builder.setTitle("确认删除？");
        _builder.setMessage("该操作会删除当前指定文件夹下的所有文件，无论它是否存在于白名单中");
        _builder.setPositiveButton(R.string.positiveButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<Integer> _checkList = _adapter.get_checkList();
                Collections.sort(_checkList);
                ArrayList<SDFile> _deleteList = new ArrayList<>(_checkList.size());
                for (int _position : _checkList) {
                    _deleteList.add(_adapter.get_data().get_children().get(_position));
                }
                for (SDFile _file : _deleteList) {
                    int _index = _adapter.get_data().get_children().indexOf(_file);
                    _file.delete(false);
                    _adapter.notifyItemRemoved(_index);
                    _adapter.notifyItemRangeChanged(_index, _adapter.get_data().get_children().size() - _index);
                }
            }
        });
        _builder.setNegativeButton(R.string.negativeButton, null);
        _builder.create().show();
    }

    /**
     * 将所选文件添加到清理列表中
     */
    private void cleanFiles() {
        ArrayList<Integer> _checkList = _adapter.get_checkList();
        ArrayList<String> _cleanList = new ArrayList<>(_checkList.size());
        for (int _position : _checkList) {
            _cleanList.add(_adapter.get_data().get_children().get(_position).get_path());
        }
        Global.add_cleanList(_cleanList);
        _adapter.get_data().updateRubbish();
        _adapter.notifyDataSetChanged();
    }

    /**
     * 将所选文件添加到保存列表中
     */
    private void saveFiles() {
        ArrayList<Integer> _checkList = _adapter.get_checkList();
        ArrayList<String> _saveList = new ArrayList<>(_checkList.size());
        for (int _position : _checkList) {
            _saveList.add(_adapter.get_data().get_children().get(_position).get_path());
        }
        Global.add_saveList(_saveList);
        _adapter.get_data().updateRubbish();
        _adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (_adapter.is_multiSelect()) {
            setMultiSelect(false);
        } else if (_files.size() > 0) {
            _text_path.setText(_files.peek().get_path().replace(_basic_path, "SD卡"));
            _text_path.setSelection(_text_path.getText().length());
            _adapter.set_data(_files.pop());
            _adapter.notifyDataSetChanged();
            _layoutManager.scrollToPositionWithOffset(_positions.pop(), _offsets.pop());
        } else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        String[] _tags = (String[]) v.getTag();
        switch (_tags[0]) {
            case ACTION_DELETE:
                setMultiSelect(false);
                deleteFiles();
                break;
            case ACTION_CLEAN:
                setMultiSelect(false);
                cleanFiles();
                break;
            case ACTION_HOLD:
                setMultiSelect(false);
                saveFiles();
                break;
            case ACTION_CHECK:
                _adapter.set_checkList(-1);
                //经测试，只刷新部分条目会出现bug
                _adapter.notifyItemRangeChanged(0, _adapter.getItemCount(), Adapter_list_file.PART_CHECKBOX);
                break;
            case ACTION_CLOSE:
                setMultiSelect(false);
                break;
            case Adapter_list_file.ACTION_CLICK_ITEM:
                int _position = Integer.parseInt(_tags[1]);
                if (_adapter.is_multiSelect()) {//处于多选状态
                    _checkbox_all.setChecked(_adapter.set_checkList(_position));
                    _adapter.notifyItemChanged(_position, Adapter_list_file.PART_CHECKBOX);
                } else {//处于浏览状态
                    SDFile _clickFile = _adapter.get_data().get_children().get(_position);
                    if (_clickFile.isDirectory()) {
                        int _firstItemPosition = _layoutManager.findFirstVisibleItemPosition();
                        _positions.push(_firstItemPosition);
                        View _firstVisibleView = _layoutManager.getChildAt(0);
                        if (_firstVisibleView != null) {
                            int _offset = _firstVisibleView.getTop();
                            _offsets.push(_offset);
                        } else
                            _offsets.push(0);
                        _text_path.setText(_clickFile.get_path().replace(_basic_path, "SD卡"));
                        _text_path.setSelection(_text_path.getText().length());
                        _files.push(_adapter.get_data());
                        _adapter.set_data(_clickFile);
                        _adapter.notifyDataSetChanged();
                        _layoutManager.scrollToPositionWithOffset(0, 0);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        String[] _tags = (String[]) v.getTag();
        int _position = Integer.parseInt(_tags[1]);
        if (_adapter.is_multiSelect()) {//处于多选状态
            _checkbox_all.setChecked(_adapter.set_checkList(_position));
            _adapter.notifyItemChanged(_position, Adapter_list_file.PART_CHECKBOX);
        } else {//处于浏览状态
            setMultiSelect(true);
            _checkbox_all.setChecked(_adapter.set_checkList(_position));
            _adapter.notifyItemChanged(_position, Adapter_list_file.PART_CHECKBOX);
        }
        return true;
    }
}
