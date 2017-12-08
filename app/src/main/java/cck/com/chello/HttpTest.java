package cck.com.chello;

/**
 * Created by chenlong on 17-12-6.
 */

public class HttpTest {
    public native void testCurl();
    static {
        System.loadLibrary("httpUtil");
    }
}
