package cck.com.chello.viewInject;

import android.app.Activity;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import cck.com.chello.utils.LogV;

/**
 * Created by chenlong on 18-1-22.
 */

public class ViewInjector {
    public static void inject(Activity activity) {
        Class<? extends Activity> target = activity.getClass();
        Field[] declaredFields = target.getDeclaredFields();
        for(Field field : declaredFields) {
            Annotation[] allAnno= field.getAnnotations();
            for(Annotation annotation : allAnno) {
                if(annotation instanceof ViewInject) {
                    processViewInject((ViewInject) annotation,field,activity);
                }else if(annotation instanceof OnClick) {

                }
            }
//            ViewInject annotation = field.getAnnotation(ViewInject.class);
//            processViewInject(annotation,field,activity);
        }
    }

    public static void iniject(Object target, View view) {
        Class<?> targetClass = target.getClass();

    }

    private static void injectInner(Class<?> targetClass,Object viewHolder) {
        Field[] declaredFields = targetClass.getDeclaredFields();
        for(Field field : declaredFields) {
            Annotation[] allAnno= field.getAnnotations();
            for(Annotation annotation : allAnno) {
                if(annotation instanceof ViewInject) {
                    processViewInject((ViewInject) annotation,field,activity);
                }else if(annotation instanceof OnClick) {

                }
            }
//            ViewInject annotation = field.getAnnotation(ViewInject.class);
//            processViewInject(annotation,field,activity);
        }
    }


    private static void processViewInject(ViewInject annotation, Field field, Activity target) {
        if(annotation != null) {
            int viewId = annotation.value();
            View view = target.findViewById(viewId);
            field.setAccessible(true);
            try{
                field.set(target,view);
            }catch (IllegalAccessException e) {
                LogV.e(e);
            }
        }
    }
}
