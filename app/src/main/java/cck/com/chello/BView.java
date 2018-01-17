package cck.com.chello;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

/**
 * Created by chenlong on 17-12-28.
 */

public class BView extends View{
    Path path = new Path();
    Paint paint = new Paint();
    PathMeasure pathMeasure = new PathMeasure();
    ValueAnimator valueAnimator;
    float[] pos = new float[2];
    Point start = new Point();
    Point end = new Point();
    Point control = new Point();
    public BView(Context context) {
        super(context);
        init(context);
    }

    public BView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        valueAnimator = ValueAnimator.ofInt(0,1);
        valueAnimator.setDuration(1000 * 4);
        start.set(100,100);
        end.set(400,400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fraction = valueAnimator.getAnimatedFraction();
                float distance = pathMeasure.getLength() * fraction;
                pathMeasure.getPosTan(distance,pos,null);
                invalidate();
            }
        });

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        valueAnimator.cancel();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(path,paint);
        canvas.drawLine(start.x,start.y,control.x,control.y,paint);
        canvas.drawLine(end.x,end.y,control.x,control.y,paint);
        canvas.drawCircle(pos[0],pos[1],4,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
            control.set((int)event.getX(),(int)event.getY());
            if(valueAnimator.isRunning()) valueAnimator.cancel();
            path.rewind();
            path.moveTo(start.x,start.y);
            path.quadTo(control.x,control.y,end.x,end.y);
            pathMeasure.setPath(path,false);
            pos[0] = start.x;
            pos[1] = start.y;
            invalidate();
            return true;
        } else if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            valueAnimator.start();
            return true;
        } else{

            return super.onTouchEvent(event);
        }
    }
}
