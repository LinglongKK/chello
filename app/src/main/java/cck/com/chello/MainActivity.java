package cck.com.chello;

import android.app.Activity;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by chenlong1 on 2017/11/16.
 */

public class MainActivity extends Activity {
    private static final String TAG = "chenlong";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        final HttpTest httpTest = new HttpTest();
        findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpTest.testCurl();
            }
        });

    }

    private void enableLauncherActivity() {
        PackageManager packageManager = getPackageManager();
        ComponentName componentName = new ComponentName(this,"cck.com.chello.LauncherActivity");
        packageManager.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        uninit();
    }
//
//    public native String hello();
//
//    public native int callJs(String js);
//
//    public native void uninit();
//
//    public native void init(Object ins);
//
//    static {
//        System.loadLibrary("hello");
//    }
}
