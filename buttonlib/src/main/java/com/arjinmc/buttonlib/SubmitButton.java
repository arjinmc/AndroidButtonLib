package com.arjinmc.buttonlib;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * a sumbit button designed by Colin Garven
 * Created by Eminem Lu on 24/3/17.
 * Email arjinmc@hotmail.com
 */

public class SubmitButton extends View {

    private final String DEFAULT_THEME_COLOR = "#1ecd97";
    private final String DEFAULT_BACKGROUND_COLOR = "#ffffff";
    private final String DEFAULT_BORDER_COLOR = "#bababa";
    private final String DEFAULT_ERROR_COLOR = "#d5767c";
    private final float DEFAULT_RADIUS = 50f;
    private final float DEFAULT_THICK = 5f;
    private final long ANIMATION_DURATION = 1000;

    private final int STATUS_NORMAL = 0;
    private final int STATUS_PRESS = 1;
    private final int STATUS_PRESS_RELEASE = 2;
    private final int STATUS_PROGRESS = 3;
    private final int STATUS_DONE = 4;
    private final int STATUS_ERROR = 5;
    private final int STATUS_RESET = 6;

    private int mWidth, mHeight;
    private int mStatus = STATUS_NORMAL;
    private int mLastSignalStatus = -1;
    private boolean mCanClick = true;
    private boolean mIsSignal;
    private int mProgress = 0;
    private float mTxtSize = 30;
    private float mRadius = DEFAULT_RADIUS;
    private float mThick = DEFAULT_THICK;
    private int mThemeColor = Color.parseColor(DEFAULT_THEME_COLOR);
    private int mBackgroundColor = Color.parseColor(DEFAULT_BACKGROUND_COLOR);
    private int mBorderColor = Color.parseColor(DEFAULT_BORDER_COLOR);
    private int mErrorColor = Color.parseColor(DEFAULT_ERROR_COLOR);

    private Paint mTxtPaint, mProgressPaint, mSignalPaint;
    private float mTxtHeight;
    //draw circle radius
    private int mCircleRadius;
    //the width for change square to circle
    private int mChangeTotalWidth;
    private GradientDrawable mBackgroundDrawable;
    private RectF mProgressRectf;
    private Path mLeftPath;
    private Path mRightPath;
    private PathMeasure mRightPathMeasure;
    private PathMeasure mLeftPathMeasure;

    private ValueAnimator mBackgroundAnimation;
    private ValueAnimator mBorderAnimation;
    private ValueAnimator mTxtAnimation;
    //animation for tick or cross
    private ValueAnimator mSignalAnimtion;

    private int mBackgroundColorAnimationValue;
    private int mBorderColorAnimationValue;
    private int mTxtColorAnimationValue;
    private float mTxtColorAlphaAnimationValue;
    private float mSignalColorAlphaAnimationValue;

    private OnSubmitListener mOnSubmitListener;


    public SubmitButton(Context context) {
        super(context);
        init(null);
    }

    public SubmitButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SubmitButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SubmitButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        if(attrs!=null){
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.SubmitButton);
            mThemeColor = typedArray.getColor(R.styleable.SubmitButton_SubmitButton_themeColor,mThemeColor);
            mBorderColor =  typedArray.getColor(R.styleable.SubmitButton_SubmitButton_borderColor,mBorderColor);
            mErrorColor = typedArray.getColor(R.styleable.SubmitButton_SubmitButton_errorColor,mErrorColor);
            mTxtSize = typedArray.getDimension(R.styleable.SubmitButton_SubmitButton_textSize,mTxtSize);
            mRadius = typedArray.getInt(R.styleable.SubmitButton_SubmitButton_radius,(int)mRadius);
            mThick = typedArray.getDimension(R.styleable.SubmitButton_SubmitButton_thickness,mThick);
        }

        mBackgroundDrawable = new GradientDrawable();

        mTxtPaint = new Paint();
        mTxtPaint.setAntiAlias(true);
        mTxtPaint.setTextSize(mTxtSize);
        mTxtPaint.setTextAlign(Paint.Align.CENTER);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setColor(mThemeColor);
        mProgressPaint.setStrokeWidth(mThick);

        mSignalPaint = new Paint();
        mSignalPaint.setAntiAlias(true);
        mSignalPaint.setColor(mBackgroundColor);
        mSignalPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mWidth == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
            //set size for signal paint
            int minThick = (mHeight > mWidth ? mWidth : mHeight) / 20;
            mSignalPaint.setStrokeWidth(minThick > 0 ? minThick : 1);
        }

        if (mTxtHeight == 0) {
            Paint.FontMetrics fontMetrics = mTxtPaint.getFontMetrics();
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            mTxtHeight = mHeight - (mHeight - fontHeight) / 2 - fontMetrics.bottom;
        }

        if (mCircleRadius == 0) {
            mCircleRadius = (mWidth > mHeight ? mHeight : mWidth) / 2;
            mChangeTotalWidth = mWidth / 2 - mCircleRadius;
        }

        switch (mStatus) {

            case STATUS_NORMAL:

                mBackgroundDrawable.setShape(GradientDrawable.RECTANGLE);
                mBackgroundDrawable.setCornerRadius(mRadius);
                mBackgroundDrawable.setBounds(0, 0, mWidth, mHeight);
                mBackgroundDrawable.setColor(mBackgroundColor);
                mBackgroundDrawable.setStroke((int) mThick, mThemeColor);
                mBackgroundDrawable.draw(canvas);

                mTxtPaint.setColor(mThemeColor);
                mTxtPaint.setAlpha(255);
                canvas.drawText(getContext().getString(R.string.submit), mWidth / 2, mTxtHeight, mTxtPaint);

                break;

            case STATUS_PRESS:
                mBackgroundDrawable.setColor(mBackgroundColorAnimationValue);
                mBackgroundDrawable.draw(canvas);

                mTxtPaint.setColor(mTxtColorAnimationValue);
                canvas.drawText(getContext().getString(R.string.submit), mWidth / 2, mTxtHeight, mTxtPaint);

                break;

            case STATUS_PRESS_RELEASE:

                int changeXRelease = mChangeTotalWidth - (int) (mChangeTotalWidth - mChangeTotalWidth
                        * ((float) (mThemeColor - mBackgroundColorAnimationValue) / mThemeColor));
                mBackgroundDrawable.setBounds(changeXRelease, 0, mWidth - changeXRelease, mHeight);
                mBackgroundDrawable.setColor(mBackgroundColorAnimationValue);
                mBackgroundDrawable.setStroke((int) mThick, mBorderColorAnimationValue);
                mBackgroundDrawable.draw(canvas);

                mTxtPaint.setAlpha((int) (mTxtColorAlphaAnimationValue * 255));
                canvas.drawText(getContext().getString(R.string.submit), mWidth / 2, mTxtHeight, mTxtPaint);
                break;

            case STATUS_PROGRESS:

                mBackgroundDrawable.setShape(GradientDrawable.OVAL);
                mBackgroundDrawable.draw(canvas);


                if (mProgressRectf == null) {
                    int midThick = Math.round(mThick / 2);
                    mProgressRectf = new RectF(mChangeTotalWidth + midThick, midThick
                            , mWidth - mChangeTotalWidth - midThick, mHeight - midThick);
                }
                canvas.drawArc(mProgressRectf, -90, (int) (3.6 * mProgress), false, mProgressPaint);

                break;

            case STATUS_DONE:

                drawSignalBackground(canvas, false);

                //draw tick
                mSignalPaint.setAlpha((int) mSignalColorAlphaAnimationValue * 255);
                //draw left
                if (mLeftPath == null) {
                    mLeftPath = new Path();
                    mLeftPath.moveTo(mWidth / 2, mHeight * 3 / 4);
                    mLeftPath.lineTo(mWidth / 2 - mHeight / 4, mHeight * 3 / 5);
                }

                mLeftPathMeasure = new PathMeasure(mLeftPath, true);
                Path leftDstTick = new Path();
                float leftStopTick = mLeftPathMeasure.getLength() * mSignalColorAlphaAnimationValue;
                mLeftPathMeasure.getSegment(0, leftStopTick, leftDstTick, true);
                canvas.drawPath(leftDstTick, mSignalPaint);

                //draw right
                if (mRightPath == null) {
                    mRightPath = new Path();
                    mRightPath.moveTo(mWidth / 2, mHeight * 3 / 4);
                    mRightPath.lineTo(mWidth / 2 + mHeight / 4, mHeight / 4);
                }

                mRightPathMeasure = new PathMeasure(mRightPath, true);
                Path rightDstTick = new Path();
                float rightStopTick = mRightPathMeasure.getLength() * mSignalColorAlphaAnimationValue;
                mRightPathMeasure.getSegment(0, rightStopTick, rightDstTick, true);
                canvas.drawPath(rightDstTick, mSignalPaint);

                //some devices do not support pathmeasure
                if (mIsSignal) {
                    canvas.drawPath(mLeftPath, mSignalPaint);
                    canvas.drawPath(mRightPath, mSignalPaint);
                }

                break;
            case STATUS_ERROR:

                drawSignalBackground(canvas, true);

                //draw cross
                mSignalPaint.setAlpha((int) mSignalColorAlphaAnimationValue * 255);
                //draw left
                if (mLeftPath == null) {
                    mLeftPath = new Path();
                    mLeftPath.moveTo(mWidth / 2 - mHeight / 4, mHeight / 4);
                    mLeftPath.lineTo(mWidth / 2 + mHeight / 4, mHeight * 3 / 4);
                }

                mLeftPathMeasure = new PathMeasure(mLeftPath, true);
                Path leftDstCross = new Path();
                float leftStopCross = mLeftPathMeasure.getLength() * mSignalColorAlphaAnimationValue;
                mLeftPathMeasure.getSegment(0, leftStopCross, leftDstCross, true);
                canvas.drawPath(leftDstCross, mSignalPaint);

                //draw right
                if (mRightPath == null) {
                    mRightPath = new Path();
                    mRightPath.moveTo(mWidth / 2 + mHeight / 4, mHeight / 4);
                    mRightPath.lineTo(mWidth / 2 - mHeight / 4, mHeight * 3 / 4);
                }

                mRightPathMeasure = new PathMeasure(mRightPath, true);
                Path rightDstCross = new Path();
                float rightStopCross = mRightPathMeasure.getLength() * mSignalColorAlphaAnimationValue;
                mRightPathMeasure.getSegment(0, rightStopCross, rightDstCross, true);
                canvas.drawPath(rightDstCross, mSignalPaint);

                //some devices do not support pathmeasure
                if (mIsSignal) {
                    canvas.drawPath(mLeftPath, mSignalPaint);
                    canvas.drawPath(mRightPath, mSignalPaint);
                }
                break;
            case STATUS_RESET:
                drawResetBackground(canvas);
                break;

            default:
                break;
        }

    }

    /**
     * draw sigal background for done or error
     *
     * @param canvas
     */
    private void drawSignalBackground(Canvas canvas, boolean isError) {
        int dstColor = mThemeColor;
        if (isError) dstColor = mErrorColor;
        int changeXDone = (int) (mChangeTotalWidth - mChangeTotalWidth
                * ((float) (dstColor - mBackgroundColorAnimationValue) / dstColor));
        mBackgroundDrawable.setShape(GradientDrawable.RECTANGLE);
        mBackgroundDrawable.setBounds(mChangeTotalWidth - changeXDone, 0
                , mWidth - mChangeTotalWidth + changeXDone, mHeight);
        mBackgroundDrawable.setColor(mBackgroundColorAnimationValue);
        mBackgroundDrawable.setStroke((int) mThick, mBorderColorAnimationValue);
        mBackgroundDrawable.draw(canvas);

    }

    private void drawResetBackground(Canvas canvas) {
        mBackgroundDrawable.setColor(mBackgroundColorAnimationValue);
        mBackgroundDrawable.setStroke((int) mThick, mBorderColorAnimationValue);
        mBackgroundDrawable.draw(canvas);

        mSignalPaint.setAlpha((int) mSignalColorAlphaAnimationValue * 255);
        if (mLastSignalStatus == STATUS_DONE) {
            //draw tick
            //draw left
            if(mLeftPath ==null) {
                mLeftPath = new Path();
                mLeftPath.moveTo(mWidth / 2, mHeight * 3 / 4);
                mLeftPath.lineTo(mWidth / 2 - mHeight / 4
                        , mHeight * 3 / 5);
            }
            canvas.drawPath(mLeftPath, mSignalPaint);

            //draw right
            if(mRightPath==null) {
                mRightPath = new Path();
                mRightPath.moveTo(mWidth / 2, mHeight * 3 / 4);
                mRightPath.lineTo(mWidth / 2 + mHeight / 4
                        , mHeight / 4);
            }
            canvas.drawPath(mRightPath, mSignalPaint);

        }else{
            //draw cross
            //draw left
            if (mLeftPath == null) {
                mLeftPath = new Path();
                mLeftPath.moveTo(mWidth / 2 - mHeight / 4, mHeight / 4);
                mLeftPath.lineTo(mWidth / 2 + mHeight / 4, mHeight * 3 / 4);
            }

            canvas.drawPath(mLeftPath, mSignalPaint);

            //draw right
            if (mRightPath == null) {
                mRightPath = new Path();
                mRightPath.moveTo(mWidth / 2 + mHeight / 4, mHeight / 4);
                mRightPath.lineTo(mWidth / 2 - mHeight / 4, mHeight * 3 / 4);
            }
            canvas.drawPath(mRightPath, mSignalPaint);

        }

        mTxtPaint.setColor(mThemeColor);
        mTxtPaint.setAlpha((int) (mTxtColorAlphaAnimationValue * 255));
        canvas.drawText(getContext().getString(R.string.submit), mWidth / 2, mTxtHeight, mTxtPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mCanClick) {
                    mCanClick = false;
                    if (mStatus == STATUS_NORMAL) {
                        mStatus = STATUS_PRESS;
                        startPressStatusAnimation();
                    } else {
                        resetStatus();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mStatus == STATUS_PRESS) {
                    mStatus = STATUS_PRESS_RELEASE;
                    startPressReleaseStatusAnimation();
                }
                break;
        }
        return true;
    }

    public void setDoneStatus() {
        if (mStatus == STATUS_PROGRESS) {
            mLeftPath = null;
            mRightPath = null;
            mStatus = STATUS_DONE;
            startSignalAnimation(false);
        }
    }

    public void setErrorStatus() {
        if (mStatus == STATUS_PROGRESS) {
            mLeftPath = null;
            mRightPath = null;
            mStatus = STATUS_ERROR;
            startSignalAnimation(true);
        }
    }

    private void resetStatus() {
        mLeftPath = null;
        mRightPath = null;
        mLastSignalStatus = mStatus;
        if (mStatus == STATUS_DONE) {
            startResetAnimation(false);
        } else if (mStatus == STATUS_ERROR) {
            startResetAnimation(true);
        }
        mStatus = STATUS_RESET;
    }

    @Override
    public void setClickable(boolean clickable) {
        super.setClickable(clickable);
        mCanClick = clickable;
    }

    public void setProgress(int progress) {
        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;
        mProgress = progress;
        if (progress == 100) {
            setDoneStatus();
        } else {
            mStatus = STATUS_PROGRESS;
            postInvalidate();
        }
    }

    public int getProgress() {
        return mProgress;
    }


    /**
     * start press status animations
     */
    private void startPressStatusAnimation() {

        mBackgroundAnimation = createColorAnimation(mBackgroundColor, mThemeColor);
        mBackgroundAnimation.setDuration(ANIMATION_DURATION / 2);
        mBackgroundAnimation.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mBackgroundColorAnimationValue = (int) animation.getAnimatedValue();
                        postInvalidate();
                    }
                });
        mBackgroundAnimation.start();

        mTxtAnimation = createColorAnimation(mThemeColor, mBackgroundColor);
        mTxtAnimation.setDuration(ANIMATION_DURATION / 2);
        mTxtAnimation.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mTxtColorAnimationValue = (int) animation.getAnimatedValue();
                    }
                });
        mTxtAnimation.start();
    }


    /**
     * press release status animtaions
     */
    private void startPressReleaseStatusAnimation() {

        mBackgroundAnimation = createColorAnimation(mThemeColor, mBackgroundColor);
        mBackgroundAnimation.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mBackgroundColorAnimationValue = (int) animation.getAnimatedValue();
                        postInvalidate();
                    }
                });
        mBackgroundAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mOnSubmitListener != null) {
                    mOnSubmitListener.onReady();
                }
                releaseAnimations();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mBackgroundAnimation.start();

        mBorderAnimation = createColorAnimation(mThemeColor, mBorderColor);
        mBorderAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBorderColorAnimationValue = (int) animation.getAnimatedValue();
            }
        });
        mBorderAnimation.start();

        mTxtAnimation = ValueAnimator.ofFloat(1f, 0f);
        mTxtAnimation.setDuration(ANIMATION_DURATION / 2);
        mTxtAnimation.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mTxtColorAlphaAnimationValue = (float) animation.getAnimatedValue();
                    }
                });
        mTxtAnimation.start();

    }

    /**
     * these animations is for done status and error status
     *
     * @param isError true for error,false for done
     */
    private void startSignalAnimation(boolean isError) {
        int dstColor = mThemeColor;
        if (isError) dstColor = mErrorColor;
        mBackgroundAnimation = createColorAnimation(mBackgroundColor, dstColor);
        mBackgroundAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackgroundColorAnimationValue = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mBackgroundAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                releaseAnimations();
                mIsSignal = true;
                mCanClick = true;

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mBackgroundAnimation.start();

        mBorderAnimation = createColorAnimation(mBorderColor, dstColor);
        mBorderAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBorderColorAnimationValue = (int) animation.getAnimatedValue();
            }
        });
        mBorderAnimation.start();

        mSignalAnimtion = ValueAnimator.ofFloat(0f, 1f);
        mSignalAnimtion.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSignalColorAlphaAnimationValue = (float) animation.getAnimatedValue();
            }
        });
        mSignalAnimtion.start();
    }


    /**
     * reset animation from done or error status
     *
     * @param isError true is from error status,error is from done status
     */
    private void startResetAnimation(boolean isError) {

        int dstColor = mThemeColor;
        if (isError) dstColor = mErrorColor;
        mBackgroundAnimation = createColorAnimation(dstColor, mBackgroundColor);

        mBackgroundAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBackgroundColorAnimationValue = (int) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        mBackgroundAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                releaseAnimations();
                mIsSignal = false;
                mCanClick = true;
                mStatus = STATUS_NORMAL;
                mLastSignalStatus = -1;
                if (mOnSubmitListener != null) mOnSubmitListener.onSignalFinsh();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mBackgroundAnimation.start();

        mBorderAnimation = createColorAnimation(dstColor, mThemeColor);
        mBorderAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBorderColorAnimationValue = (int) animation.getAnimatedValue();
            }
        });
        mBorderAnimation.start();

        mSignalAnimtion = ValueAnimator.ofFloat(1f, 0f);
        mSignalAnimtion.setDuration(ANIMATION_DURATION);
        mSignalAnimtion.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSignalColorAlphaAnimationValue = (float) animation.getAnimatedValue();
            }
        });
        mSignalAnimtion.start();

        mTxtAnimation = ValueAnimator.ofFloat(0f, 1f);
        mTxtAnimation.setDuration(ANIMATION_DURATION);
        mTxtAnimation.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mTxtColorAlphaAnimationValue = (float) animation.getAnimatedValue();
                    }
                });
        mTxtAnimation.start();

    }

    private void releaseAnimations() {

        mBackgroundAnimation = null;
        mBorderAnimation = null;
        mTxtAnimation = null;
        mSignalAnimtion = null;
    }

    /**
     * this method replace for ValueAnimator.ofArgb() support below sdk 6.0
     *
     * @param values
     * @return
     */
    private ValueAnimator createColorAnimation(int... values) {
        ValueAnimator colorAnimation = new ValueAnimator();
        colorAnimation.setIntValues(values);
        colorAnimation.setEvaluator(new ArgbEvaluator());
        colorAnimation.setDuration(ANIMATION_DURATION);
        return colorAnimation;
    }

    public void setOnSubmitListener(OnSubmitListener onSubmitListener) {
        mOnSubmitListener = onSubmitListener;
    }

    public interface OnSubmitListener {

        public void onReady();

        public void onSignalFinsh();
    }
}
