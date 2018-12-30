
package com.example.livenessproject.view;

import com.megvii.livenessproject.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

public class CircleProgressBar extends View {

    private final TextPaint textPaint;
    private int progress = 100;
    private int max = 100;
    private Paint paint;
    private RectF oval;
    private int Width = 20;
    private int Radius = 75;
    private Bitmap bit;

    SweepGradient sweepGradient = null;

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int use = width > height ? height : width;
        int sum = Width + Radius;
        Width = use / 2 * Width/ sum;
        Radius = use / 2 * Radius/ sum;
        setMeasuredDimension(use, use);
    }

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        oval = new RectF();
        textPaint = new TextPaint();
        bit =BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        sweepGradient = new SweepGradient(getWidth()/2, getHeight()/2, new int[]{0xfffe9a8e, 0xff3fd1e4
                ,0xffdc968e},null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        oval.set(0,0,getWidth(), getHeight());

//        canvas.drawBitmap(bit,null, oval, paint);
        paint.setAntiAlias(true);
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xff000000);
        paint.setStrokeWidth(Width);// 设置画笔宽度
        paint.setStyle(Paint.Style.STROKE);// 设置中空的样�?
        canvas.drawCircle(Radius + Width, Radius + Width, Radius, paint);// 在中心为�?00,100）的地方画个半径�?5的圆，宽度为setStrokeWidth�?0，也就是灰色的底�?



//        paint.setShader(sweepGradient);
        paint.setColor(0xff3fd1e4);// 设置画笔为绿�?
        oval.set(Width, Width, Radius * 2 + Width, (Radius * 2 + Width));// 设置类似于左上角坐标�?5,45），右下角坐标（155,155），这样也就保证了半径为55
        canvas.drawArc(oval, -90, ((float) progress / max) * 360, false, paint);// 画圆弧，第二个参数为：起始角度，第三个为跨的角度，第四个为true的时候是实心，false的时候为空心
        paint.reset();
//        textPaint.setStrokeWidth(3);
//        textPaint.setTextSize(getMeasuredWidth() / 3);// 设置文字的大�?
//        textPaint.setColor(0xff318deb);// 设置画笔颜色
//        textPaint.setTextAlign(Paint.Align.CENTER);
//        FontMetrics fontMetrics= textPaint.getFontMetrics();
//        float baseX = Radius + Width;
//        float baseY = Radius + Width;
//        float textHeight = fontMetrics.descent - fontMetrics.ascent;
//        if (progress == max) {
//            // canvas.drawText("完成", baseX, baseY - fontMetrics.descent
//            // + (textHeight) / 2, paint);
//            if (bit!=null)
//            canvas.drawBitmap(bit, Radius + Width - bit.getHeight() / 2,
//                    Radius + Width - bit.getHeight() / 2, paint);
//        } else {
//            canvas.drawText(progress * 100 / max + "%", baseX, baseY - fontMetrics.descent
//                    + (textHeight) / 2, textPaint);
//
//        }
    }
}
