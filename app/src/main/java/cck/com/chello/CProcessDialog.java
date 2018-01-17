package cck.com.chello;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by chenlong on 17-12-13.
 */

public class CProcessDialog extends ProgressDialog{
    public CProcessDialog(Context context) {
        super(context);
    }

    public CProcessDialog(Context context, int theme) {
        super(context, theme);
    }
}
