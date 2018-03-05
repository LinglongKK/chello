package cck.com.chello.utils;

import android.util.Log;

/**
 * Created by chenlong on 17-12-18.
 */

public class LogV {
    private static final String TAG = "chenlong";
    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void e(Throwable e) {
        Log.d(TAG,e.getMessage());
    }
}
