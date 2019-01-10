package com.blg.rtu.frmFunction.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.blg.rtu3.R;

public class CountDownProgress extends View{

    private static final int DEFAULT_CIRCLE_RADIUS = 80;
    private static final int DEFAULT_CIRCLE_SOLIDE_COLOR = Color.parseColor("#FFFFFF");
    private static final int DEFAULT_CIRCLE_STROKE_COLOR = Color.parseColor("#3DCBF8");
    private static final int DEFAULT_CIRCLE_STROKE_WIDTH = 3;
    private static final int PROGRESS_COLOR = Color.parseColor("#f18f00");
    private static final int PROGRESS_WIDTH = 3;
    private static final int SMALL_CIRCLE_RADIUS = 4;
    private static final int SMALL_CIRCLE_SOLIDE_COLOR = Color.parseColor("#f18f00");
    private static final int SMALL_CIRCLE_STROKE_COLOR = Color.parseColor("#f18f00");
    private static final int SMALL_CIRCLE_STROKE_WIDTH = 4;
    private static final int TEXT_COLOR = Color.parseColor("#212a32");
    private static final int TEXT_SIZE = 16;
    private long countdownTime;
    private float currentAngle;
    private int defaultCircleRadius = dp2px(80);
    private int defaultCircleSolideColor = DEFAULT_CIRCLE_SOLIDE_COLOR;
    private int defaultCircleStrokeColor = DEFAULT_CIRCLE_STROKE_COLOR;
    private int defaultCircleStrokeWidth = dp2px(3);
    private Paint defaultCriclePaint;
    private float extraDistance = 0.7F;
    private Path mPath;
    private float mStartSweepValue = -90.0F;
    private int progressColor = PROGRESS_COLOR;
    private Paint progressPaint;
    private int progressWidth = dp2px(3);
    private Paint smallCirclePaint;
    private int smallCircleRadius = dp2px(4);
    private int smallCircleSolideColor = SMALL_CIRCLE_SOLIDE_COLOR;
    private Paint smallCircleSolidePaint;
    private int smallCircleStrokeColor = SMALL_CIRCLE_STROKE_COLOR;
    private int smallCircleStrokeWidth = dp2px(4);
    private int textColor = TEXT_COLOR;
    private String textDesc;
    private Paint textPaint;
    private int textSize = sp2px(16);
    private CountDownTimer timer;
    private OnonTickListener ononTickListener ;
    public CountDownProgress(Context paramContext)
    {
        this(paramContext, null);
    }

    public CountDownProgress(Context paramContext, AttributeSet paramAttributeSet)
    {
        this(paramContext, paramAttributeSet, 0);
    }

    public CountDownProgress(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
    {
        super(paramContext, paramAttributeSet, paramInt);
        TypedArray paramContext1 = getContext().obtainStyledAttributes(paramAttributeSet, R.styleable.CountDownProgress);
        int i = paramContext1.getIndexCount();
        paramInt = 0;
        while (paramInt < i)
        {
            int j = paramContext1.getIndex(paramInt);
            switch (j)
            {
                default:
                    break;
                case 11:
                    this.textSize = ((int)paramContext1.getDimension(j, this.textSize));
                    break;
                case 10:
                    this.textColor = paramContext1.getColor(j, this.textColor);
                    break;
                case 9:
                    this.smallCircleStrokeWidth = ((int)paramContext1.getDimension(j, this.smallCircleStrokeWidth));
                    break;
                case 8:
                    this.smallCircleStrokeColor = paramContext1.getColor(j, this.smallCircleStrokeColor);
                    break;
                case 7:
                    this.smallCircleSolideColor = paramContext1.getColor(j, this.smallCircleSolideColor);
                    break;
                case 6:
                    this.smallCircleRadius = ((int)paramContext1.getDimension(j, this.smallCircleRadius));
                    break;
                case 5:
                    this.progressWidth = ((int)paramContext1.getDimension(j, this.progressWidth));
                    break;
                case 4:
                    this.progressColor = paramContext1.getColor(j, this.progressColor);
                    break;
                case 3:
                    this.defaultCircleStrokeWidth = ((int)paramContext1.getDimension(j, this.defaultCircleStrokeWidth));
                    break;
                case 2:
                    this.defaultCircleStrokeColor = paramContext1.getColor(j, this.defaultCircleStrokeColor);
                    break;
                case 1:
                    this.defaultCircleSolideColor = paramContext1.getColor(j, this.defaultCircleSolideColor);
                    break;
                case 0:
                    this.defaultCircleRadius = ((int)paramContext1.getDimension(j, this.defaultCircleRadius));
            }
            paramInt += 1;
        }
        paramContext1.recycle();
        setPaint();
    }

    private void countdownMethod(OnonTickListener paramOnonTickListener)
    {
        this.ononTickListener = paramOnonTickListener ;
        this.timer = new CountDownTimer(this.countdownTime + 1000L, 1000L)
        {
            public void onFinish()
            {
                textDesc = "网络配置成功" ;
                //CountDownProgress.access$202(CountDownProgress.this, "网络配置成功");
                smallCirclePaint.setColor(CountDownProgress.this.getResources().getColor(android.R.color.transparent));
                smallCircleSolidePaint.setColor(CountDownProgress.this.getResources().getColor(android.R.color.transparent));
                invalidate();
            }

            public void onTick(long paramAnonymousLong)
            {
                countdownTime =  countdownTime - 1000L;
                StringBuilder localStringBuilder = new StringBuilder();
                localStringBuilder.append(countdownTime / 1000L);
                localStringBuilder.append("s后配置成功");
                textDesc = localStringBuilder.toString();
                invalidate();
                if (ononTickListener != null) {
                    ononTickListener.onTickListener();
                }
            }
        }.start();
    }

    private void setPaint()
    {
        this.defaultCriclePaint = new Paint();
        this.defaultCriclePaint.setAntiAlias(true);
        this.defaultCriclePaint.setDither(true);
        this.defaultCriclePaint.setStyle(Paint.Style.STROKE);
        this.defaultCriclePaint.setStrokeWidth(this.defaultCircleStrokeWidth);
        this.defaultCriclePaint.setColor(this.defaultCircleStrokeColor);
        this.progressPaint = new Paint();
        this.progressPaint.setAntiAlias(true);
        this.progressPaint.setDither(true);
        this.progressPaint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeWidth(this.progressWidth);
        this.progressPaint.setColor(this.progressColor);
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.smallCirclePaint = new Paint();
        this.smallCirclePaint.setAntiAlias(true);
        this.smallCirclePaint.setDither(true);
        this.smallCirclePaint.setStyle(Paint.Style.FILL);
        this.smallCirclePaint.setStrokeWidth(this.smallCircleStrokeWidth);
        this.smallCirclePaint.setColor(this.smallCircleStrokeColor);
        this.smallCircleSolidePaint = new Paint();
        this.smallCircleSolidePaint.setAntiAlias(true);
        this.smallCircleSolidePaint.setDither(true);
        this.smallCircleSolidePaint.setStyle(Paint.Style.FILL);
        this.smallCircleSolidePaint.setColor(this.smallCircleSolideColor);
        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setDither(true);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setColor(this.textColor);
        this.textPaint.setTextSize(this.textSize);
    }

    protected int dp2px(int paramInt)
    {
        return (int)TypedValue.applyDimension(1, paramInt, getResources().getDisplayMetrics());
    }

    protected void onDraw(Canvas paramCanvas)
    {
        super.onDraw(paramCanvas);
        paramCanvas.save();
        paramCanvas.translate(getPaddingLeft(), getPaddingTop());
        paramCanvas.drawCircle(this.defaultCircleRadius, this.defaultCircleRadius, this.defaultCircleRadius, this.defaultCriclePaint);
        paramCanvas.drawArc(new RectF(0.0F, 0.0F, this.defaultCircleRadius * 2, this.defaultCircleRadius * 2), this.mStartSweepValue, this.currentAngle * 360.0F, false, this.progressPaint);
        float f1 = this.textPaint.measureText(this.textDesc);
        float f2 = (this.textPaint.descent() + this.textPaint.ascent()) / 2.0F;
        paramCanvas.drawText(this.textDesc, this.defaultCircleRadius - f1 / 2.0F, this.defaultCircleRadius - f2, this.textPaint);
        double d = (float)Math.abs((this.currentAngle * 360.0F + this.extraDistance) * 3.141592653589793D / 180.0D);
        f1 = (float)Math.abs(Math.sin(d) * this.defaultCircleRadius + this.defaultCircleRadius);
        f2 = (float)Math.abs(this.defaultCircleRadius - Math.cos(d) * this.defaultCircleRadius);
        paramCanvas.drawCircle(f1, f2, this.smallCircleRadius, this.smallCirclePaint);
        paramCanvas.drawCircle(f1, f2, this.smallCircleRadius - this.smallCircleStrokeWidth, this.smallCircleSolidePaint);
        paramCanvas.restore();
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
        int k = MeasureSpec.getMode(paramInt1);
        int i = MeasureSpec.getMode(paramInt2);
        int j = Math.max(this.defaultCircleStrokeWidth, this.progressWidth);
        if (k != MeasureSpec.EXACTLY) {
            paramInt1 = MeasureSpec.makeMeasureSpec(getPaddingLeft() + this.defaultCircleRadius * 2 + j + getPaddingRight(), MeasureSpec.EXACTLY);
        }
        if (i != MeasureSpec.EXACTLY) {
            paramInt2 = MeasureSpec.makeMeasureSpec(getPaddingTop() + this.defaultCircleRadius * 2 + j + getPaddingBottom(), MeasureSpec.EXACTLY);
        }
        super.onMeasure(paramInt1, paramInt2);
    }

    public void setCountdownTime(long paramLong)
    {
        this.countdownTime = paramLong;
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append(paramLong / 1000L);
        localStringBuilder.append("后配置成功");
        this.textDesc = localStringBuilder.toString();
    }

    protected int sp2px(int paramInt)
    {
        return (int)TypedValue.applyDimension(2, paramInt, getResources().getDisplayMetrics());
    }

    public void startCountDownTime(final OnCountdownFinishListener paramOnCountdownFinishListener, OnonTickListener paramOnonTickListener)
    {
        setClickable(false);
        ValueAnimator localValueAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F });
        localValueAnimator.setDuration(this.countdownTime + 1000L);
        localValueAnimator.setInterpolator(new LinearInterpolator());
        localValueAnimator.setRepeatCount(0);
        localValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
            {
                currentAngle = (float) paramAnonymousValueAnimator.getAnimatedValue();
                invalidate();////实时刷新view，这样我们的进度条弧度就动起来了
            }
        });
        localValueAnimator.start();
        localValueAnimator.addListener(new Animator.AnimatorListener()
        {
            public void onAnimationCancel(Animator paramAnonymousAnimator) {}

            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
                if (paramOnCountdownFinishListener != null) {
                    paramOnCountdownFinishListener.countdownFinished();
                }
                if (countdownTime > 0L)
                {
                    setClickable(true);
                    return;
                }
                setClickable(false);
            }

            public void onAnimationRepeat(Animator paramAnonymousAnimator) {}

            public void onAnimationStart(Animator paramAnonymousAnimator) {}
        });
        countdownMethod(paramOnonTickListener);
    }

    public void stopCountDown()
    {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    public static abstract interface OnCountdownFinishListener
    {
        public abstract void countdownFinished();
    }

    public static abstract interface OnonTickListener
    {
        public abstract void onTickListener();
    }
}
