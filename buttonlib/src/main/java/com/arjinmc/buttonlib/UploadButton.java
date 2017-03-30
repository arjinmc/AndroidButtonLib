package com.arjinmc.buttonlib;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

/**
 * UploadButton designed by Colin Garven
 * Created by Eminem Lu on 30/3/17.
 * Email arjinmc@hotmail.com
 */

public class UploadButton extends View {

    public UploadButton(Context context) {
        super(context);
    }

    public UploadButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UploadButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public UploadButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
