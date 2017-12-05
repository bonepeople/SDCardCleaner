package com.bonepeople.android.sdcardcleaner.activity;

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

/**
 * 保留列表和待清理列表的展示页面
 */
public class Activity_list_path extends AppCompatActivity implements View.OnClickListener {
    public static final int MODE_SAVE = 0;//保留列表
    public static final int MODE_CLEAN = 1;//待清理列表
    private int _mode;
    private LinearLayoutManager _layoutManager;
    private RecyclerView _list;
    private Adapter_list_path _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _mode = getIntent().getIntExtra("mode", MODE_SAVE);
        setContentView(R.layout.activity_list_path);
        setTitle(_mode == MODE_SAVE ? "白名单" : "黑名单");

        _list = (RecyclerView) findViewById(R.id.recyclerview);

        _layoutManager = new LinearLayoutManager(this);
        _layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _list.setLayoutManager(_layoutManager);
        _adapter = new Adapter_list_path(this, this);
        _adapter.set_data(_mode == MODE_SAVE ? Global.get_saveList() : Global.get_cleanList());
        _list.setAdapter(_adapter);
        _list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onClick(View v) {
        String[] _tags = (String[]) v.getTag();
        switch (_tags[0]) {
            case Adapter_list_path.ACTION_CLICK_ITEM:
                Toast.makeText(this, _tags[1], Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
