package cck.com.chello;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.Choreographer;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ViewAnimator;

import cck.com.chello.utils.LogV;
import cck.com.chello.utils.MaskView;


/**
 * Created by chenlong on 17-12-28.
 */

public class AnimationActivity extends Activity{
    private View target;
    private View car;
    private Path path = new Path();
    private PathMeasure pathMeasure = new PathMeasure();
    private ValueAnimator viewAnimator;
    private MaskView maskView;
    InterceptorView interceptorView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_activity_layout);
        target = findViewById(R.id.target);
        car = findViewById(R.id.car);
        findViewById(R.id.begin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test3(v);
            }
        });
        maskView = new MaskView(this);
        interceptorView = findViewById(R.id.intercptor_view);
//        interceptorView.setInterpolator(new AnticipateInterpolator(0.5f));
//        interceptorView.setInterpolator(new AccelerateDecelerateInterpolator());
//        interceptorView.setInterpolator(new BInterpolator(new float[]{1.0F,0.0F}));
//        interceptorView.setInterpolator(PathInterpolatorCompat.create(1.0F,0f));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(viewAnimator != null) viewAnimator.cancel();
    }



    private void startAnimation() {
        final int[] targetPos = new int[2];
        final int[] carPos = new int[2];
        target.getLocationOnScreen(targetPos);
        car.getLocationOnScreen(carPos);
        path.rewind();
        maskView.reset();
        path.moveTo(targetPos[0],targetPos[1]);
        path.quadTo(targetPos[0]+400,targetPos[1]-100,carPos[0],carPos[1]);
        path.lineTo(carPos[0],carPos[1]);
        pathMeasure.setPath(path,false);
        viewAnimator = ObjectAnimator.ofInt(0,1);
        viewAnimator.setDuration(4000);
        viewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float distance = animation.getAnimatedFraction() * pathMeasure.getLength();
                float[] pos = new float[2];
                pathMeasure.getPosTan(distance,pos,null);
                LogV.d("x:"+pos[0]+",y:"+pos[1]);
                float diffX = pos[0]-targetPos[0];
                float diffY = pos[1]-targetPos[1];
                target.setTranslationX(diffX);
                target.setTranslationY(diffY);
                maskView.setCircleXY(pos[0],pos[1]);
            }
        });
        viewAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                target.setTranslationX(0);
                target.setTranslationY(0);
            }
        });
        View view = getWindow().getDecorView();
        if(view instanceof ViewGroup) {
            ((ViewGroup) view).addView(maskView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        viewAnimator.start();


    }

    private void postCount(final long last) {
        Choreographer.getInstance().postFrameCallback(new Choreographer.FrameCallback() {
            @Override
            public void doFrame(long frameTimeNanos) {
                long diff = frameTimeNanos - last;
                LogV.d("frameTime:"+diff);
                postCount(frameTimeNanos);
            }
        });
    }

    private void test2(View v) {
        final int x = (int)target.getX();
        final int y = (int)target.getY();

        final ValueAnimator objectAnimator = ObjectAnimator.ofObject(
                null,
                new Point(x,y),
                new Point(351,636),
                new Point(x+700,y+800));
        //
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                Point current = (Point)animation.getAnimatedValue();
                target.setX(current.x);
                target.setY(current.y);
                maskView.setCircleXY(current.x+target.getMeasuredWidth()/2,current.y+target.getMeasuredHeight()/2);
                float fraction = animation.getAnimatedFraction();
                LogV.d("fraction total->"+fraction);
                if(fraction >= 0.5F) {
                    LogV.d("half x:"+current.x+",y:"+current.y);
                }
            }
        });
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                target.setX(x);
                target.setY(y);
                objectAnimator.removeAllUpdateListeners();
                objectAnimator.removeListener(this);
            }
        });
        maskView.reset();
        objectAnimator.setEvaluator(new TestIntTypeEvaluator(objectAnimator));
        objectAnimator.setDuration(1000);
        ViewParent view = target.getParent();
        if(view instanceof ViewGroup) {
            ((ViewGroup) view).addView(maskView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    private void test1(View view) {
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0,1,2,3);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setEvaluator(new CIntTypeEvaluator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                LogV.d("fraction:"+animation.getAnimatedFraction());
            }
        });
        valueAnimator.start();
//        Choreographer choreographer = Choreographer.getInstance();
//        choreographer.postFrameCallback(new Choreographer.FrameCallback() {
//            @Override
//            public void doFrame(long frameTimeNanos) {
//                postCount(frameTimeNanos);
//            }
//        });

    }

    private void test3(View view){
        interceptorView.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(target,"translationX",0.0f,500f);
        objectAnimator.setInterpolator(new LinearInterpolator());
//        objectAnimator.setInterpolator(new AccelerateInterpolator());
//        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            private float lastX;
            private float lastT;
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float tranX = (float)animation.getAnimatedValue();
                float diffX = tranX - lastX;
                float time = animation.getDuration() * animation.getAnimatedFraction();
                float diffT = time - lastT;

                float speed = diffX/diffT;

                lastX = tranX;
                lastT = time;
                LogV.d("speed:"+(diffT));
                interceptorView.addPoint(new float[]{time,tranX});
            }
        });
        objectAnimator.start();
    }

    private static class TestIntTypeEvaluator implements TypeEvaluator<Point> {
        public long duration;
        private ValueAnimator valueAnimator;
        public TestIntTypeEvaluator(ValueAnimator valueAnimator) {
            this.duration = valueAnimator.getDuration();
            this.valueAnimator = valueAnimator;
        }

        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            LogV.d("fraction->"+fraction);
            float totalFraction = valueAnimator.getAnimatedFraction();
            int Sx = endValue.x -startValue.x;
            float Vx = Sx/duration;
            float accelearte = 2*(endValue.y - startValue.y)/duration*duration;

            Point point = new Point();

            float x = startValue.x + (endValue.x - startValue.x) * fraction;
            float y = startValue.y + (endValue.y - startValue.y) * fraction * fraction;
            point.set((int)x,(int)y);
            return point;
        }
    }

    private static class CIntTypeEvaluator implements TypeEvaluator<Integer>{

        @Override
        public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
            int startInt = startValue;
            int res = (int)(startInt + fraction * (endValue - startInt));
            LogV.d("fraction:"+fraction+",start:"+startValue+",end:"+endValue);
            return res;
        }
    }

    private static class CInterceptor implements TimeInterpolator {

        @Override
        public float getInterpolation(float input) {
            LogV.d("before");
            return input;
        }
    }

}
