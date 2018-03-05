package cck.com.chello;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;

import cck.com.chello.viewInject.ViewInject;
import cck.com.chello.viewInject.ViewInjector;

/**
 * Created by chenlong on 18-1-22.
 */

public class AnnotationActivity extends Activity{
    @ViewInject(R.id.name) TextView mName;
    @ViewInject(R.id.click) Button mClick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.annotation_layout);
        ViewInjector.inject(this);
        if(mName != null) mName.setText("hahahah");
    }

}
