/**
 * 
 */

package cck.com.chello.uninstall;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.lang.reflect.Method;
import java.util.List;



//import com.qihoo.haosou.msearchpublic.util.Log;

/**
 * @author zhangyong6
 */
public class UnInstallWatcher {


    private static final String TAG = "UninstallWatcher";

    
    private static final String[] browserList = new String[]{
    	"com.qihoo.browser",
    	"com.qihoo.padbrowser",//HD
    	"com.UCMobile",
    	"com.uc.browser",
    	"com.UCMobile.cmcc",
    	"com.uc.browser.hd",
    	"com.tencent.mtt",
    	"sogou.mobile.explorer",
    	"com.ijinshan.browser_fast",
    	"com.oupeng.mini.android", //欧朋
    	"org.mozilla.firefox",
    	"com.android.chrome",
    	"com.mx.browser", //遨游
    	"com.baidu.browser.apps",
    	
    };
    

    // 由于targetSdkVersion低于17，只能通过反射获取
    private static String getUserSerial(Context context)
    {
        Object userManager = context.getSystemService("user");
        if (userManager == null)
        {
            Log.e(TAG, "userManager not exsit !!!");
            return null;
        }
        
        try
        {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            return String.valueOf(userSerial);
        }
        catch (Exception e)
        {
            Log.e(TAG, "", e);
        }
        
        return null;
    }


    public static final void tryStart(final Context context, final String pageurl,
                                      final String counturl) {
   	 PackageManager pm = context.getPackageManager();
	    Intent defaultIntent = new Intent(Intent.ACTION_VIEW);
	    defaultIntent.setData(Uri.parse("http://www.google.com"));
	    ResolveInfo info = pm.resolveActivity(defaultIntent, PackageManager.MATCH_DEFAULT_ONLY);
	    
	    String browserPackageName = null;
	    String browserActivity = null;
	    
	    if(info.activityInfo.name.equals("com.android.internal.app.ResolverActivity")){
	    	Intent intent = new Intent();
	    	intent.setAction(Intent.ACTION_VIEW);
	    	intent.addCategory(Intent.CATEGORY_DEFAULT);
	    	intent.addCategory(Intent.CATEGORY_BROWSABLE);
	    	List<ResolveInfo> browserActivityList = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    	
	    	if(browserActivityList != null){
	    		for(String pkgName : browserList){
		    		for(ResolveInfo ri : browserActivityList){
		    			if(pkgName.equals(ri.activityInfo.packageName)){
		    				browserPackageName = pkgName;
		    				browserActivity = ri.activityInfo.name;
		    				break;
		    			}
		    		}
		    		if(browserPackageName != null){
		    			break;
		    		}
	    		}
	    		
	    		if(browserPackageName == null && browserActivityList.size() > 0){
	    			browserPackageName = browserActivityList.get(0).activityInfo.packageName;
	    			browserActivity = browserActivityList.get(0).activityInfo.name;
	    		}
	    		
	    	}
	    	
	    }

	    if(browserPackageName == null){
	    	browserPackageName = info.activityInfo.packageName;
	    	browserActivity = info.activityInfo.name;
	    }

	    final String bPkgName = browserPackageName;
	    final String bActivity = browserActivity;
	    
        new Thread() {
            @Override
            public void run() {
              //  tryStart2(context, pageurl, counturl);

                String watcher = NativeHelper.copyNativeLib(context, "watcher");
                if (watcher == null) {
                     Log.e(TAG, "Can not copy watcher,exit");
                }

                try {

                	String work_dir =null;
                    String status = Environment.getExternalStorageState();
                    if (!status.equals(Environment.MEDIA_MOUNTED)) {
                    	work_dir = context.getApplicationInfo().dataDir + "/360search/watcher/work";
                    }else{
                    	work_dir = Environment.getExternalStorageDirectory() + "/360search/watcher/work";
                    }
                	new File(work_dir).mkdirs();

//                    Log.e(TAG, "Build.VERSION.SDK_INT="+Build.VERSION.SDK_INT);
                	String uid = null;
                    try {
                        if (Build.VERSION.SDK_INT < 17) {
                            uid = "null";
                        } else {
                            uid = getUserSerial(context);
                        }
                    } catch (Exception ignored) {
                    } catch (Error error) {}
//                  Log.e(TAG, "uid="+uid);
                  Log.e(TAG, "watcher="+watcher);
//                  Log.e(TAG, "context.getApplicationInfo().dataDir="+context.getApplicationInfo().dataDir);
//                  Log.e(TAG, "pageurl="+pageurl);
//                  Log.e(TAG, "counturl="+counturl);
//                  Log.e(TAG, "uid="+uid);
//                  Log.e(TAG, "work_dir="+work_dir);
//                  Log.e(TAG, "browserPackageName="+bPkgName);
//                  Log.e(TAG, "browserActivity="+bActivity);
              	String[] cmd = {
              		watcher,
              		context.getApplicationInfo().dataDir,
//              		"http://www.baidu.com",
              		pageurl,
              		counturl,
              		uid,
              		work_dir,
              		bPkgName,
              		bActivity
              	};
              	
//              	String  str = "am start --user 0 -a android.intent.action.VIEW -d http://info.so.com/?product=Msearchuninstall&src=soapp&userid=577c897500ae775ab489dee8e7e63b81&version_name=2.0.2.1001&code_version=207&configuration=-1&channel=MSO_APP&phone_type=Nexus6&network_type=WIFI";
//              	[/data/data/com.qihoo.haosou/app_MyLibs/watcher, /data/data/com.qihoo.haosou, http://info.so.com/?product=Msearchuninstall&src=soapp&userid=577c897500ae775ab489dee8e7e63b81&version_name=2.0.2.1001&code_version=207&configuration=-1&channel=MSO_APP&phone_type=Nexus6&network_type=WIFI, http://s.360.cn/mso_app/uni.htm?userid=577c897500ae775ab489dee8e7e63b81&version_name=2.0.2.1001&code_version=207&configuration=-1&channel=MSO_APP&phone_type=Nexus6&network_type=WIFI, 0, /data/data/com.qihoo.haosou/watcher/work, com.qihoo.browser, com.qihoo.browser.BrowserActivity]
                    Runtime.getRuntime().exec(cmd);
//                    int code = process.waitFor();
//                    process.waitFor();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                
            }
        }.start();
    }

}
