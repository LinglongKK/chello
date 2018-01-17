package cck.com.chello;

import android.app.Application;


/**
 * Created by chenlong on 17-12-25.
 */

public class SimpleApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        HttpTest.init("http://k-api.360kan.com/");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
