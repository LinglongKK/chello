package cck.com.chello.utils;



import java.io.File;
import java.io.IOException;


/**
 * Created by chenlong on 17-12-18.
 */

public class ProcessUtils {
    public static Process execute(File workDir,String executable) {
        File exeFile = new File(workDir,executable);
        if(!exeFile.exists()) {
            LogV.d(exeFile.getAbsolutePath() + " doesn't exist!");
        }
        if(!exeFile.canExecute()) {
            boolean res = makeExecute(exeFile);
            if(!res) {
                LogV.d(exeFile.getAbsolutePath()+" can't execute!");
                return null;
            }
        }
        try {
            return Runtime.getRuntime().exec("./"+executable,null,workDir);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean makeExecute(File target) {
        if(!target.isFile()) return false;
        try {
            LogV.d("File path:"+ target.getAbsolutePath());
            Process process = Runtime.getRuntime().exec("/system/bin/chmod 744 "+target.getAbsolutePath());
            int exitValue = process.waitFor();
            LogV.d("exit value:"+exitValue);
            return exitValue == 0;
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
