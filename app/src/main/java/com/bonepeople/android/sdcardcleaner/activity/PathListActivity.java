package com.bonepeople.android.sdcardcleaner.activity;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.adapter.PathListAdapter;
import com.bonepeople.android.sdcardcleaner.basic.BaseAppCompatActivity;
import com.bonepeople.android.sdcardcleaner.service.FileManager;

import java.util.ArrayList;

/**
 * 保留列表和待清理列表的展示页面
 * <p>
 * <b>intent:</b>"mode"-当前页面的类型(MODE_SAVE/MODE_CLEAN)
 */
public class PathListActivity extends BaseAppCompatActivity implements View.OnClickListener {
    public static final int MODE_SAVE = 0;//保留列表
    public static final int MODE_CLEAN = 1;//待清理列表
    private int _mode;
    private LinearLayoutManager _layoutManager;
    private RecyclerView _list;
    private PathListAdapter _adapter;
    private ArrayList<String> _data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _mode = getIntent().getIntExtra("mode", MODE_SAVE);
        setContentView(R.layout.activity_path_list);
        String _basic_path = Environment.getExternalStorageDirectory().getPath();
        if (_mode == MODE_SAVE) {
            setTitle(getString(R.string.caption_text_white));
            _data = new ArrayList<>(Global.get_saveList().size());
            for (int _temp_i = 0; _temp_i < Global.get_saveList().size(); _temp_i++) {
                _data.add(Global.get_saveList().get(_temp_i).replace(_basic_path, getString(R.string.str_path_rootFile)));
            }
        } else {
            setTitle(getString(R.string.caption_text_black));
            _data = new ArrayList<>(Global.get_cleanList().size());
            for (int _temp_i = 0; _temp_i < Global.get_cleanList().size(); _temp_i++) {
                _data.add(Global.get_cleanList().get(_temp_i).replace(_basic_path, getString(R.string.str_path_rootFile)));
            }
        }

        _list = (RecyclerView) findViewById(R.id.recyclerview);

        _layoutManager = new LinearLayoutManager(this);
        _layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _list.setLayoutManager(_layoutManager);
        _adapter = new PathListAdapter(this);
        _adapter.set_data(_data);
        _list.setAdapter(_adapter);
        _list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        if (FileManager.get_state() == FileManager.STATE_SCAN_EXECUTING)
            Toast.makeText(this, R.string.toast_list_path_lock, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, R.string.toast_list_path_remove, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        String[] _tags = (String[]) v.getTag();
        switch (_tags[0]) {
            case PathListAdapter.ACTION_CLICK_ITEM:
                if (FileManager.get_state() != FileManager.STATE_SCAN_EXECUTING)
                    removeItem(Integer.parseInt(_tags[1]));
                break;
        }
    }

    private void removeItem(final int _index) {
        AlertDialog.Builder _builder = new AlertDialog.Builder(this);
        String _path = _data.get(_index);
        _builder.setMessage(getString(R.string.dialog_list_path_remove, _path));
        _builder.setPositiveButton(R.string.caption_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (_mode == MODE_SAVE) {
                    Global.remove_saveList(_index);
                } else {
                    Global.remove_cleanList(_index);
                }
                _data.remove(_index);
                _adapter.notifyItemRemoved(_index);
                _adapter.notifyItemRangeChanged(_index, _data.size() - _index);
            }
        });
        _builder.setNegativeButton(R.string.caption_button_negative, null);
        _builder.create().show();
    }
}
