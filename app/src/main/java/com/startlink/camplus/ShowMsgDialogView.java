package com.startlink.camplus;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ShowMsgDialogView extends Dialog implements View.OnClickListener {


    private TextView contentTv;
    private Button btn;
    private Button cancelBtn;

    private OnDialogListener onDialogListener;

    public void setOnDialogListener(OnDialogListener onDialogListener) {
        this.onDialogListener = onDialogListener;
    }

    public ShowMsgDialogView(@NonNull Context context) {
        super(context);
    }

    public ShowMsgDialogView(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_msg_dialog_layout);


        contentTv = findViewById(R.id.dialogContentTv);

        btn = findViewById(R.id.dialogBtn);
        cancelBtn = findViewById(R.id.dialogCancelBtn);
        btn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.dialogBtn){
            if(onDialogListener != null)
                onDialogListener.onDismissView();
        }

        if(view.getId() == R.id.dialogCancelBtn){
            dismiss();
        }
    }


    public void setContentTvTxt(String txt){
        if(contentTv != null)
            contentTv.setText(txt);
    }


    public void setShowCancelBtn(boolean isShow){
        cancelBtn.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    public interface OnDialogListener{
        void onDismissView();
        void onCancelView();
    }
}
