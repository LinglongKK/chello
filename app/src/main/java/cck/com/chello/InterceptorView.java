package cck.com.chello;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlong on 17-12-29.
 */

public class InterceptorView extends View{

    Paint paint;
    Paint redPaint;
    Interpolator interpolator;
    PointF controlPoint;
    PointF controlPointDraw;
    float controlPointRadius;
    int height,width;
    int minHeight,minWidth;
    float density;
    int point_num = 100;
    int defaultPadding;
    float fractions[];

    List<float[]> points = new ArrayList<>();
    ViewConfiguration configure;
    public InterceptorView(Context context) {
        super(context);
        init(context);
    }

    public InterceptorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InterceptorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        density = context.getResources().getDisplayMetrics().density;
        configure = ViewConfiguration.get(context);
        minHeight = (int)(60 * density);
        minWidth = (int)(60 * density);
        defaultPadding = (int) (4 * density);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(2);
        fractions = new float[point_num];
        for(int i =0;i<point_num;i++) {
            fractions[i] = (i+1)/(float)point_num;
        }
        this.controlPoint = new PointF(0.5f,0.5f);
        this.controlPointDraw = new PointF();
        this.controlPointRadius = density * 4;
        this.interpolator = PathInterpolatorCompat.create(controlPoint.x,controlPoint.y);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获得宽高测量模式和大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 保存测量结果
        if (widthMode == MeasureSpec.EXACTLY) {
            // 宽度
            width = widthSize;
        } else {
            // 宽度加左右内边距
            width = this.minWidth+ getPaddingLeft() + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                // 取小的那个
                width = Math.min(width, widthSize);
            }
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            // 高度
            height = heightSize;
        } else {
            // 高度加左右内边距
            height = this.minWidth + getPaddingTop() + getPaddingBottom();
            if (heightMode == MeasureSpec.AT_MOST) {
                // 取小的那个
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        controlPointDraw.set(controlPoint.x * (float) width,
                (1-controlPoint.y )* (float) height);
    }

    public void setInterpolator(Interpolator interpolator) {
        this.interpolator = interpolator;
        invalidate();
    }

    public void addPoint(float[] point) {
        points.add(point);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#e5e5e5"));
        canvas.scale(0.95F,0.95F,width/2.0f,height/2.0f);
        canvas.drawLine(0,0,0,height,paint);
        canvas.drawLine(0,0.75F*height,width,0.75F*height,paint);
//        if(interpolator != null) {
//            for(int i =0;i<point_num;i++) {
//                canvas.drawPoint(fractions[i] * width,height-interpolator.getInterpolation(fractions[i]) * height,paint);
//            }
//        }
//
//        canvas.drawLine(0,height,controlX,controlY,redPaint);
//        canvas.drawLine(controlX,controlY,width,0,redPaint);
//        canvas.drawCircle(controlPointDraw.x,controlPointDraw.y,controlPointRadius, redPaint);
        for(float[] point:points) {
            canvas.drawPoint(point[0],transY(point[1]),paint);
        }
    }

    private float transY(float y) {
        float y0 = height * 0.75F;
        return y0 - y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
