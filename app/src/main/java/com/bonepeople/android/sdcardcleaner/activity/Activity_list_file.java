package com.bonepeople.android.sdcardcleaner.activity;

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
        View _button_delete = findViewById(R.id.textView_delete);
        View _button_clean = findViewById(R.id.textView_clean);
        View _button_hold = findViewById(R.id.textView_hold);
        View _button_close = findViewById(R.id.imageview_close);
        _checkbox_all = (CheckBox) findViewById(R.id.checkbox_all);

        _text_path.setText("SD卡\\");
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

    private void exitMultiSelect() {
        _buttonbar.setVisibility(LinearLayout.GONE);
        _adapter.set_multiSelect(false);
        _checkbox_all.setChecked(false);
    }

    @Override
    public void onBackPressed() {
        if (_adapter.is_multiSelect()) {
            exitMultiSelect();
        } else if (_files.size() > 0) {
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
        String[] _tags = (String[]) v.getTag();
        switch (_tags[0]) {
            case ACTION_DELETE:
                exitMultiSelect();
                _adapter.delete();
                break;
            case ACTION_CLEAN:
                exitMultiSelect();
                _adapter.clean();
                break;
            case ACTION_HOLD:
                exitMultiSelect();
                _adapter.save();
                break;
            case ACTION_CHECK:
                _adapter.set_checkedSet(-1);
                break;
            case ACTION_CLOSE:
                exitMultiSelect();
                break;
            case Adapter_list_file.ACTION_CLICK_ITEM:
                int _position = Integer.parseInt(_tags[1]);
                if (_adapter.is_multiSelect()) {//处于多选状态
                    _checkbox_all.setChecked(_adapter.set_checkedSet(_position));
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
                        _paths.push(_text_path.getText().toString());
                        _text_path.append(_clickFile.get_name() + "\\");
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
            _checkbox_all.setChecked(_adapter.set_checkedSet(_position));
        } else {//处于浏览状态
            _adapter.set_multiSelect(true);
            _checkbox_all.setChecked(_adapter.set_checkedSet(_position));
            _buttonbar.setVisibility(LinearLayout.VISIBLE);
        }
        return true;
    }
}
