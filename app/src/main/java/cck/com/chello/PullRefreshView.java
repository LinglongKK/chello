package cck.com.chello;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;

import cck.com.chello.utils.LogV;

/**
 * Created by chenlong on 18-1-10.
 */

public class PullRefreshView extends LinearLayout {
    enum Direction {
        DOWN,UP
    }
    enum State {
        REFRESHING,IDLE
    }
    private LinearLayout mContainer;
    private ListView mListView;
    private View mHeaderView;
    private IPullRefreshListener mRefreshListener;
    private float mDensity;
    private int mMaxHeaderHeight;
    private int mFinalHeaderHeight;
    private float mLastX;
    private float mLastY;
    private boolean mHasMoveFlag = false;
    private MotionEvent mLastMoveEvent;
    private Direction mDirection;
    private State mState = State.IDLE;


    public PullRefreshView(@NonNull Context context) {
        super(context);
        init();
    }

    public PullRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullRefreshView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        mHeaderView = LayoutInflater.from(getContext()).inflate(R.layout.refresh_header,this,false);
        mListView = new ListView(getContext());
        addView(mHeaderView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
        addView(mListView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mDensity = displayMetrics.density;
        mMaxHeaderHeight = (int)(mDensity * displayMetrics.widthPixels * 0.6 );
        mFinalHeaderHeight = (int)(mDensity * 60);
    }

    public ListView getListView() {
        return mListView;
    }

    public void refreshFinish() {
        release(true);
    }

    public void setPullRefreshListener(IPullRefreshListener listener){
        this.mRefreshListener = listener;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogV.d("dispatchTouchEvent:"+MotionEvent.actionToString(ev.getAction()));
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getX();
                mLastY = ev.getY();
                mHasMoveFlag = false;
                super.dispatchTouchEvent(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = ev;
                float diffX = ev.getX() - mLastX;
                float diffY = ev.getY() - mLastY;
                mDirection = diffY > 0 ? Direction.DOWN :Direction.UP;
                mLastX = ev.getX();
                mLastY = ev.getY();
                LogV.d("diffX:"+diffX+",diffY:"+diffY);
                if(Math.abs(diffY) <= Math.abs(diffX)) return super.dispatchTouchEvent(ev);
                if(!mListView.canScrollList(-(int)diffY) || shouldIntercept()) {
                    LogV.d("dispatch move");
                    mHasMoveFlag = true;
                    moveY(diffY);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                release(false);
                if(mHasMoveFlag){
                    sendCancelEvent();
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean shouldIntercept() {
        LayoutParams params = (LayoutParams)mHeaderView.getLayoutParams();
        return params.height > 0;
    }

    private void moveY(float diffY) {
        LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
        int originHeight = params.height;
        originHeight += (int)diffY * 0.8;
        if(originHeight < 0) originHeight = 0;
        if(originHeight >= mMaxHeaderHeight) originHeight = mMaxHeaderHeight;
        params.height = originHeight;
        mHeaderView.setLayoutParams(params);
    }

    private void release(boolean force) {
        int height = mHeaderView.getLayoutParams().height;
        if(height <= 0) return ;
        int finalY = mFinalHeaderHeight;

        if((height < mFinalHeaderHeight && mDirection == Direction.DOWN)
                || mDirection == Direction.UP
                || force) {
            finalY = 0;
            mState = State.IDLE;
        }else{
            mState = State.REFRESHING;
        }
        final ValueAnimator objectAnimator = ObjectAnimator.ofFloat(height,finalY);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.setDuration(100L);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float height = (float)animation.getAnimatedValue();
                LogV.d("height:"+height);
                LayoutParams params = (LayoutParams) mHeaderView.getLayoutParams();
                params.height = (int)height;
                mHeaderView.setLayoutParams(params);
            }
        });
        objectAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                objectAnimator.removeAllUpdateListeners();
                objectAnimator.removeListener(this);
                if(mState == State.REFRESHING && mRefreshListener != null){
                    mRefreshListener.onRefresh();
                }
            }
        });
        objectAnimator.start();
    }

    private boolean dispatchTouchEventSupper(MotionEvent event) {
        return super.dispatchTouchEvent(event);
    }

    private void sendCancelEvent() {
        // The ScrollChecker will update position and lead to send cancel event when mLastMoveEvent is null.
        // fix #104, #80, #92
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    static void logMotionEvent(MotionEvent event) {
        LogV.d("action:"+MotionEvent.actionToString(event.getAction())
                +",x:"+event.getX()
                +",y:"+event.getY());
    }

    static interface IPullRefreshListener {
        void onRefresh();
    }
}
