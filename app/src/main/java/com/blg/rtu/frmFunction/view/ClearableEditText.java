package com.blg.rtu.frmFunction.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;

import com.blg.rtu3.R;


public class ClearableEditText extends EditText implements OnTouchListener, TextWatcher{
    Drawable deleteImage = getResources().getDrawable(R.drawable.selector_editor_clear);
    Drawable icon;

    public ClearableEditText(Context paramContext)
    {
        super(paramContext);
        init();
    }

    public ClearableEditText(Context paramContext, AttributeSet paramAttributeSet)
    {
        super(paramContext, paramAttributeSet);
        init();
    }

    public ClearableEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
        init();
    }

    private void init()
    {
        setOnTouchListener(this);
        addTextChangedListener(this);
        this.deleteImage.setBounds(0, 0, this.deleteImage.getIntrinsicWidth(), this.deleteImage.getIntrinsicHeight());
        manageClearButton();
    }

    void addClearButton()
    {
        setCompoundDrawables(this.icon, getCompoundDrawables()[1], this.deleteImage, getCompoundDrawables()[3]);
    }

    public void afterTextChanged(Editable paramEditable) {}

    public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3) {}

    void manageClearButton()
    {
        if ((!getText().toString().equals("")) && (isEnabled()))
        {
            addClearButton();
            return;
        }
        removeClearButton();
    }

    public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
    {
        manageClearButton();
    }

    public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
    {
        if (getCompoundDrawables()[2] == null) {
            return false;
        }
        if (paramMotionEvent.getAction() != 1) {
            return false;
        }
        if (paramMotionEvent.getX() > getWidth() - getPaddingRight() - this.deleteImage.getIntrinsicWidth())
        {
            setText("");
            removeClearButton();
        }
        return false;
    }

    void removeClearButton()
    {
        setCompoundDrawables(this.icon, getCompoundDrawables()[1], null, getCompoundDrawables()[3]);
    }

    public void setDeleteImage(int paramInt)
    {
        this.deleteImage = getResources().getDrawable(paramInt);
        this.deleteImage.setBounds(0, 0, this.deleteImage.getIntrinsicWidth(), this.deleteImage.getIntrinsicHeight());
        manageClearButton();
    }

    public void setEnabled(boolean paramBoolean)
    {
        super.setEnabled(paramBoolean);
        manageClearButton();
    }

    public void setIconResource(int paramInt)
    {
        this.icon = getResources().getDrawable(paramInt);
        this.icon.setBounds(0, 0, this.icon.getIntrinsicWidth(), this.icon.getIntrinsicHeight());
        manageClearButton();
    }
}
