package cck.com.chello;

import android.util.Log;

/**
 * Created by chenlong on 17-12-13.
 */

public abstract class JavaCall {
    int count;
    @CallByNative
    public void setCurrentCount(int c) {
        this.count = c;
    }
    public int getCurrentCount() {
        return this.count;
    }

    abstract void setCount(int c);

    void setCC(int c){
        Log.d("chenlong","setCC in JavaCall");
        this.count = c;
    }
}
