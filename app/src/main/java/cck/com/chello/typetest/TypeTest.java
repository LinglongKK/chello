package cck.com.chello.typetest;

/**
 * Created by chenlong on 17-12-1.
 */

public class TypeTest {

    static {
        System.loadLibrary("typeTest");
    }

    public native int getIntValue(int var);
    public native boolean getBooleanValue(boolean var);
    public native byte getByteValue(byte var);
    public native short getShortValue(short value);
    public native char getCharValue(char var);
    public native double getDoubleValue(double var);
    public native float getFloatValue(float var);
    public native long getLongValue(long var);
    public native void setInstance();
    public static native void logNative(String format,String msg);
}
