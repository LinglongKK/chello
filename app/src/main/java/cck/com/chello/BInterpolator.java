package cck.com.chello;

import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.view.animation.Interpolator;

import cck.com.chello.utils.LogV;

/**
 * Created by chenlong on 17-12-29.
 */

public class BInterpolator implements Interpolator{
    private float[] control;
    private PathMeasure pathMeasure;
    private Interpolator wrapper;

    public BInterpolator(){
        this.control = new float[] {0.5F,0.5F};
        init();
    }

    public BInterpolator(float[] control) {
        this.control = control;
        this.wrapper = PathInterpolatorCompat.create(control[0],control[1]);
        init();
    }


    private void init() {
        Path path = new Path();
        path.moveTo(0,0);
        path.quadTo(control[0],control[1],1.0F,1.0F);
        pathMeasure = new PathMeasure(path,false);
    }

    public float[] getControl() {
        return control;
    }

    @Override
    public float getInterpolation(float input) {
        float wrapp = this.wrapper.getInterpolation(input);
//        float distance = pathMeasure.getLength() *input;
        float point[] = new float[2];
        pathMeasure.getPosTan(input,point,null);
        LogV.d("input:"+input+",fraction:"+point[1]+",wr:"+wrapp);
        return point[1];
    }
}
