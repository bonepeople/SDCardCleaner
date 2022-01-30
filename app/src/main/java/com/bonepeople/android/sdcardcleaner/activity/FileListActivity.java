package com.bonepeople.android.sdcardcleaner.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.bonepeople.android.sdcardcleaner.Global;
import com.bonepeople.android.sdcardcleaner.R;
import com.bonepeople.android.sdcardcleaner.adapter.FileListAdapter;
import com.bonepeople.android.sdcardcleaner.basic.BaseAppCompatActivity;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.thread.DeleteFileThread;
import com.bonepeople.android.sdcardcleaner.service.FileManager;
import com.bonepeople.android.sdcardcleaner.thread.UpdateRubbishThread;
import com.bonepeople.android.sdcardcleaner.widget.TitleBar;

import java.util.ArrayList;
import java.util.Stack;

/**
 * 文件列表界面
 *
 * @author bonepeople
 */
public class FileListActivity extends BaseAppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_CLEAN = "clean";
    private static final String ACTION_HOLD = "hold";
    private static final String ACTION_CHECK = "checkAll";
    private static final String ACTION_CLOSE = "close";
    private EditText editText_path;
    private LinearLayoutManager layoutManager;
    private LinearLayout buttonBar;
    private CheckBox checkBox_all;
    private ProgressDialog progressDialog;
    private LocalBroadcastManager broadcastManager;
    private FileListAdapter adapter;
    private String basic_path;
    private Stack<SDFile> files = new Stack<>();
    private Stack<Integer> positions = new Stack<>();
    private Stack<Integer> offsets = new Stack<>();
    private ArrayList<Integer> notifyItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_list);

        TitleBar titleBar = findViewById(R.id.titleBar);
        editText_path = findViewById(R.id.edittext_path);
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        buttonBar = findViewById(R.id.linearlayout_buttonbar);
        View view_delete = findViewById(R.id.textView_delete);
        View view_clean = findViewById(R.id.textView_clean);
        View view_hold = findViewById(R.id.textView_hold);
        View view_close = findViewById(R.id.imageview_close);
        checkBox_all = findViewById(R.id.checkbox_all);

        if (Global.getRootFile() == null) {
            FileManager.reset();
            finish();
            return;
        }

        titleBar.setTitle(R.string.caption_text_mine);
        basic_path = Global.getRootFile().getPath();
        editText_path.setText(Global.getRootFile().getPath().replace(basic_path, getString(R.string.str_path_rootFile)));
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new FileListAdapter(this, this);
        adapter.setData(Global.getRootFile());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        view_delete.setTag(new String[]{ACTION_DELETE});
        view_clean.setTag(new String[]{ACTION_CLEAN});
        view_hold.setTag(new String[]{ACTION_HOLD});
        checkBox_all.setTag(new String[]{ACTION_CHECK});
        view_close.setTag(new String[]{ACTION_CLOSE});
        view_delete.setOnClickListener(this);
        view_clean.setOnClickListener(this);
        view_hold.setOnClickListener(this);
        checkBox_all.setOnClickListener(this);
        view_close.setOnClickListener(this);
    }

    private void setMultiSelect(boolean selecting) {
        adapter.setMultiSelect(selecting);
        //经测试，只刷新部分条目会出现bug @ recyclerview-v7:25.3.1'
        adapter.notifyItemRangeChanged(0, adapter.getItemCount(), FileListAdapter.PART_CHECKBOX);
        if (selecting) {
            buttonBar.setVisibility(LinearLayout.VISIBLE);
        } else {
            buttonBar.setVisibility(LinearLayout.GONE);
            checkBox_all.setChecked(false);
        }
    }

    /**
     * 删除所选文件
     */
    private void deleteFiles() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_list_file_delete_title));
        builder.setMessage(getString(R.string.dialog_list_file_delete_message));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.caption_button_positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SparseArray<SDFile> deleteFiles = new SparseArray<>(adapter.getCheckList().size());
                for (int position : adapter.getCheckList()) {
                    deleteFiles.put(position, adapter.getData().getChildren().get(position));
                }
                showProgress(ACTION_DELETE, deleteFiles.size());
                FileManager.startDelete(deleteFiles);
            }
        });
        builder.setNegativeButton(R.string.caption_button_negative, null);
        builder.create().show();
    }

    /**
     * 将所选文件添加到清理列表中
     */
    private void cleanFiles() {
        ArrayList<String> cleanPathList = new ArrayList<>(adapter.getCheckList().size());
        SparseArray<SDFile> cleanFiles = new SparseArray<>(adapter.getCheckList().size());
        for (int position : adapter.getCheckList()) {
            cleanPathList.add(adapter.getData().getChildren().get(position).getPath());
            cleanFiles.put(position, adapter.getData().getChildren().get(position));
        }
        Global.add_cleanList(cleanPathList);
        showProgress(ACTION_CLEAN, cleanFiles.size());
        new UpdateRubbishThread(cleanFiles).start();
    }

    /**
     * 将所选文件添加到保存列表中
     */
    private void saveFiles() {
        ArrayList<String> savePathList = new ArrayList<>(adapter.getCheckList().size());
        SparseArray<SDFile> saveFiles = new SparseArray<>(adapter.getCheckList().size());
        for (int position : adapter.getCheckList()) {
            savePathList.add(adapter.getData().getChildren().get(position).getPath());
            saveFiles.put(position, adapter.getData().getChildren().get(position));
        }
        Global.add_saveList(savePathList);
        showProgress(ACTION_HOLD, saveFiles.size());
        new UpdateRubbishThread(saveFiles).start();
    }

    private void showProgress(String action, int count) {
        progressDialog = new ProgressDialog(this, R.style.DialogTheme);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        switch (action) {
            case ACTION_DELETE:
                progressDialog.setMessage(getString(R.string.dialog_list_file_deleting));
                progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, getString(R.string.caption_button_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FileManager.stopDelete();
                    }
                });
                break;
            case ACTION_CLEAN:
                progressDialog.setMessage(getString(R.string.dialog_list_file_cleaning));
                break;
            case ACTION_HOLD:
                progressDialog.setMessage(getString(R.string.dialog_list_file_saving));
                break;
        }
        progressDialog.setMax(count);
        progressDialog.show();
        notifyItems.clear();

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null)
                    return;
                int index;
                switch (action) {
                    case DeleteFileThread.ACTION_DELETE:
                        index = intent.getIntExtra("index", -1);
                        if (index != -1) {
                            notifyItems.add(index - progressDialog.getProgress());
                            progressDialog.incrementProgressBy(1);
                        }
                        break;
                    case DeleteFileThread.ACTION_FINISH:
                        progressDialog.dismiss();
                        progressDialog = null;
                        broadcastManager.unregisterReceiver(this);
                        for (int index_change : notifyItems) {
                            adapter.notifyItemRemoved(index_change);
                        }
                        adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                        break;
                    case UpdateRubbishThread.ACTION_UPDATE:
                        index = intent.getIntExtra("index", -1);
                        if (index != -1) {
                            notifyItems.add(index);
                            progressDialog.incrementProgressBy(1);
                        }
                        break;
                    case UpdateRubbishThread.ACTION_FINISH:
                        progressDialog.dismiss();
                        progressDialog = null;
                        broadcastManager.unregisterReceiver(this);
                        for (int index_change : notifyItems) {
                            adapter.notifyItemChanged(index_change);
                        }
                        break;
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(DeleteFileThread.ACTION_DELETE);
        filter.addAction(DeleteFileThread.ACTION_FINISH);
        filter.addAction(UpdateRubbishThread.ACTION_UPDATE);
        filter.addAction(UpdateRubbishThread.ACTION_FINISH);
        broadcastManager.registerReceiver(receiver, filter);
    }

    @Override
    public void onBackPressed() {
        if (adapter.isMultiSelect()) {
            setMultiSelect(false);
        } else if (files.size() > 0) {
            editText_path.setText(files.peek().getPath().replace(basic_path, getString(R.string.str_path_rootFile)));
            editText_path.setSelection(editText_path.getText().length());
            adapter.setData(files.pop());
            adapter.notifyDataSetChanged();
            layoutManager.scrollToPositionWithOffset(positions.pop(), offsets.pop());
        } else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        String[] tags = (String[]) v.getTag();
        switch (tags[0]) {
            case ACTION_DELETE:
                setMultiSelect(false);
                if (adapter.getCheckList().size() > 0)
                    deleteFiles();
                break;
            case ACTION_CLEAN:
                setMultiSelect(false);
                if (adapter.getCheckList().size() > 0)
                    cleanFiles();
                break;
            case ACTION_HOLD:
                setMultiSelect(false);
                if (adapter.getCheckList().size() > 0)
                    saveFiles();
                break;
            case ACTION_CHECK:
                adapter.set_checkList(-1);
                //经测试，只刷新部分条目会出现bug @ recyclerview-v7:25.3.1'
                adapter.notifyItemRangeChanged(0, adapter.getItemCount(), FileListAdapter.PART_CHECKBOX);
                break;
            case ACTION_CLOSE:
                setMultiSelect(false);
                break;
            case FileListAdapter.ACTION_CLICK_ITEM:
                int position = Integer.parseInt(tags[1]);
                if (adapter.isMultiSelect()) {//处于多选状态
                    checkBox_all.setChecked(adapter.set_checkList(position));
                    adapter.notifyItemChanged(position, FileListAdapter.PART_CHECKBOX);
                } else {//处于浏览状态
                    SDFile clickFile = adapter.getData().getChildren().get(position);
                    if (clickFile.isDirectory()) {
                        int firstItemPosition = layoutManager.findFirstVisibleItemPosition();
                        positions.push(firstItemPosition);
                        View firstVisibleView = layoutManager.getChildAt(0);
                        if (firstVisibleView != null) {
                            int offset = firstVisibleView.getTop();
                            offsets.push(offset);
                        } else
                            offsets.push(0);
                        editText_path.setText(clickFile.getPath().replace(basic_path, getString(R.string.str_path_rootFile)));
                        editText_path.setSelection(editText_path.getText().length());
                        files.push(adapter.getData());
                        adapter.setData(clickFile);
                        adapter.notifyDataSetChanged();
                        layoutManager.scrollToPositionWithOffset(0, 0);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        String[] tags = (String[]) v.getTag();
        int position = Integer.parseInt(tags[1]);
        if (adapter.isMultiSelect()) {//处于多选状态
            checkBox_all.setChecked(adapter.set_checkList(position));
            adapter.notifyItemChanged(position, FileListAdapter.PART_CHECKBOX);
        } else {//处于浏览状态
            setMultiSelect(true);
            checkBox_all.setChecked(adapter.set_checkList(position));
            adapter.notifyItemChanged(position, FileListAdapter.PART_CHECKBOX);
        }
        return true;
    }
}
