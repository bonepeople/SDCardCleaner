package com.bonepeople.android.sdcardcleaner.activity;

import android.content.DialogInterface;
import android.os.Environment;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.adapter.PathListAdapter;
import com.bonepeople.android.sdcardcleaner.basic.BaseAppCompatActivity;
import com.bonepeople.android.sdcardcleaner.service.FileManager;
import com.bonepeople.android.sdcardcleaner.widget.TitleBar;

import java.util.ArrayList;

/**
 * 保留列表和待清理列表的展示页面
 * <p>
 * <b>intent:</b>"mode"-当前页面的类型(MODE_SAVE/MODE_CLEAN)
 *
 * @author bonepeople
 */
public class PathListActivity extends BaseAppCompatActivity implements View.OnClickListener {
    public static final int MODE_SAVE = 0;//保留列表
    public static final int MODE_CLEAN = 1;//待清理列表
    private int mode;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private PathListAdapter adapter;
    private ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_list);

        TitleBar titleBar = findViewById(R.id.titleBar);
        recyclerView = findViewById(R.id.recyclerview);

        String basic_path = Environment.getExternalStorageDirectory().getPath();
        mode = getIntent().getIntExtra("mode", MODE_SAVE);
        if (mode == MODE_SAVE) {
            titleBar.setTitle(R.string.caption_text_white);
            data = new ArrayList<>(Global.getSaveList().size());
            for (int temp_i = 0; temp_i < Global.getSaveList().size(); temp_i++) {
                data.add(Global.getSaveList().get(temp_i).replace(basic_path, getString(R.string.str_path_rootFile)));
            }
        } else {
            titleBar.setTitle(R.string.caption_text_black);
            data = new ArrayList<>(Global.getCleanList().size());
            for (int temp_i = 0; temp_i < Global.getCleanList().size(); temp_i++) {
                data.add(Global.getCleanList().get(temp_i).replace(basic_path, getString(R.string.str_path_rootFile)));
            }
        }
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PathListAdapter(this);
        adapter.setData(data);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        if (FileManager.getState() == FileManager.STATE_SCAN_EXECUTING)
//            Toast.makeText(this, R.string.toast_list_path_lock, Toast.LENGTH_LONG).show();
//        else
//            Toast.makeText(this, R.string.toast_list_path_remove, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        String[] tags = (String[]) v.getTag();
        switch (tags[0]) {
            case PathListAdapter.ACTION_CLICK_ITEM:
                if (FileManager.getState() != FileManager.STATE_SCAN_EXECUTING)
                    removeItem(Integer.parseInt(tags[1]));
                break;
        }
    }

    private void removeItem(final int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String path = data.get(index);
        builder.setMessage(getString(R.string.dialog_list_path_remove, path));
        builder.setPositiveButton(R.string.caption_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mode == MODE_SAVE) {
                    Global.remove_saveList(index);
                } else {
                    Global.remove_cleanList(index);
                }
                data.remove(index);
                adapter.notifyItemRemoved(index);
                adapter.notifyItemRangeChanged(index, data.size() - index);
            }
        });
        builder.setNegativeButton(R.string.caption_button_negative, null);
        builder.create().show();
    }
}
