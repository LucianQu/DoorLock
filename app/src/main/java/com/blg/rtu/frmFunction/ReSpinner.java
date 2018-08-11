package com.blg.rtu.frmFunction;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;

/**
 * Created by qulus on 2017/9/13 0013.
 */

public class ReSpinner extends Spinner {
    public boolean isDropDownMenuShown = false;//标志下拉列表是否正在显示

    public ReSpinner(Context context) {
        super(context);
    }

    public ReSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ReSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void
    setSelection(int position, boolean animate) {
        boolean sameSelected = position == getSelectedItemPosition();
        super.setSelection(position, animate);
        if (sameSelected) {
            // 如果选择项是Spinner当前已选择的项,则 OnItemSelectedListener并不会触发,因此这里手动触发回调
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public boolean performClick() {
        this.isDropDownMenuShown = true;
        return super.performClick();
    }

    public boolean isDropDownMenuShown(){
        return isDropDownMenuShown;
    }

    public void setDropDownMenuShown(boolean isDropDownMenuShown){
        this.isDropDownMenuShown=isDropDownMenuShown;
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        boolean sameSelected ;
        if (position == getSelectedItemPosition()) {
            sameSelected = true ;
        }else {
            sameSelected = false ;
        }
        if (sameSelected) {
            getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}

