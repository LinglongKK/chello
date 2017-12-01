package cck.com.chello;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import cck.com.chello.typetest.TypeTest;
import cck.com.chello.uninstall.AssetsUtil;
import cck.com.chello.uninstall.UnInstallWatcher;

/**
 * Created by chenlong1 on 2017/11/16.
 */

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "chenlong";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        findViewById(R.id.click).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hello = hello();
                Toast.makeText(v.getContext(),hello,Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.watcher).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AssetsUtil.init(MainActivity.this);
//                UnInstallWatcher.tryStart(MainActivity.this,"https://www.baidu.com","");
                init(this);
            }
        });
        final EditText editText = (EditText)findViewById(R.id.js_input);
        findViewById(R.id.run_js).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String js = editText.getText().toString();
                if(!TextUtils.isEmpty(js)) {
                    int result = callJs(js);
                    Toast.makeText(view.getContext(),"result:"+result,Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(view.getContext(),"输入js",Toast.LENGTH_SHORT).show();
                }
            }
        });
        final TypeTest typeTest = new TypeTest();
        findViewById(R.id.type_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                int result = typeTest.getIntValue(10);
//                boolean result = typeTest.getBooleanValue(false);
                typeTest.setInstance();
//                long result = typeTest.getLongValue(100000L);
                double result = typeTest.getDoubleValue(1076.121);
                Log.d(TAG,"r:"+result);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        typeTest.setInstance();
                        TypeTest.logNative("msg:%s","测试...");
                    }
                }).start();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uninit();
    }

    public native String hello();

    public native int callJs(String js);

    public native void uninit();

    public native void init(Object ins);

    static {
        System.loadLibrary("hello");
    }
}
