package com.arjinmc.buttonlib;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Slide Button
 * if donot slide to the edge of right,it will rewind to the left
 * Created by Eminem Lu on 8/3/17.
 * Email arjinmc@hotmail.com
 */

public class SlideButton extends RelativeLayout {

    private final int DEFAULT_BACKGROUND_COLOR = Color.BLACK;

    private Button mButton;
    private Paint mTxtPaint;
    private Shader mShader;
    private Paint mLightPaint;

    //mark for touch mButton down
    private float mDownX;
    //mark for touch mButton move
    private float mMoveX;
    //mark mButton move totoal distance for translation
    private float mTotoalMove;
    //animtaion for mButton reserve to original place
    private ValueAnimator mRewindAnimation;
    //animation for light view
    private ValueAnimator mLightAnimation;
    //callback for slidebutton check status
    private OnSlideListender mOnSlideListender;
    //mark slidebutton status
    private boolean mSlideStatus = false;
    //text height
    private float mTextHeight;

    //property
    private int mTipsColor = Color.WHITE;
    private float mTipsTextSize = -1;
    private int mSlideButtonDrawableID = -1;
    private int mBackgroudColor;

    public SlideButton(Context context) {
        super(context);
        init(null);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SlideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SlideButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        if (attrs != null) {

            TypedArray lAttrs = getContext().obtainStyledAttributes(
                    attrs, R.styleable.SlideButton);
            mTipsColor = lAttrs.getColor(R.styleable.SlideButton_SlideButton_tipsColor, Color.WHITE);
            mTipsTextSize = lAttrs.getDimension(R.styleable.SlideButton_SlideButton_tipsTextSize, -1);
            mSlideButtonDrawableID = lAttrs.getResourceId(
                    R.styleable.SlideButton_SlideButton_button, R.drawable.btn_silver_slide);
        }

        //slide mButton
        mButton = new Button(getContext());
        mButton.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (mSlideButtonDrawableID != 0) {
            mButton.setBackgroundResource(mSlideButtonDrawableID);
        } else {
            mButton.setBackgroundResource(R.drawable.btn_silver_slide);
        }

        addView(mButton);
        measure(0, 0);


        //get background color
        try {
            mBackgroudColor = ((ColorDrawable) getBackground()).getColor();
        } catch (Exception e) {
            mBackgroudColor = DEFAULT_BACKGROUND_COLOR;
            e.printStackTrace();
        }

        setBackgroundColor(mBackgroudColor);

        mTxtPaint = new Paint();
        mTxtPaint.setAntiAlias(true);
        mTxtPaint.setColor(mTipsColor);
        mTxtPaint.setStrokeWidth(mTipsTextSize);
        mTxtPaint.setTextSize(mTipsTextSize);
        mTxtPaint.setTextAlign(Paint.Align.CENTER);
        mLightPaint = new Paint();
        mLightPaint.setAntiAlias(true);

        initTouchButton();

    }

    private void initTouchButton() {
        mButton.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mDownX = event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        mMoveX = event.getX();
                        float alterMove = mMoveX - mDownX;
                        mTotoalMove += alterMove;
                        if (mTotoalMove <= 0) {
                            mTotoalMove = 0;
                        } else if (mTotoalMove >= getWidth() - mButton.getWidth()) {
                            mTotoalMove = getWidth() - mButton.getWidth();
                        }
                        mButton.setTranslationX(mTotoalMove);
                        break;
                    case MotionEvent.ACTION_UP:

                        if (mTotoalMove < getWidth() - mButton.getWidth()) {
                            startSlideAnimation();
                        }
                        break;
                }
                updateTips();
                return false;
            }
        });
    }

    private void startSlideAnimation() {

        if (mRewindAnimation == null) {
            mRewindAnimation = ObjectAnimator.ofFloat(mButton, "translationX"
                    , 0);
            mRewindAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    mTotoalMove = 0;

                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            mRewindAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {

                    float value = (Float) animation.getAnimatedValue();
                    mTotoalMove -= mTotoalMove + value;
                    updateTips();

                }
            });
            mRewindAnimation.setInterpolator(new AccelerateInterpolator());
            mRewindAnimation.setDuration(500);
        }
        mRewindAnimation.start();

    }


    //update tips view
    private void updateTips() {
        if (mTotoalMove == 0) {
            startLightAnimation();
            if (mOnSlideListender != null && mSlideStatus) {
                mSlideStatus = false;
                mOnSlideListender.onStatusChange(mSlideStatus);
            }
        } else if (mTotoalMove == getWidth() - mButton.getWidth()) {
            stopLightAnimation();
            if (mOnSlideListender != null && !mSlideStatus) {
                mSlideStatus = true;
                mOnSlideListender.onStatusChange(mSlideStatus);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        startLightAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mShader != null) {
            canvas.drawRect(0, 0, getMeasuredWidth(), getHeight(), mLightPaint);
            mLightPaint.setShader(mShader);
        }

        //count the pointY of vertial center
        if (mTextHeight == 0) {
            Paint.FontMetrics fontMetrics = mTxtPaint.getFontMetrics();
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            mTextHeight = getMeasuredHeight() - (getMeasuredHeight() - fontHeight) / 2 - fontMetrics.bottom;
        }

        canvas.drawText(getContext().getString(R.string.slidebutton_tips)
                , getMeasuredWidth() / 2, mTextHeight, mTxtPaint);

        super.onDraw(canvas);

    }

    private void startLightAnimation() {

        if (mLightAnimation == null) {

            mLightAnimation = ObjectAnimator.ofFloat(10, getMeasuredWidth());
            mLightAnimation.setRepeatCount(ValueAnimator.INFINITE);
            mLightAnimation.setDuration(3000);
            mLightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float lightMove = (float) animation.getAnimatedValue();
                    if (Math.abs(lightMove) < getMeasuredWidth()) {
                        mShader = new RadialGradient(lightMove, getMeasuredHeight() / 2, 90f
                                , new int[]{Color.WHITE, mBackgroudColor}, null, Shader.TileMode.CLAMP);
                        postInvalidate();
                    }

                }
            });
        }

        if (mLightAnimation != null && !mLightAnimation.isRunning()) {
            mLightAnimation.start();
        }

    }

    private void stopLightAnimation() {
        if (mLightAnimation != null && mLightAnimation.isRunning()) {

            mLightAnimation.cancel();
            mShader = null;
            postInvalidate();

        }
    }

    public boolean getStatus() {
        return mSlideStatus;
    }

    public void setmOnSlideListender(OnSlideListender mOnSlideListender) {
        this.mOnSlideListender = mOnSlideListender;
    }

    //callback for check status
    public interface OnSlideListender {
        public void onStatusChange(boolean status);
    }

}

