package cck.com.chello;

import android.util.Log;

/**
 * Created by chenlong on 17-12-13.
 */

public class Final2JavaCall extends JavaCall{

    @Override
    void setCount(int c) {
        Log.d("chenlong","call final 2 Java Call");
        this.count = c;
    }
}
