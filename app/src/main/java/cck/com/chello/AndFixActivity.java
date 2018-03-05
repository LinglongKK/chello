package cck.com.chello;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cck.com.chello.hotfix.Hotfix;
import cck.com.chello.hotfix.test.BookModel;
import cck.com.chello.utils.FileIOUtil;
import cck.com.chello.utils.LogV;

/**
 * Created by chenlong on 18-1-17.
 */

public class AndFixActivity extends Activity implements View.OnClickListener{
    private static final int REQUEST_PERMISSION_CODE = 1000;
    private BookModel mTestBookModel;
    private String hotFixPath = "/sdcard/hotfix.dex";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotfix_activity);
        findViewById(R.id.init).setOnClickListener(this);
        findViewById(R.id.load_hotfix).setOnClickListener(this);
        findViewById(R.id.method_call).setOnClickListener(this);
        mTestBookModel = new BookModel(10.0F);
    }

    void initHotfix(View view) {
        try{
            File hotfixDir = new File(getFilesDir(),"hotfix");
            if(!hotfixDir.exists()) hotfixDir.mkdir();
            hotFixPath = new File(hotfixDir,"hotfix.dex").getAbsolutePath();
            FileIOUtil.copyFile(getAssets().open("hotfix.dex"),hotFixPath);
        }catch (IOException e) {
            e.printStackTrace();
        }
        int size = Hotfix.getInstance().getArtMethodSize();
        LogV.d("size:"+size);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int read_result  = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
            int write_result = ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(read_result == PackageManager.PERMISSION_GRANTED && write_result == PackageManager.PERMISSION_GRANTED) {
                Hotfix.getInstance().init(hotFixPath);
            } else {
              ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSION_CODE);
            }
        } else {
            Hotfix.getInstance().init(hotFixPath);
        }
    }

    void loadHotfixDex() {

        Hotfix.getInstance().loadHotFix(BookModel.class.getName(),"getPrice");
    }

    void callMethod(){
        Toast.makeText(this,"price:"+mTestBookModel.getPrice(),Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.method_call) {
            callMethod();
        }else if(v.getId() == R.id.load_hotfix) {
            loadHotfixDex();
        }else if(v.getId() == R.id.init) {
            initHotfix(v);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_PERMISSION_CODE) {
            boolean read = false;
            boolean write = false;
            for(int i = 0;i<permissions.length;i++) {
                String permission = permissions[i];
                if(TextUtils.equals(Manifest.permission.READ_EXTERNAL_STORAGE,permission)) {
                    int result = grantResults[i];
                    read = result == PackageManager.PERMISSION_GRANTED;
                }
                if(TextUtils.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE,permission)) {
                    write = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
            }
            if(read && write) Hotfix.getInstance().init(hotFixPath);
        }else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
