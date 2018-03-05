package cck.com.chello;

import android.app.Application;
import android.content.Context;

import cck.com.chello.hotfix.Hotfix;

/**
 * Created by chenlong on 18-1-18.
 */

public class App extends Application{
    private static Application appCtx;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        appCtx = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HttpTest.init("http://k-api.360kan.com/");
    }

    public static Application getApp() {
        return appCtx;
    }
}
