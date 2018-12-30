package com.example.livenessproject.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by binghezhouke on 15-1-14.
 */
public class RotaterView extends View{
    public RotaterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mRectF = new RectF();
    }

    private int paintWidth = -1;
    private int mMax = 100;
    private int progress = 10;
    private Paint mPaint;
    private RectF mRectF;
    private int mColor = 0xffff0000;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int mProgress) {
        this.progress = mProgress;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        int use = width>height?height:width;
        setMeasuredDimension(use, use);

        paintWidth = use/2/30;

        mRectF.set(paintWidth, paintWidth, use - paintWidth, use - paintWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(paintWidth);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(mRectF, -90, ((float) progress / mMax) * 360, false, mPaint);
        mPaint.reset();
    }
    public void setColour(int colour)
    {
        this.mColor = colour;
    }
}
