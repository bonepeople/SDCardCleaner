package com.bonepeople.android.sdcardcleaner.activity;

import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.adapter.Adapter_list_path;
import com.bonepeople.android.sdcardcleaner.thread.Service_fileManager;

import java.util.ArrayList;

/**
 * 保留列表和待清理列表的展示页面
 * <p>
 * <b>intent:</b>"mode"-当前页面的类型(MODE_SAVE/MODE_CLEAN)
 */
public class Activity_list_path extends AppCompatActivity implements View.OnClickListener {
    public static final int MODE_SAVE = 0;//保留列表
    public static final int MODE_CLEAN = 1;//待清理列表
    private int _mode;
    private LinearLayoutManager _layoutManager;
    private RecyclerView _list;
    private Adapter_list_path _adapter;
    private ArrayList<String> _data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _mode = getIntent().getIntExtra("mode", MODE_SAVE);
        setContentView(R.layout.activity_list_path);
        String _basic_path = Environment.getExternalStorageDirectory().getPath();
        if (_mode == MODE_SAVE) {
            setTitle("白名单");
            _data = new ArrayList<>(Global.get_saveList().size());
            for (int _temp_i = 0; _temp_i < Global.get_saveList().size(); _temp_i++) {
                _data.add(Global.get_saveList().get(_temp_i).replace(_basic_path, "SD卡"));
            }
        } else {
            setTitle("黑名单");
            _data = new ArrayList<>(Global.get_cleanList().size());
            for (int _temp_i = 0; _temp_i < Global.get_cleanList().size(); _temp_i++) {
                _data.add(Global.get_cleanList().get(_temp_i).replace(_basic_path, "SD卡"));
            }
        }

        _list = (RecyclerView) findViewById(R.id.recyclerview);

        _layoutManager = new LinearLayoutManager(this);
        _layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _list.setLayoutManager(_layoutManager);
        _adapter = new Adapter_list_path(this);
        _adapter.set_data(_data);
        _list.setAdapter(_adapter);
        _list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        if (Service_fileManager.get_scanState() == Service_fileManager.STATE_SCAN_EXECUTING)
            Toast.makeText(this, "扫描过程中无法修改", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(this, R.string.toast_list_path, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        String[] _tags = (String[]) v.getTag();
        switch (_tags[0]) {
            case Adapter_list_path.ACTION_CLICK_ITEM:
                if (Service_fileManager.get_scanState() == Service_fileManager.STATE_SCAN_FINISH)
                    removeItem(Integer.parseInt(_tags[1]));
                break;
        }
    }

    private void removeItem(final int _index) {
        AlertDialog.Builder _builder = new AlertDialog.Builder(this);
        String _path = _data.get(_index);
        _builder.setMessage(getResources().getString(R.string.dialog_list_path, _path));
        _builder.setPositiveButton(R.string.positiveButton, new DialogInterface.OnClickListener() {
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
        _builder.setNegativeButton(R.string.negativeButton, null);
        _builder.create().show();
    }
}
