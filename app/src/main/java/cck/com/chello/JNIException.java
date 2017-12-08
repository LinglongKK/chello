package cck.com.chello;

/**
 * Created by chenlong on 17-12-2.
 */

public class JNIException extends Exception{
    public JNIException() {
    }

    public JNIException(String message) {
        super(message);
    }

    public JNIException(String message, Throwable cause) {
        super(message, cause);
    }

    public JNIException(Throwable cause) {
        super(cause);
    }
}
