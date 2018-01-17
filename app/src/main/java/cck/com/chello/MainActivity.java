package cck.com.chello;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cck.com.chello.utils.LogV;


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
//                httpTest.testCurl("ceshi");
//                httpTest.testArray();
//                httpTest.emptyMethod();
//                httpTest.testGlobalRef();
//                httpTest.testHttpClient("k2/api/app/suggest?query=pgone");
                ContentResolver contentResolver = getContentResolver();
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("content");
                builder.authority("cck.com.chello.multisp.provider");
                builder.path("test");
                Cursor cursor = contentResolver.query(builder.build(),new String[]{"count"},null,new String[]{"int","-1"},null);
                if(cursor != null) {
                    while (cursor.moveToNext()) {
                        int res = cursor.getInt(0);
                        LogV.d(""+res);
                    }
                    cursor.close();
                }

            }
        });
        findViewById(R.id.watcher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpTest.testJavaCall();
                EditText editText = findViewById(R.id.js_input);
                editText.setText(""+httpTest.getCallObj().getCurrentCount());
            }
        });
        findViewById(R.id.run_js).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpTest.testFreeGlobalRef();
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
