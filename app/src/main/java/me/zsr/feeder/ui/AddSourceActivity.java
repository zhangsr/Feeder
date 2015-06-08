package me.zsr.feeder.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import me.zsr.feeder.R;

public class AddSourceActivity extends Activity implements View.OnClickListener {
    private ImageButton mBackButton;
    private EditText mSourceEditText;
    private ImageButton mEmptyTextButton;
    private Button mAddSourceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_source);

        initView();
        setListener();
    }

    private void initView() {
        mBackButton = (ImageButton) findViewById(R.id.back_btn);
        mSourceEditText = (EditText) findViewById(R.id.add_source_edt);
        mEmptyTextButton = (ImageButton) findViewById(R.id.empty_text_btn);
        mAddSourceButton = (Button) findViewById(R.id.add_feed_btn);
    }

    private void setListener() {
        mBackButton.setOnClickListener(this);
        mSourceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    mEmptyTextButton.setVisibility(View.INVISIBLE);
                    mAddSourceButton.setVisibility(View.INVISIBLE);
                } else {
                    mEmptyTextButton.setVisibility(View.VISIBLE);
                    mAddSourceButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEmptyTextButton.setOnClickListener(this);
        mAddSourceButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.empty_text_btn:
                mSourceEditText.setText("");
                break;
            case R.id.add_feed_btn:
                break;
            default:
        }
    }
}
