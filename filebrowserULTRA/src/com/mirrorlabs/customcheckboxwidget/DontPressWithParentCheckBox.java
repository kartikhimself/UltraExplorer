package com.mirrorlabs.customcheckboxwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;


public class DontPressWithParentCheckBox extends CheckBox {

    public DontPressWithParentCheckBox(Context context) {
        super(context);
    }

    public DontPressWithParentCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DontPressWithParentCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        // Make sure the parent is a View prior casting it to View
        if (pressed && getParent() instanceof View && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
    }

}
