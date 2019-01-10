package com.blg.rtu.frmFunction.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.blg.rtu.frmFunction.util.QMUIStatusBarHelper;
import com.blg.rtu3.R;

public abstract class BaseActivity extends Activity {
    private static Handler handler;
    private boolean destroyed = false;

    @TargetApi(17)
    private boolean isDestroyedCompatible17()
    {
        return super.isDestroyed();
    }
    protected boolean displayHomeAsUpEnabled()
    {
        return true;
    }

    protected <T extends View> T findView(int paramInt)
    {
        return findViewById(paramInt);
    }

    protected final Handler getHandler()
    {
        if (handler == null) {
            handler = new Handler(getMainLooper());
        }
        return handler;
    }

    public abstract void initData();

    public abstract void initView();

    protected boolean isCompatible(int paramInt)
    {
        return Build.VERSION.SDK_INT >= paramInt;
    }

    public boolean isDestroyedCompatible()
    {
        if (Build.VERSION.SDK_INT >= 17) {
            return isDestroyedCompatible17();
        }
        return (this.destroyed) || (super.isFinishing());
    }

    public boolean isTranslucent()
    {
        return true;
    }

    protected void onCreate(@Nullable Bundle paramBundle)
    {
        super.onCreate(paramBundle);
      /*  if ((isTranslucent()) && (QMUIStatusBarHelper.isSupportTranslucent())) {
            QMUIStatusBarHelper.translucent(this, 1073741824);
        }
       setStatusBarLightMode(this, true);*/
    }

    public static void setStatusBarLightMode(Activity paramActivity, boolean paramBoolean)
    {
        if (paramBoolean)
        {
            QMUIStatusBarHelper.setStatusBarLightMode(paramActivity);
            return;
        }
        QMUIStatusBarHelper.setStatusBarDarkMode(paramActivity);
    }

    protected void onPause()
    {
        super.onPause();
    }

    protected void onResume()
    {
        super.onResume();
    }

    public void setContentView(@LayoutRes int paramInt)
    {
        super.setContentView(paramInt);
        //setTitleBar();
    }

    public void setContentView(View paramView)
    {
        super.setContentView(paramView);
        //setTitleBar();
    }



    public void setTitleBar()
    {
        if ((isTranslucent()) && (QMUIStatusBarHelper.isSupportTranslucent()))
        {
            int i = getStatusBarHeight(this);
            View localView = findView(R.id.top_rll);
            if (localView != null)
            {
                localView.setPadding(0, i, 0, 0);
                localView.getLayoutParams().height += i;
            }
        }
    }

    public static int getStatusBarHeight(Activity paramActivity)
    {
        int i = paramActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (i > 0) {
            return paramActivity.getResources().getDimensionPixelSize(i);
        }
        return 0;
    }

    protected void showKeyboard(boolean paramBoolean)
    {
        InputMethodManager localInputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (paramBoolean)
        {
            if (getCurrentFocus() == null)
            {
                localInputMethodManager.toggleSoftInput(2, 0);
                return;
            }
            localInputMethodManager.showSoftInput(getCurrentFocus(), 0);
            return;
        }
        if (getCurrentFocus() != null) {
            localInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
        }
    }

    protected void showKeyboardDelayed(final View paramView)
    {
        if (paramView != null) {
            paramView.requestFocus();
        }
        getHandler().postDelayed(new Runnable()
        {
            public void run()
            {
                if ((paramView == null) || (paramView.isFocused())) {
                    BaseActivity.this.showKeyboard(true);
                }
            }
        }, 200L);
    }
}
