package cck.com.chello.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ViewAnimator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenlong on 17-12-28.
 */

public class MaskView extends View {
    private Paint paint;
    private List<float[]> points = new ArrayList<>();
    public MaskView(Context context) {
        super(context);
        init(context);
    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLUE);
    }

    public void setCircleXY(float x,float y) {
        points.add(new float[]{x,y});
        invalidate();
    }

    public void reset() {
        points.clear();
        invalidate();
        ViewParent viewParent;
        if((viewParent = getParent()) != null) {
            ((ViewGroup)viewParent).removeView(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(float[] point : points) {
            canvas.drawCircle(point[0],point[1],3,paint);
        }
    }
}
