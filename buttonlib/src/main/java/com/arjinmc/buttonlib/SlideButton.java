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
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

/**
 * Slide Button
 * if donot slide to the edge of right,it will rewind to the left
 * Created by Eminem Lo on 8/3/17.
 * Email arjinmc@hotmail.com
 */

public class SlideButton extends RelativeLayout {

    private final int DEFAULT_BACKGROUND_COLOR = Color.BLACK;
    private final int PROGRESS_OFFSET = 5;

    private Button mButton;
    private Paint mTxtPaint;
    private Shader mShader;
    private Paint mLightPaint;
    private Paint mCheckedPaint;

    /**
     * mark for touch mButton down
     */
    private float mDownX;
    /**
     * mark for touch mButton move
     */
    private float mMoveX;
    /**
     * mark mButton move totoal distance for translation
     */
    private float mTotalMove;
    /**
     * animtaion for mButton reserve to original place
     */
    private ValueAnimator mRewindAnimation;
    /**
     * animation for light view
     */
    private ValueAnimator mLightAnimation;
    /**
     * callback for slidebutton check status
     */
    private OnSlideListender mOnSlideListender;
    /**
     * mark slidebutton status
     */
    private boolean mSlideStatus = false;
    /**
     * text height
     */
    private float mTextHeight;
    private ClipDrawable mProgressDrawable;
    private int mProgress;
    /**
     * this view's width and height
     */
    private int mWidth, mHeight;

    //property
    private int mTipsColor = Color.WHITE;
    private float mTipsTextSize = -1;
    private int mSlideButtonDrawableID = -1;
    private String mTips;
    private boolean mShaderVisible = false;
    private int mShaderColor;
    private float mShaderRadius;
    private int mShaderBackgoundColor;
    /**
     * slide across area color
     */
    private int mCheckedColor;
    /**
     * slide across area background radius
     */
    private float mBackgroundRadius;

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
            mTips = lAttrs.getString(R.styleable.SlideButton_SlideButton_text);
            if (TextUtils.isEmpty(mTips)) {
                mTips = getContext().getString(R.string.slidebutton_tips);
            }
            mSlideButtonDrawableID = lAttrs.getResourceId(
                    R.styleable.SlideButton_SlideButton_button, R.drawable.btn_silver_slide);
            mShaderVisible = lAttrs.getBoolean(R.styleable.SlideButton_SlideButton_shaderVisible, false);
            mShaderColor = lAttrs.getColor(R.styleable.SlideButton_SlideButton_shaderColor, Color.WHITE);
            mShaderRadius = lAttrs.getFloat(R.styleable.SlideButton_SlideButton_shaderRadius, 90f);
            mShaderBackgoundColor = lAttrs.getColor(R.styleable.SlideButton_SlideButton_shaderBackgroundColor, Color.TRANSPARENT);
            mCheckedColor = lAttrs.getColor(R.styleable.SlideButton_SlideButton_checkedColor, Color.TRANSPARENT);
            mBackgroundRadius = lAttrs.getDimensionPixelSize(R.styleable.SlideButton_SlideButton_backgroundRadius, 0);
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

        mTxtPaint = new Paint();
        mTxtPaint.setAntiAlias(true);
        mTxtPaint.setColor(mTipsColor);
        mTxtPaint.setStrokeWidth(mTipsTextSize);
        mTxtPaint.setTextSize(mTipsTextSize);
        mTxtPaint.setTextAlign(Paint.Align.CENTER);

        mLightPaint = new Paint();
        mLightPaint.setAntiAlias(true);

        mCheckedPaint = new Paint();
        mCheckedPaint.setColor(mCheckedColor);
        mCheckedPaint.setAntiAlias(true);
        mCheckedPaint.setStyle(Paint.Style.FILL);

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
                        mTotalMove += alterMove;
                        if (mTotalMove <= 0) {
                            mTotalMove = 0;
                        } else if (mTotalMove >= getWidth() - mButton.getWidth()) {
                            mTotalMove = getWidth() - mButton.getWidth();
                        }
                        mButton.setTranslationX(mTotalMove);
                        if (mCheckedColor != Color.TRANSPARENT) {
                            //2 is offset to optimize the ux
                            mProgress = (int) (mTotalMove / (double) getWidth() * 100) + PROGRESS_OFFSET;
                            postInvalidate();
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        if (mTotalMove < getWidth() - mButton.getWidth()) {
                            startSlideAnimation();
                        }
                        break;
                    default:
                        break;
                }
                updateTips();
                return false;
            }
        });
    }

    public void setText(String text) {

        mTips = text;
        if (TextUtils.isEmpty(mTips)) {
            mTips = "";
        }
        postInvalidate();
    }

    public void setText(@StringRes int textResId) {
        mTips = getContext().getString(textResId);
        setText(mTips);
    }

    public void setTextSize(int size) {
        if (size <= 0) {
            return;
        }
        mTipsTextSize = size;
        mTxtPaint.setStrokeWidth(mTipsTextSize);
        mTxtPaint.setTextSize(mTipsTextSize);
        postInvalidate();
    }

    public void setTextColor(@ColorRes int colorResId) {
        int color = ContextCompat.getColor(getContext(), colorResId);
        mTipsColor = color;
        mTxtPaint.setColor(mTipsColor);
        postInvalidate();
    }

    public void setTextColor(String color) {
        mTipsColor = Color.parseColor(color);
        mTxtPaint.setColor(mTipsColor);
        postInvalidate();
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
                    mTotalMove = 0;
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
                    mTotalMove -= mTotalMove + value;
                    updateTips();

                    if (mCheckedColor != Color.TRANSPARENT) {
                        mProgress = Math.abs((int) (mTotalMove / (double) getWidth() * 100)) + PROGRESS_OFFSET;
                        postInvalidate();
                    }

                }
            });
            mRewindAnimation.setInterpolator(new AccelerateInterpolator());
            mRewindAnimation.setDuration(500);
        }
        mRewindAnimation.start();

    }


    /**
     * update tips view
     */
    private void updateTips() {
        if (mTotalMove == 0) {
            if (mShaderVisible) {
                startLightAnimation();
            }
            if (mOnSlideListender != null && mSlideStatus) {
                mSlideStatus = false;
                mOnSlideListender.onStatusChange(mSlideStatus);
            }
        } else if (mTotalMove == getWidth() - mButton.getWidth()) {
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
        if (mShaderVisible) {
            startLightAnimation();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (mShader != null) {
            canvas.drawRect(0, 0, getMeasuredWidth(), getHeight(), mLightPaint);
            mLightPaint.setShader(mShader);
        }

        if (mWidth == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
        }

        //draw progress
        if (mCheckedColor != Color.TRANSPARENT) {
            if (mProgressDrawable == null) {
                GradientDrawable progressGradientDrawable = new GradientDrawable();
                progressGradientDrawable.setBounds(0, 0, mWidth, mHeight);
                progressGradientDrawable.setColor(mCheckedColor);
                progressGradientDrawable.setCornerRadius(mBackgroundRadius);
                mProgressDrawable = new ClipDrawable(
                        progressGradientDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
                mProgressDrawable.setBounds(0, 0, mWidth, mHeight);
            }
            mProgressDrawable.setLevel(mProgress * 100);
            mProgressDrawable.draw(canvas);
        }

        //count the pointY of vertical center
        if (mTextHeight == 0) {
            Paint.FontMetrics fontMetrics = mTxtPaint.getFontMetrics();
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            mTextHeight = getMeasuredHeight() - (getMeasuredHeight() - fontHeight) / 2 - fontMetrics.bottom;
        }

        canvas.drawText(mTips, getMeasuredWidth() / 2, mTextHeight, mTxtPaint);

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
                        mShader = new RadialGradient(lightMove, getMeasuredHeight() / 2, mShaderRadius
                                , new int[]{mShaderColor, mShaderBackgoundColor}, null, Shader.TileMode.CLAMP);
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

    public void setOnSlideListender(OnSlideListender onSlideListender) {
        this.mOnSlideListender = onSlideListender;
    }

    //callback for check status
    public interface OnSlideListender {
        public void onStatusChange(boolean status);
    }

}

