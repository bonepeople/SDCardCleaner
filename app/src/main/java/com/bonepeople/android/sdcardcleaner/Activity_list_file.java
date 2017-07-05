package com.bonepeople.android.sdcardcleaner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bonepeople.android.sdcardcleaner.adapter.Adapter_list_file;
import com.bonepeople.android.sdcardcleaner.models.SDFile;
import com.bonepeople.android.sdcardcleaner.utils.FileScanUtil;

import java.util.Stack;

public class Activity_list_file extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private EditText _text_path;
    private LinearLayoutManager _layoutManager;
    private RecyclerView _list;
    private LinearLayout _buttonbar;
    private View _button_delete, _button_clean, _button_hold, _button_close;
    private CheckBox _checkbox_all;
    private Adapter_list_file _adapter;
    private Stack<SDFile> _files = new Stack<>();
    private Stack<String> _paths = new Stack<>();
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
        _button_delete = findViewById(R.id.textView_delete);
        _button_clean = findViewById(R.id.textView_clean);
        _button_hold = findViewById(R.id.textView_hold);
        _button_close = findViewById(R.id.imageview_close);
        _checkbox_all = (CheckBox) findViewById(R.id.checkbox_all);

        _text_path.setText("SD卡\\");
        _layoutManager = new LinearLayoutManager(this);
        _layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        _list.setLayoutManager(_layoutManager);
        _adapter = new Adapter_list_file(this, this, this);
        _adapter.set_data(FileScanUtil.get_rootFile());
        _list.setAdapter(_adapter);
        _list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        _button_delete.setOnClickListener(this);
        _button_clean.setOnClickListener(this);
        _button_hold.setOnClickListener(this);
        _button_close.setOnClickListener(this);
        _checkbox_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.out.println("isChecked = " + isChecked);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (_files.size() > 0) {
            _text_path.setText(_paths.pop());
            _text_path.setSelection(_text_path.getText().length());
            _adapter.set_data(_files.pop());
            _adapter.notifyDataSetChanged();
            _layoutManager.scrollToPositionWithOffset(_positions.pop(), _offsets.pop());
        } else
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_delete:
                _buttonbar.setVisibility(LinearLayout.GONE);
                break;
            case R.id.textView_clean:
                _buttonbar.setVisibility(LinearLayout.GONE);
                break;
            case R.id.textView_hold:
                _buttonbar.setVisibility(LinearLayout.GONE);
                break;
            case R.id.checkbox_all:

                break;
            case R.id.imageview_close:
                _buttonbar.setVisibility(LinearLayout.GONE);
                break;
            default:
                int _position = (int) v.getTag();
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
                    _paths.push(_text_path.getText().toString());
                    _text_path.append(_clickFile.get_name() + "\\");
                    _text_path.setSelection(_text_path.getText().length());
                    _files.push(_adapter.get_data());
                    _adapter.set_data(_clickFile);
                    _adapter.notifyDataSetChanged();
                    _layoutManager.scrollToPositionWithOffset(0, 0);
                }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        int _position = (int) v.getTag();
        System.out.println("long click - " + _position);
        _buttonbar.setVisibility(LinearLayout.VISIBLE);
        return true;
    }
}
