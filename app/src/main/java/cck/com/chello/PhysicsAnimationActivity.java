package cck.com.chello;

import android.app.Activity;
import android.os.Bundle;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import cck.com.chello.utils.LogV;

/**
 * Created by chenlong on 18-1-9.
 */

public class PhysicsAnimationActivity extends Activity{
    View mBallView;
    Button mGoBtn;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.physics_animation_activity_layout);
        mBallView = findViewById(R.id.ball);
        mGoBtn = findViewById(R.id.click);
        mGoBtn.setOnClickListener(this::onClickGoBtn);
        mBallView.setOnTouchListener(new View.OnTouchListener() {
           private float downX;
           private float downY;
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
               if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                   downX = motionEvent.getRawX();
                   downY = motionEvent.getRawY();
                   LogV.d("downX:"+downX+",downY:"+downY);
                   return true;
               }else if(motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                   float moveX = motionEvent.getRawX() - downX;
                   float moveY = motionEvent.getRawY() - downY;
                   LogV.d("moveY:"+moveY+",moveX:"+moveX);
                   mBallView.setTranslationX(moveX);
                   mBallView.setTranslationY(moveY);
                   return true;
               } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                   SpringAnimation springAnimationX = new SpringAnimation(mBallView,DynamicAnimation.TRANSLATION_X);
                   SpringAnimation  springAnimationY = new SpringAnimation(mBallView, DynamicAnimation.TRANSLATION_Y);
                   springAnimationX.setStartVelocity(5000);
                   springAnimationY.setStartVelocity(5000);
                   springAnimationX.animateToFinalPosition(0);
                   springAnimationY.animateToFinalPosition(0);
                   return true;
               }
               return false;
           }
       });
    }

    private void onClickGoBtn(View v) {
        final SpringAnimation  springAnimation = new SpringAnimation(mBallView, DynamicAnimation.TRANSLATION_Y,0);
        springAnimation.setStartVelocity(5000);
        springAnimation.start();

    }
}
