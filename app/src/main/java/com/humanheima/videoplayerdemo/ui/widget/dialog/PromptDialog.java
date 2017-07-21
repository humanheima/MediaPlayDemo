package com.humanheima.videoplayerdemo.ui.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.humanheima.videoplayerdemo.R;


/**
 * Created by chenchao on 16/10/19.
 * cc@cchao.org
 * 提示框
 */
public class PromptDialog extends Dialog {

    private TextView textTitle;
    private TextView textContent;
    private Button btnCancel;
    private Button btnOk;

    private String title;
    private String content;
    private String okStr;

    public PromptDialog(Context context, String title, String content, String okStr) {
        this(context, title, content);
        this.okStr = okStr;
    }

    public PromptDialog(Context context, String title, String content) {
        super(context, R.style.MyDialog);
        this.title = title;
        this.content = content;
    }

    public static PromptDialog show(Context context, String title, String content, String okStr) {
        PromptDialog promptDialog = new PromptDialog(context, title, content, okStr);
        promptDialog.setCancelable(false);
        promptDialog.show();
        return promptDialog;
    }

    public static PromptDialog show(Context context, String title, String content) {
        PromptDialog promptDialog = new PromptDialog(context, title, content);
        promptDialog.setCancelable(false);
        promptDialog.show();
        return promptDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_prompt);
        textTitle = (TextView) findViewById(R.id.dialog_prompt_title);
        textContent = (TextView) findViewById(R.id.dialog_prompt_content);
        btnCancel = (Button) findViewById(R.id.dialog_prompt_cancle);
        btnOk = (Button) findViewById(R.id.dialog_prompt_ok);


        if (!TextUtils.isEmpty(title)) {
            textTitle.setText(title);
        }
        if (!TextUtils.isEmpty(content)) {
            textContent.setText(content);
        }
        if (!TextUtils.isEmpty(okStr)) {
            btnOk.setText(okStr);
        }
    }

    public void setTextContent(String content) {
        if (!TextUtils.isEmpty(content)) {
            textContent.setText(content);
        }
    }

    public void setCancelListener(View.OnClickListener onClickListener) {
        btnCancel.setOnClickListener(onClickListener);
    }

    public void setOkListener(View.OnClickListener onClickListener) {
        btnOk.setOnClickListener(onClickListener);
    }
}
