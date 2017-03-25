package com.arjinmc.buttonlib;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * a sumbit button designed by Colin Garven
 * link https://dribbble.com/shots/1426764-Submit-Button
 * Created by Eminem Lu on 24/3/17.
 * Email arjinmc@hotmail.com
 */

public class SubmitButton extends View {

    private final String DEFAULT_COLOR = "#1ECD97";

    public SubmitButton(Context context) {
        super(context);
    }

    public SubmitButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SubmitButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SubmitButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
