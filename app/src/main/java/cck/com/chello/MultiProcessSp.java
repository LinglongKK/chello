package cck.com.chello;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import cck.com.chello.uninstall.SharePreferenceHelper;

/**
 * Created by chenlong on 17-12-26.
 */

public class MultiProcessSp extends ContentProvider implements SharedPreferences{
    private static final String AUTHORITY = "cck.com.chello.multisp.provider";
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "*/*", 1);
    }

    public enum ValueType {
        INT,
        LONG,
        FLOAT,
        BOOL,
        STRING,
        STRING_SET,
    }

    public static final class ColumnKey {
        public static final String RESULT_VALUE_COLUMN = "_value";
    }


    private volatile ReadWriteLock mReadWriteLock = new ReentrantReadWriteLock();
    private String mSpFileName;
    private Context mContext;

    public MultiProcessSp() {}

    public MultiProcessSp(Context context,String name, int mode) {
        this.mContext = context;
        this.mSpFileName = name;
    }

    @Override
    public Map<String, ?> getAll() {
        return null;
    }

    @Override
    public String getString(String s, String s1) {
//        Cursor res = mContext.getContentResolver().query();
        return null;
    }

    @Override
    public Set<String> getStringSet(String s, Set<String> set) {
        return null;
    }

    @Override
    public int getInt(String s, int i) {
        return 0;
    }

    @Override
    public long getLong(String s, long l) {
        return 0;
    }

    @Override
    public float getFloat(String s, float v) {
        return 0;
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        return false;
    }

    @Override
    public boolean contains(String s) {
        return false;
    }

    @Override
    public Editor edit() {
        return null;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {

    }



    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = sUriMatcher.match(uri);
        if (code != 1) return null;
        List<String> pathList = uri.getPathSegments();
        if (pathList == null || pathList.size() < 2) return null;
        if (selectionArgs.length < 2) return null;
        Context context = getContext();
        if (context == null) return null;
        String spFileName = pathList.get(0);
        String spKeyName = pathList.get(1);
        if (TextUtils.isEmpty(spFileName) || TextUtils.isEmpty(spKeyName)) return null;

        ValueType valueType = ValueType.valueOf(selectionArgs[0]);
        String defaultValue = selectionArgs[1];

        MatrixCursor cursor = new MatrixCursor(new String[]{ColumnKey.RESULT_VALUE_COLUMN});
        MatrixCursor.RowBuilder rowBuilder = cursor.newRow();
        try {
            mReadWriteLock.readLock().lock();
            SharedPreferences sp = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
            if (sp == null || !sp.contains(spKeyName)) {
                rowBuilder.add(processDefaultValue(valueType,defaultValue));
            } else {
                switch (valueType) {
                    case INT:
                        int intRes = sp.getInt(spKeyName, saveConvertInt(defaultValue));
                        rowBuilder.add(intRes);
                        break;
                    case LONG:
                        long longRes = sp.getLong(spKeyName, saveConvertLong(defaultValue));
                        rowBuilder.add(longRes);
                        break;
                    case FLOAT:
                        float floatRes = sp.getFloat(spKeyName, saveConvertFloat(defaultValue));
                        rowBuilder.add(floatRes);
                        break;
                    case BOOL:
                        boolean boolRes = sp.getBoolean(spKeyName, saveConvertBoolean(defaultValue));
                        rowBuilder.add(boolRes);
                        break;
                    case STRING:
                        String stringRes = sp.getString(spKeyName, defaultValue);
                        rowBuilder.add(stringRes);
                        break;
                    case STRING_SET:
                        break;
                    default:
                }
            }
        } finally {
            mReadWriteLock.readLock().unlock();
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.item/vnd.cck.com.chello.multisp.provider.";
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException("not support insert");
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException("not support delete");
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int code = sUriMatcher.match(uri);
        if (code != 1) return 0;
        List<String> pathList = uri.getPathSegments();
        if (pathList == null || pathList.size() < 2) return 0;
        if (selectionArgs.length < 2) return 0;
        Context context = getContext();
        if (context == null) return 0;
        String spFileName = pathList.get(0);
        String spKeyName = pathList.get(1);
        if (TextUtils.isEmpty(spFileName) || TextUtils.isEmpty(spKeyName)) return 0;

        ValueType valueType = ValueType.valueOf(selectionArgs[0]);
        String methodName = selectionArgs[1];
        try{
            mReadWriteLock.writeLock().lock();
            SharedPreferences sp = context.getSharedPreferences(spFileName, Context.MODE_PRIVATE);
            if(sp == null) return 0;
            Object newValue = contentValues.get(spKeyName);
            Editor editor = null;
            switch (valueType) {
                case INT:
                    editor = sp.edit().putInt(spKeyName,saveConvertInt(newValue));
                    break;
                case LONG:
                    editor = sp.edit().putLong(spKeyName,saveConvertLong(newValue));
                    break;
                case FLOAT:
                    editor = sp.edit().putFloat(spKeyName,saveConvertFloat(newValue));
                    break;
                case BOOL:
                    editor = sp.edit().putBoolean(spKeyName,saveConvertBoolean(newValue));
                    break;
                case STRING:
                    editor = sp.edit().putString(spKeyName,String.valueOf(newValue));
                    break;
                case STRING_SET:
                    break;
                default:
            }
            if(editor != null) {
                if ("commit".equals(methodName)) {
                    editor.commit();
                }else if("apply".equals(methodName)) {
                    editor.apply();
                }
            }
        }finally {
            mReadWriteLock.writeLock().unlock();
        }
        return 0;
    }

    private int saveConvertInt(final Object intStr) {
        try {
            return Integer.valueOf(String.valueOf(intStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private long saveConvertLong(final Object intStr) {
        try {
            return Long.valueOf(String.valueOf(intStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }

    private float saveConvertFloat(final Object intStr) {
        try {
            return Float.valueOf(String.valueOf(intStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0F;
    }

    private double saveConvertDouble(final Object intStr) {
        try {
            return Double.valueOf(String.valueOf(intStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0D;
    }

    private boolean saveConvertBoolean(final Object intStr) {
        try {
            return Boolean.valueOf(String.valueOf(intStr));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Object processDefaultValue(ValueType valueType, String defaultValue) {
        switch (valueType) {
            case INT:
                return saveConvertInt(defaultValue);
            case LONG:
                return saveConvertLong(defaultValue);
            case FLOAT:
                return saveConvertFloat(defaultValue);
            case BOOL:
                return saveConvertBoolean(defaultValue);
            case STRING:
                return defaultValue;
            case STRING_SET:
                return null;
            default:
                return null;
        }
    }

}
