package com.blg.rtu.frmFunction.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

public class DialogMaker {
    private static EasyProgressDialog progressDialog ;
    public static void dismissProgressDialog() {
        if (progressDialog == null) {
            return;
        }
        if (progressDialog.isShowing()) {}
        try {
            progressDialog.dismiss();
            progressDialog = null ;
        }catch (Exception e) {}
    }

    public static boolean isShowing() {
        return (progressDialog != null) && (progressDialog.isShowing()) ;
    }

    public static void setMessage(String message) {
        if ((progressDialog != null) && (progressDialog.isShowing()) && !TextUtils.isEmpty(message)) {
            progressDialog.setMessage(message);
        }
    }

    public static EasyProgressDialog showProgressDialog(Context context, String paramString) {
        return showProgressDialog(context, null, paramString, true, null) ;
    }

    public static EasyProgressDialog showProgressDialog(Context paramContext, StringBuilder paramString1, String paramString2, boolean paramBoolean, DialogInterface.OnCancelListener paramOnCancelListener) {
        if (progressDialog == null)
        {
            progressDialog = new EasyProgressDialog(paramContext, paramString2);
        }
        else if (progressDialog.getContext() != paramContext)
        {
            paramString1 = new StringBuilder();
            paramString1.append("there is a leaked window here,orign context: ");
            paramString1.append(progressDialog.getContext());
            paramString1.append(" now: ");
            paramString1.append(paramContext);
            Log.e("dialog", paramString1.toString());
            dismissProgressDialog();
            progressDialog = new EasyProgressDialog(paramContext, paramString2);
        }
        progressDialog.setCancelable(paramBoolean);
        progressDialog.setOnCancelListener(paramOnCancelListener);
        progressDialog.show();
        return progressDialog;
    }

    public static EasyProgressDialog showProgressDialog(Context paramContext, String paramString, boolean paramBoolean)
    {
        return showProgressDialog(paramContext, null, paramString, paramBoolean, null);
    }

    public static void updateLoadingMessage(String paramString)
    {
        if ((progressDialog != null) && (progressDialog.isShowing()) && (!TextUtils.isEmpty(paramString))) {
            progressDialog.updateLoadingMessage(paramString);
        }
    }
}
