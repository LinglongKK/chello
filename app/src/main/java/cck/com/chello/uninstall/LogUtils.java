package cck.com.chello.uninstall;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author zhoushengtao
 */
public class LogUtils {

    public final static String ACTION_CRASH_SEND = "action.crash.send";
    public final static String DATA_THROWABLE = "data.throwable";
    public final static String DATA_TAG = "data.tag";

//     public final static boolean DEBUG = BuildConfig.DEBUG;

	/*
     * 为保持兼容，此处的的DEBUG常量不再使用，在新的日志系统中另外控制日志的输出
	 * 在com.qihoo.haosou.QihooApplication的onCreate中修改Log.setDebugLevel(Log.LEVEL_DEBUG)这行即可控制日志。
	 * 调试模式设置参数为Log.LEVEL_DEBUG，正式发布一般设置为Log.LEVEL_INFO，要屏蔽所有日志调用，则设置为Log.LEVEL_NONE
	 */
//    public final static boolean DEBUG = true;

    //与日志分开的调试模式可在此处设置
    public static boolean isDebug() {
        return sIsDebug;
    }

    public static String customTagPrefix = "";

    private LogUtils() {
    }

    static public void SetDebugEnable(Context cxt) {

    }

    private static boolean sIsDebug = false;
    public static boolean allowD = true;
    public static boolean allowE = true;
    public static boolean allowI = true;
    public static boolean allowV = true;
    public static boolean allowW = true;
    public static boolean allowWtf = true;
    public static boolean sQueryLogSwitch = false;

    public static String generateTag(StackTraceElement caller) {
        String tag = "%s[%s, %d]";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : customTagPrefix + ":" + tag;
        return tag;
    }

    public static StackTraceElement getStackTrace(int level) {
        return Thread.currentThread().getStackTrace()[level];
    }

    /**
     * 默认TAG是类名+方法名
     *
     * @param content
     */
    public static void d(String content) {
        if (!allowD) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.d(tag, content);
    }

    public static void d(String content, Throwable tr) {
        if (!allowD) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
//        Log.d(tag, content, tr);
        Log.e(tag, content, tr);
    }

    /**
     * 指定TAG
     *
     * @param tag
     */
    public static void d(String tag, String content) {
        if (!allowD) {
            return;
        }
        Log.d(tag, content);
    }



    public static void e(String content) {
        if (!allowE) {
            return;
        }
        if (content == null) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.e(tag, content);
    }

    public static void e(Throwable content) {
        if (!allowE)
            return;
        if (content == null) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Throwable cause = content.getCause();
        if (cause == null) {
            if (content.getMessage() != null) {
                Log.e(tag, content.getMessage());
            } else {
                Log.e(tag, "" + content);
            }
        } else {
            if (content.getMessage() != null) {
                e(content.getMessage(), cause);
            } else {
                e("msearch", cause);
            }
        }

    }

// TODO Remove unused code found by UCDetector
//     public static void e(String tag,String content, Throwable tr) {
//         Log.e(tag, content, tr);
//     }

    public static void e(String tag, Throwable tr) {
        if (!allowE) {
            return;
        }
        if (tr == null) {
            return;
        }
        Log.e(tag, tr.getMessage(), tr);
    }

    /**
     * 指定TAG
     *
     * @param tag
     */
    public static void e(String tag, String content) {
        if (!allowD) {
            return;
        }
        Log.e(tag, content);
    }

    public static void i(String content) {
        if (!allowI) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.i(tag, content);
    }

    public static void i(String content, Throwable tr) {
        if (!allowI) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
//        Log.i(tag, content, tr);
        Log.e(tag, content, tr);
    }

    /**
     * 指定TAG
     *
     * @param tag
     */
    public static void i(String tag, String content) {
        if (!allowD) {
            return;
        }
        Log.i(tag, content);
    }

// TODO Remove unused code found by UCDetector
//     public static void v(String content) {
//         if (!allowI) {
//             return;
//         }
//         StackTraceElement caller = getCallerStackTraceElement();
//         String tag = generateTag(caller);
//         Log.v(tag, content);
//     }

    public static void v(String content, Throwable tr) {
        if (!allowV) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
//        Log.v(tag, content, tr);
        Log.e(tag, content, tr);
    }

    /**
     * 指定TAG
     *
     * @param tag
     */
    public static void v(String tag, String content) {
        if (!allowD) {
            return;
        }
        Log.v(tag, content);
    }

    public static void w(String content) {
        if (!allowV) {
            return;
        }
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
        Log.w(tag, content);
    }

    public static void w(String content, Throwable tr) {
        if (!allowW)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
//        Log.w(tag, content, tr);
        Log.e(tag, content, tr);
    }

    /**
     * 指定TAG
     *
     * @param tag
     */
    public static void w(String tag, String content) {
        if (!allowD) {
            return;
        }
        Log.w(tag, content);
    }

// TODO Remove unused code found by UCDetector
//     public static void w(Throwable tr) {
//         if (!allowW)
//             return;
//         StackTraceElement caller = getCallerStackTraceElement();
//         String tag = generateTag(caller);
// //        Log.w(tag, tr);
//         Log.e(tag, tr);
//     }

    public static void wtf(String content) {
        if (!allowWtf)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
//        Log.wtf(tag, content);
        Log.e(tag, content);
    }

    public static void wtf(String content, Throwable tr) {
        if (!allowWtf)
            return;
        StackTraceElement caller = getCallerStackTraceElement();
        String tag = generateTag(caller);
//      Log.wtf(tag, content, tr);
        Log.e(tag, content, tr);
    }

// TODO Remove unused code found by UCDetector
//     public static void wtf(Throwable tr) {
//         if (!allowWtf)
//             return;
//         StackTraceElement caller = getCallerStackTraceElement();
//         String tag = generateTag(caller);
// //        Log.wtf(tag, tr);
//         Log.e(tag, tr);
//     }

// TODO Remove unused code found by UCDetector
//     public static StackTraceElement getCurrentStackTraceElement() {
//         return Thread.currentThread().getStackTrace()[3];
//     }


    public static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    public static class FunctionTracer {
        private long timeStart = 0;
        private String functionName;
        private String shortFunctionName;
        private long timeLastKick = 0;
        private long lastKickLine = 0;
        private String tag;

        public FunctionTracer() {
            this("FunctionTracer");
        }

        public FunctionTracer(String tag) {
            if (!isDebug())
                return;

            this.tag = tag;
            timeStart = System.currentTimeMillis();
            timeLastKick = timeStart;
            StackTraceElement stackTraceElem = Thread.currentThread().getStackTrace()[4];
            lastKickLine = stackTraceElem.getLineNumber();
            functionName = stackTraceElem.toString();

            String className = stackTraceElem.getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
            shortFunctionName = String.format("%s.%s", className, stackTraceElem.getMethodName());
            Log.i(tag, "进入 " + functionName);
        }

        static public FunctionTracer enter() {
            return new FunctionTracer();
        }

// TODO Remove unused code found by UCDetector
//         static public FunctionTracer enter(String tag)
//         {
//             return new FunctionTracer(tag);
//         }

        public void kick() {
            if (!isDebug())
                return;

            StackTraceElement currentStackTraceElem = Thread.currentThread().getStackTrace()[3];
            int currentLine = currentStackTraceElem.getLineNumber();
            long duration = System.currentTimeMillis() - timeLastKick;
            timeLastKick = System.currentTimeMillis();
            Log.i(tag, shortFunctionName + ": 从" + lastKickLine + "行到" + currentLine + "行，用时" + duration + "毫秒");
            lastKickLine = currentLine;
        }

        public void leave() {
            if (!isDebug())
                return;

            Log.i(tag, "离开 " + functionName + "，函数运行了" + (System.currentTimeMillis() - timeStart) + "毫秒");
        }
    }

    static private Boolean isProductPack = null;

    static public boolean isProductionPackage() {
        if (isProductPack == null) {
            isProductPack = true;
            try {
                do {
                    Class<?> appClass = Class.forName("com.qihoo.haosou.msearchpublic.AppGlobal");
                    if (appClass == null) {
                        break;
                    }
                    Method getInstanceMethod = appClass.getMethod("getBaseApplication");
                    if (getInstanceMethod == null) {
                        break;
                    }
                    Application application = (Application) getInstanceMethod.invoke(null);
                    if (application == null) {
                        break;
                    }
                } while (false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (isProductPack == null) {
            return true;
        }

        return isProductPack;
    }

    public static void ASSERT(boolean b) {
        ASSERT(b, "ASSERT FAILED");
    }

    public static void ASSERT(boolean b, String msg) {
        /*if (isProductionPackage())
            return;

        if (!b)
            throw new AssertionError(msg);*/
    }



    private final static String DEFAULT_UPLOAD_TAG = "MyException";

    private static void upload(String tag, Throwable th) {
        Intent intent = new Intent(ACTION_CRASH_SEND);
        if (TextUtils.isEmpty(tag))
            tag = DEFAULT_UPLOAD_TAG;

        intent.putExtra(DATA_THROWABLE, th);
        intent.putExtra(DATA_TAG, tag);
        AppGlobal.getBaseApplication().sendBroadcast(intent);
    }

    private static void upload(Throwable th) {
        upload(null, th);
    }

    public static class UnkonwRuntimeException extends RuntimeException {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        public UnkonwRuntimeException(String msg) {
            // TODO Auto-generated constructor stub
            super(msg);
        }

    }

// TODO Remove unused code found by UCDetector
//     /**
//      * 调用LogUtil.e并且上传日志
//      * @param msg
//      */
//     public static void u(String msg){
//     	e(msg);
//     	upload(new UnkonwRuntimeException(msg));
//     }


// TODO Remove unused code found by UCDetector
//     public static void u(String tag,String msg){
//     	e(tag,msg);
//     	upload(tag,new UnkonwRuntimeException(msg));
//     }

// TODO Remove unused code found by UCDetector
//     public static void u(Throwable th){
//     	e(th);
//     	upload(th);
//     }

// TODO Remove unused code found by UCDetector
//     public static void u(String tag,Throwable th){
//     	e(tag,th);
//     	upload(tag,th);
//     }

}
