package com.blg.rtu.frmFunction.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.blg.rtu3.R;

public class EasyProgressDialog extends Dialog {
    private Context mContext;
    private int mLayoutId;
    private String mMessage;
    private TextView message;
    public EasyProgressDialog(Context context) {
        this(context, R.style.easy_dialog_style, R.layout.easy_progress_dialog) ;
    }

    public EasyProgressDialog(Context paramContext, int paramInt1, int paramInt2)
    {
        super(paramContext, paramInt1);
        this.mContext = paramContext;
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = -1;
        params.height = -1;
        getWindow().setAttributes(params);
        this.mLayoutId = paramInt2;
    }

    public EasyProgressDialog(Context paramContext, int paramInt, String paramString)
    {
        this(paramContext, R.style.easy_dialog_style, paramInt);
        setMessage(paramString);
    }

    public EasyProgressDialog(Context paramContext, String paramString)
    {
        this(paramContext, R.style.easy_dialog_style, R.layout.easy_progress_dialog);
        setMessage(paramString);
    }

    private void showMessage()
    {
        if ((this.message != null) && (!TextUtils.isEmpty(this.mMessage)))
        {
            this.message.setVisibility(View.VISIBLE);
            this.message.setText(this.mMessage);
        }
    }

    protected void onCreate(Bundle paramBundle)
    {
        super.onCreate(paramBundle);
        setContentView(this.mLayoutId);
        this.message = ((TextView)findViewById(R.id.easy_progress_dialog_message));
        showMessage();
    }
    public void setMessage(String paramString)
    {
        this.mMessage = paramString;
    }

    public void updateLoadingMessage(String paramString)
    {
        this.mMessage = paramString;
        showMessage();
    }
}
