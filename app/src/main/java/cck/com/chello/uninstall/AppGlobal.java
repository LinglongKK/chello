package cck.com.chello.uninstall;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用全局信息统一在该类中获取,该类尽量不写与应用业务有关的代码
 * 
 * @author xupengpai
 * @date 2015年10月23日 下午3:57:03
 */
public class AppGlobal {

	private final static String TAG = AppGlobal.class.getSimpleName();

	private static Map<String,List<AppInitHandler>> appInitHandlers;

	//所有进程标识
	public final static String PROCESS_ALL = "!__APPGLOBAL__ALL_PROCESS";
	
	//主进程标识
	public final static String PROCESS_MAIN = "";


	// 全局application
	private static Application application;

	public static Application getBaseApplication() {
		checkError();
		return application;
	}

	public static void init(Application app) {
		AppGlobal.application = app;

		/**
		 * 设置插件中默认使用的日志托管对象
		 */
		LogUtils.SetDebugEnable(app);
	}

	private static void checkError() {
		if (application == null)
			throw new RuntimeException(
					"AppGlobal did not call through to AppGlobal.init()");
	}

	public static String getPackageName() {
		checkError();
		return application.getPackageName();
	}

	/**
	 * 获取当前进程名称
	 * @return
	 */
	public static String getProcessName() {
		checkError();
		
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) application
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
				.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {

				return appProcess.processName;
			}
		}
		return null;
	}

	public static boolean isMainProcess() {
		return getPackageName().equals(getProcessName());
	}


	public static int getVersionCode() {
		checkError();
		int verCode = 0;
		PackageManager pm = application.getPackageManager();
		if (pm != null) {
			PackageInfo pkgInfo;
			try {
				pkgInfo = pm.getPackageInfo(getPackageName(), 0);
				verCode = pkgInfo.versionCode;
			} catch (Exception e) {
				LogUtils.e(e);
			}
		}
		return verCode;
	}

	public static String getVersionName() {
		checkError();
		try {
			return application.getPackageManager().getPackageInfo(
					getPackageName(), 0).versionName;
		} catch (Throwable e) {
			e.printStackTrace();
			return "";
		}
	}

	public static abstract class AppInitHandler {
		
		public abstract void onCreate(Application app);

		public void onTerminate(Application app){
			
		}
		
		//延迟处理回调，主线程空闲时调用
		public void onDelayed(Application app){
			
		};

	}

	/**
	 * 注册一个应用初始化处理器
	 * 在所有进程都会执行
	 * 
	 * @param init
	 */
	public static void registerAppInitEvent(AppInitHandler init) {
		registerAppInitEvent(PROCESS_ALL,false,init);
	}
	
	/**
	 * 注册一个应用初始化处理器
	 * 只在指定进程中执行
	 * processName为进程后缀，主进程设为空即可
	 * 
	 * @param processName
	 * @param init
	 */
	public static void registerAppInitEvent(String processName, AppInitHandler init) {
//		registerAppInitEvent(init);
		registerAppInitEvent(processName,processName==null || !processName.startsWith("!"),init);
	}


	/**
	 * 
	 * 注册一个应用初始化处理器
	 * 只在指定进程中执行
	 * processNameIsSuffix 为true时，processName为进程后缀，如：":push"
	 * processNameIsSuffix 为false时，processName为进程全名，如："com.qihoo.haosou:push"
	 * 
	 * @param processName
	 * @param processNameIsSuffix
	 * @param init
	 */
	public static void registerAppInitEvent(String processName, boolean processNameIsSuffix, AppInitHandler init) {
		if(processName == null){
			processName = "";
		}
		if (init != null) {
			if(processNameIsSuffix){
				processName = getPackageName() + processName;
			}
			
			if(appInitHandlers == null){
				appInitHandlers = new HashMap<String, List<AppInitHandler>>();
			}

			//如果进程名为ALL_PROCESS，则在所有的处理器列表中添加该处理器
			/**
			 * 为保持注册顺序和执行顺序一致，实际上ALL_PROCESS的handler存在多个注册，一个在ALL_PROCESS里面
			 * 另外还有其他已经注册的进程里面(放在这里面是为了记录其注册顺序)
			 * 在执行的时候，先剔除自身进程重复的handler，然后再执行
			 * 这样避免了顺序被打乱，也避免了一个全局handler在一个进程中被执行两次
			 */
			if(processName.equals(PROCESS_ALL)){
				for (List<AppInitHandler> tmpList : appInitHandlers.values()) {
					if(!tmpList.contains(init))
						tmpList.add(init);
				}
			}
				
			List<AppInitHandler> list = appInitHandlers.get(processName);
				
			if(list == null){
				list = new ArrayList<AppInitHandler>();
				appInitHandlers.put(processName, list);
			}
			if(!list.contains(init))
				list.add(init);
			
		}
	}


	/**
	 * Application.onTerminate()调用时调用
	 * 
	 * @param app
	 */
	public static void appOnTerminate(Application app) {

		LogUtils.i(TAG, "---------------AppGlobal.appOnTerminate()--------------");
		LogUtils.i(TAG, "processName: " + getProcessName());
		LogUtils.i(TAG, "versionName: " + getVersionName());
		LogUtils.i(TAG, "versionCode: " + getVersionCode());

		long total = 0;
		if(appInitHandlers != null){
			String processName = getProcessName();
			List<AppInitHandler> list = appInitHandlers.get(processName);
			if(list != null){
				for (AppInitHandler handler : list) {
					if(handler != null){
						long start = System.currentTimeMillis();
						handler.onTerminate(app);
						long end = System.currentTimeMillis();
						long time = end - start;
						LogUtils.i(TAG, "Executive " + handler.getClass().getSimpleName()
								+ ".onTerminate() takes " + time + " ms.  ");
						total += time;
					}
				}
			}
		}

		LogUtils.i(TAG, "It took a total of 12 ms" + total + " ms.  ");
		LogUtils.i(TAG, " ");
		
	}
	
	private static long appOnCreate(final Application app, final List<AppInitHandler> list){
		
        Looper.myQueue().addIdleHandler(new IdleHandler() {
            @Override
            public boolean queueIdle() {
	        	for (AppInitHandler handler : list) {
	        		if(handler != null){
	        			long start = System.currentTimeMillis();
	        			handler.onDelayed(app);
	        			long end = System.currentTimeMillis();
	        			long time = end - start;
	        			LogUtils.i(TAG, "Executive " + handler.getClass().getSimpleName()
	        					+ ".onDelayed() takes " + time + " ms.  ");
	        		}
        		}
                return false;
            }
        });
        
		long total = 0;
		for (AppInitHandler handler : list) {
			if(handler != null){
				long start = System.currentTimeMillis();
				try{
					handler.onCreate(app);
				}catch(Exception e){
					LogUtils.e(TAG, "execption,"+handler.getClass().getName());
					LogUtils.e(TAG, e);
				}
				long end = System.currentTimeMillis();
				long time = end - start;
				LogUtils.i(TAG, "Executive " + handler.getClass().getSimpleName()
						+ ".onCreate() takes " + time + " ms.  ");
				total += time;
			}		
		}
		return total;
	}

	/**
	 * Application.onCreate()调用时调用
	 * 
	 * @param app
	 */
	public static void appOnCreate(Application app) {

		LogUtils.i(TAG, "---------------AppGlobal.appOnCreate()--------------");
		LogUtils.i(TAG, "processName: " + getProcessName());
		LogUtils.i(TAG, "versionName: " + getVersionName());
		LogUtils.i(TAG, "versionCode: " + getVersionCode());

		long total = 0;
		if(appInitHandlers != null){
			String processName = getProcessName();
			
			List<AppInitHandler> list = appInitHandlers.get(processName);
			List<AppInitHandler> allProcessHandlerList = appInitHandlers.get(PROCESS_ALL);
			if(allProcessHandlerList != null){
				List<AppInitHandler> tmpAllProcessHandlerList = new ArrayList<AppInitHandler>();
				tmpAllProcessHandlerList.addAll(allProcessHandlerList);
				//如果在自己进程的列表里面包含了每个进程都要执行的handler，则移除之，因为它自己的列表也会按顺序执行一次
				//如果没有包含，因为它是所有进程都要执行的，所以必须执行一次
				for(AppInitHandler handler : allProcessHandlerList){
					if(list != null && list.contains(handler)){
						tmpAllProcessHandlerList.remove(handler);
					}
				}
				total += appOnCreate(app, tmpAllProcessHandlerList);
			}
			if(list != null){
				total += appOnCreate(app, list);
			}
		}

		LogUtils.i(TAG, "It took a total of " + total + " ms.  ");
		LogUtils.i(TAG, "  ");
	}

	

// TODO Remove unused code found by UCDetector
// 	/**
// 	 * 获取栈顶Activity
// 	 * 
// 	 * @return
// 	 */
// 	public static ComponentName getTopActivity() {
// 
// 		checkError();
// 
// 		ActivityManager am = (ActivityManager) application
// 				.getSystemService(Context.ACTIVITY_SERVICE);
// 		return am.getRunningTasks(1).get(0).topActivity;
// 
// 	}

}
