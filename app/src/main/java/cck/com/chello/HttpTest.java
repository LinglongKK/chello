package cck.com.chello;

import android.util.Log;

/**
 * Created by chenlong on 17-12-6.
 */

public class HttpTest {
    private FinalJavaCall mCall;
    public HttpTest() {
        this.mCall = new FinalJavaCall();
    }


    @CallByNative
    public void emptyMethod(){
        Log.d("chenlong","1111");
    }

    @CallByNative
    public FinalJavaCall getCallObj() {
        return mCall;
    }

    public native void testJavaCall();
    public native void testCurl(String keyword);
    public native void testArray();
    public native void testNativeMemory();
    public native void testFreeNativeMemory();
    public native void testGlobalRef();
    public native void testFreeGlobalRef();
    public static native void init(String base);
    public native void testHttpClient(String path);

    static {
        System.loadLibrary("httpUtil");
    }
}
