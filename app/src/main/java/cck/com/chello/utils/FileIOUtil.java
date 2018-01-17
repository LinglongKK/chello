package cck.com.chello.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by chenlong on 17-12-18.
 */

public class FileIOUtil {
    private static final int BUFFER_SIZE = 4096;
    public static void copyFile(InputStream source,File dest) {
        copyFile(source,dest.getAbsolutePath());
    }
    public static void copyFile(InputStream source, String dest) {
        byte[] buffer = new byte[BUFFER_SIZE];
        File destFile = new File(dest);
        deleteSafe(destFile);
        OutputStream outputStream = null;
        int len;
        try{
            outputStream = new FileOutputStream(destFile);
            while((len = source.read(buffer,0,BUFFER_SIZE)) != -1) {
                outputStream.write(buffer,0,len);
            }
            outputStream.flush();
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeQuiet(source);
            closeQuiet(outputStream);
        }
    }

    public static String readUTF8String(InputStream stream) {
        if(stream == null) return "";
        int len;
        byte[] buffer = new byte[BUFFER_SIZE];
        try{
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while((len = stream.read(buffer,0,BUFFER_SIZE)) != -1) {
                byteArrayOutputStream.write(buffer,0,len);
            }
            closeQuiet(byteArrayOutputStream);
            closeQuiet(stream);
            return new String(byteArrayOutputStream.toByteArray(),"UTF-8");
        }catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void closeQuiet(Closeable closeable) {
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteSafe(File file) {
        if(file != null && file.exists()) {
            try{
                file.deleteOnExit();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
