package cck.com.chello.uninstall;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 */
public class SharePreferenceHelper {
    private static SharedPreferences sSharedpreferences;

    private static SharedPreferences getSharePreference() {
	if (sSharedpreferences == null) {
	    sSharedpreferences = PreferenceManager.getDefaultSharedPreferences(AppGlobal.getBaseApplication());
	}
	return sSharedpreferences;
    }

    public static boolean save(String key, String value) {
	SharedPreferences.Editor editor = getSharePreference().edit();
	try {
	    editor.putString(key, value);
	} catch (Exception e) {
	    editor.putString(key, value);
	    LogUtils.e(e);
	}
	return editor.commit();
    }

    

    public static String load(String key, String defValue) {
	String str = defValue;
	try {
	    str = getSharePreference().getString(key, defValue);
	} catch (Exception e) {
	    LogUtils.e(e);
	}
	return str;
    }

    public static boolean save(String key, int value) {
	SharedPreferences.Editor editor = getSharePreference().edit();
	editor.putInt(key, value);
	return editor.commit();
    }

    public static int getInt(String key, int defValue) {
	return getSharePreference().getInt(key, defValue);
    }

    public static boolean save(String key, float value) {
	SharedPreferences.Editor editor = getSharePreference().edit();
	editor.putFloat(key, value);
	return editor.commit();
    }

    public static float getFloat(String key, float defValue) {
	return getSharePreference().getFloat(key, defValue);
    }
	public static boolean save(String key, long value) {
		SharedPreferences.Editor editor = getSharePreference().edit();
		editor.putLong(key, value);
		return editor.commit();
	}
    public static long getLong(String key, long defValue) {
		return getSharePreference().getLong(key, defValue);
    }

    public static boolean save(String key, Boolean value) {
	SharedPreferences.Editor editor = getSharePreference().edit();
	editor.putBoolean(key, value);
	return editor.commit();
    }

    public static boolean getBoolean(String key, boolean defValue) {
	return getSharePreference().getBoolean(key, defValue);
    }

    

    

    public static boolean removeKey(String key) {
	SharedPreferences.Editor editor = getSharePreference().edit();
	editor.remove(key);
	return editor.commit();
    }

    
    public static boolean HasKey(String key){
    	return getSharePreference().contains(key);
    }
}
