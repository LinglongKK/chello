package cck.com.chello;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import cck.com.chello.uninstall.IO;
import cck.com.chello.utils.FileIOUtil;
import cck.com.chello.utils.LogV;
import cck.com.chello.utils.ProcessUtils;

/**
 * Created by chenlong on 17-12-18.
 */

public class ProcessActivity extends Activity{
    private File processBoxDir;
    private Process nativeProcess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_activity_layout);
        initEnv();
        findViewById(R.id.release).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                release();
            }
        });
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });
    }

    private void initEnv() {
        processBoxDir = new File(getFilesDir(),"process");
        if(!processBoxDir.exists()) {
            processBoxDir.mkdir();
        }
    }

    private void start() {
        Thread run = new Thread(new Runnable() {
            @Override
            public void run() {
                nativeProcess = ProcessUtils.execute(processBoxDir,"process_ex");
                String output = FileIOUtil.readUTF8String(nativeProcess.getInputStream());
                LogV.d(output);
            }
        });
        run.start();
    }

    private void stop(){
        if(nativeProcess != null) nativeProcess.destroy();
    }

    private void release() {
        try {
            File destFile = new File(processBoxDir,"process_ex");
            FileIOUtil.copyFile(getAssets().open("process_ex"),destFile);
            LogV.d("release process_ex success!");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
