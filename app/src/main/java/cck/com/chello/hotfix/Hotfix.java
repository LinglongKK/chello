package cck.com.chello.hotfix;

import java.io.File;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cck.com.chello.App;
import cck.com.chello.utils.LogV;
import cck.com.chello.utils.ReflectUtils;
import dalvik.system.DexClassLoader;

/**
 * Created by chenlong on 18-1-17.
 */

public class Hotfix {
    private static volatile Hotfix instance;
    private File mHotFixRoot;
    private DexClassLoader mHotfixClassLoader;
    private Hotfix () {}
    public static Hotfix getInstance() {
        if(instance == null) {
            synchronized (Hotfix.class){
                if(instance == null){
                    instance = new Hotfix();
                }
            }
        }
        return instance;
    }
    public void init(String hotfixPath){
        LogV.d("init");
        mHotFixRoot =  new File(App.getApp().getFilesDir(),"hotfix");
        String opPath = mHotFixRoot.getAbsolutePath()+"/op";
        String soPath = mHotFixRoot.getAbsolutePath()+"/so";
        mHotfixClassLoader = new DexClassLoader(hotfixPath,opPath,soPath,App.getApp().getClassLoader().getParent());
    }

    public void loadHotFix(String className,String methodName,Class<?>... params) {
        if(mHotfixClassLoader == null) return ;
        Class fixClass = null;
        Class originClass = null;

        try{
            fixClass = mHotfixClassLoader.loadClass(className);
            originClass = Class.forName(className);
        }catch (ClassNotFoundException e){
            LogV.e(e);
        }

        if(fixClass != null && originClass != null) {
            Method origin = ReflectUtils.getDeclaredMethod(originClass,methodName,params);
            if(origin == null){
                LogV.d("can't find "+methodName+" in origin Class "+className);
                return ;
            }
            Method fix = ReflectUtils.getDeclaredMethod(fixClass,methodName,params);
            if(fix == null){
                LogV.d("can't find "+methodName+" in fix Class "+className);
                return ;
            }
            try {
                boolean success = replaceMethod(origin,fix);
                if(!success) {
                    LogV.d("native replace method fail!");
                }
            } catch (Exception e) {
                LogV.e(e);
            }
        }

    }

    public native boolean replaceMethod(Method origin,Method hotfix);

    public native int getArtMethodSize();

    static {
        System.loadLibrary("hotfix");
    }

    static class LoadDexThread implements Runnable {

        @Override
        public void run() {

        }
    }
}
