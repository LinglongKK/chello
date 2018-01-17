package cck.com.chello;

import android.util.Log;

/**
 * Created by chenlong on 17-12-13.
 */

public class FinalJavaCall extends JavaCall{

    @Override
    void setCount(int c) {
        Log.d("chenlong","call final Java Call");
        this.count = c;
    }
    void setCC(int c){
        Log.d("chenlong","setCC in FinalJavaCall ,c="+c);
        this.count = c;
    }
}
