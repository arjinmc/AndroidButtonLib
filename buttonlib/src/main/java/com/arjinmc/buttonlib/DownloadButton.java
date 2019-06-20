package com.arjinmc.buttonlib;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * a download button style designed by Gleb Stroganov
 * link:https://dribbble.com/shots/2551579-Download-Button
 * <p>
 * Created by Eminem Lo on 22/3/17.
 * Email arjinmc@hotmail.com
 */

public class DownloadButton extends View {

    private final int DEFAULT_TEXT_SIZE = 30;
    private final int DEFAULT_TEXT_COLOR = Color.WHITE;
    private final String DEFAULT_BACKGROUND_COLOR = "#0277bd";
    private final String DEFAULT_PROGRESS_COLOR = "#619ade";
    private final int DEFAULT_RADIUS = 20;
    private final int STATUS_NORMAL = 0;
    private final int STATUS_PRESS = 1;
    private final int STATUS_PROGRESS = 2;
    private final int STATUS_DONE = 3;

    private Paint mTxtPaint;
    private Paint mBgPaint;
    private Paint mTickPaint;
    //background of button
    private RectF mOvalRect;
    private int mWidth, mHeight;

    private ClipDrawable mProgressDrawable;
    private Path mLeftPath;
    private Path mRightPath;
    private PathMeasure mRightPathMeasure;
    private PathMeasure mLeftPathMeasure;
    private float mAnimationValue;
    private ValueAnimator mAnim;


    private int mStatus = STATUS_NORMAL;
    private boolean mCanClick = true;
    private boolean mIsDone;
    private int mProgress;
    private float mTxtHeight;

    private float mTxtSize = DEFAULT_TEXT_SIZE;
    private int mTxtColor = DEFAULT_TEXT_COLOR;
    private int mBgColor = Color.parseColor(DEFAULT_BACKGROUND_COLOR);
    private int mProgressColor = Color.parseColor(DEFAULT_PROGRESS_COLOR);
    private int mRadius = DEFAULT_RADIUS;

    private OnDownloadListener mOnDownloadListener;

    public DownloadButton(Context context) {
        super(context);
        init(null);
    }

    public DownloadButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DownloadButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DownloadButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        if (attrs != null) {

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs
                    , R.styleable.DownloadButton);
            mTxtSize = typedArray.getDimension(R.styleable.DownloadButton_DownloadButton_textSize
                    , DEFAULT_TEXT_SIZE);
            mTxtColor = typedArray.getColor(R.styleable.DownloadButton_DownloadButton_textColor
                    , DEFAULT_TEXT_COLOR);
            mProgressColor = typedArray.getColor(R.styleable.DownloadButton_DownloadButton_progressColor
                    , Color.parseColor(DEFAULT_PROGRESS_COLOR));
            mRadius = typedArray.getInteger(R.styleable.DownloadButton_DownloadButton_radius, DEFAULT_RADIUS);
            mBgColor = typedArray.getColor(R.styleable.DownloadButton_DownloadButton_backgroundColor
                    , Color.parseColor(DEFAULT_BACKGROUND_COLOR));
        }

        ColorDrawable drawable = ((ColorDrawable) getBackground());
        if (drawable != null) {
            mBgColor = drawable.getColor();
            mRadius = 0;
        }

        mTxtPaint = new Paint();
        mTxtPaint.setAntiAlias(true);
        mTxtPaint.setTextSize(mTxtSize);
        mTxtPaint.setTextAlign(Paint.Align.CENTER);
        mTxtPaint.setColor(mTxtColor);

        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setStrokeCap(Paint.Cap.ROUND);
        mBgPaint.setStyle(Paint.Style.FILL);

        mTickPaint = new Paint();
        mTickPaint.setAntiAlias(true);
        mTickPaint.setColor(mTxtColor);
        mTickPaint.setStrokeCap(Paint.Cap.ROUND);
        mTickPaint.setStyle(Paint.Style.STROKE);

        //path
        mLeftPath = new Path();
        mRightPath = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mWidth == 0) {
            mWidth = getMeasuredWidth();
            mHeight = getMeasuredHeight();
            //set size for tick
            int minThick = (mHeight > mWidth ? mWidth : mHeight) / 20;
            mTickPaint.setStrokeWidth(minThick > 0 ? minThick : 1);
        }

        if (mTxtHeight == 0) {
            Paint.FontMetrics fontMetrics = mTxtPaint.getFontMetrics();
            float fontHeight = fontMetrics.bottom - fontMetrics.top;
            mTxtHeight = mHeight - (mHeight - fontHeight) / 2 - fontMetrics.bottom;
        }

        if (mOvalRect == null) {
            mOvalRect = new RectF(0, 0, mWidth, mHeight);
        }

        switch (mStatus) {
            case STATUS_NORMAL:
                mBgPaint.setColor(mBgColor);
                canvas.drawRoundRect(mOvalRect, mRadius, mRadius, mBgPaint);
                mTxtPaint.setTextSize(mTxtSize);
                canvas.drawText(getContext().getString(R.string.download), mWidth / 2, mTxtHeight, mTxtPaint);
                break;
            case STATUS_PRESS:
                mBgPaint.setColor(mProgressColor);
                canvas.drawRoundRect(mOvalRect, mRadius, mRadius, mBgPaint);
                mTxtPaint.setTextSize((int) (mTxtSize * 1.3));
                canvas.drawText(getContext().getString(R.string.download), mWidth / 2, mTxtHeight, mTxtPaint);
                break;
            case STATUS_PROGRESS:

                //draw background
                mBgPaint.setColor(mBgColor);
                canvas.drawRoundRect(mOvalRect, mRadius, mRadius, mBgPaint);

                //draw progress
                if (mProgressDrawable == null) {
                    GradientDrawable progressGradientDrawable = new GradientDrawable();
                    progressGradientDrawable.setBounds(0, 0, mWidth, mHeight);
                    progressGradientDrawable.setColor(mProgressColor);
                    progressGradientDrawable.setCornerRadius(mRadius);
                    mProgressDrawable = new ClipDrawable(
                            progressGradientDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
                    mProgressDrawable.setBounds(0, 0, mWidth, mHeight);
                }
                mProgressDrawable.setLevel(mProgress * 100);
                mProgressDrawable.draw(canvas);

                //draw text
                mTxtPaint.setTextSize(mTxtSize);
                canvas.drawText(getContext().getString(R.string.download), mWidth / 2, mTxtHeight, mTxtPaint);
                break;
            case STATUS_DONE:
                drawTick(canvas);
                startAnimation();
//                mCanClick = true;
                break;
            default:
                break;
        }

    }

    public void drawTick(Canvas canvas) {
        mBgPaint.setColor(mProgressColor);
        canvas.drawRoundRect(mOvalRect, mRadius, mRadius, mBgPaint);

        //draw left
        mLeftPath.moveTo(mWidth / 2, mHeight * 3 / 4);
        mLeftPath.lineTo(mWidth / 2 - mHeight / 4, mHeight * 3 / 5);

        mLeftPathMeasure = new PathMeasure(mLeftPath, true);
        Path leftDst = new Path();
        float leftStop = mLeftPathMeasure.getLength() * mAnimationValue;
        mLeftPathMeasure.getSegment(0, leftStop, leftDst, true);
        canvas.drawPath(leftDst, mTickPaint);


        //draw right
        mRightPath.moveTo(mWidth / 2, mHeight * 3 / 4);
        mRightPath.lineTo(mWidth / 2 + mHeight / 4, mHeight / 4);

        mRightPathMeasure = new PathMeasure(mRightPath, true);
        Path rightDst = new Path();
        float rightStop = mRightPathMeasure.getLength() * mAnimationValue;
        mRightPathMeasure.getSegment(0, rightStop, rightDst, true);
        canvas.drawPath(rightDst, mTickPaint);


        //some devices do not support pathmeasure
        if (mIsDone) {
            canvas.drawPath(mLeftPath, mTickPaint);
            canvas.drawPath(mRightPath, mTickPaint);
        }


    }

    private void startAnimation() {
        if (mAnim == null) {
            mAnim = ValueAnimator.ofFloat(0, 1);
            mAnim.setDuration(1500);
            mAnim.setInterpolator(new LinearInterpolator());
            mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (!mIsDone) {
                        mAnimationValue = (float) animation.getAnimatedValue();
                        postInvalidate();
                    }
                }
            });
            mAnim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsDone = true;
                    if (mOnDownloadListener != null) {
                        mOnDownloadListener.onDone();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }


        if (!mAnim.isRunning() && !mIsDone) {
            mAnim.start();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mCanClick) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mStatus = STATUS_PRESS;
                    postInvalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    mStatus = STATUS_PROGRESS;
                    postInvalidate();
                    if (mOnDownloadListener != null) {
                        mOnDownloadListener.onReady();
                    }
                    mCanClick = false;
                    break;
                default:
                    break;
            }
        }
        return true;
    }


    public void setProgress(int progress) {
        if (mProgress < 0) {
            new Exception("Progress cannot smaller than 0").printStackTrace();
        } else {
            this.mProgress = progress;
            if (mProgress >= 100) {
                mStatus = STATUS_DONE;
            } else {
                mStatus = STATUS_PROGRESS;
            }
            postInvalidate();
        }
    }

    public int getProgress() {
        return mProgress;
    }

    public void reset() {
        if (mAnim != null) {
            mAnim.cancel();
        }
        mStatus = STATUS_NORMAL;
        mCanClick = true;
        mIsDone = false;
        mProgress = 0;
        postInvalidate();
    }

    public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
        this.mOnDownloadListener = onDownloadListener;
    }

    public interface OnDownloadListener {
        public void onReady();

        public void onDone();
    }
}
