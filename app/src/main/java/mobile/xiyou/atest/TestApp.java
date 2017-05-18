package mobile.xiyou.atest;

/**
 * Created by user on 2017/4/10.
 */
import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Created by miaojie on 2017/3/29.
 */

public class TestApp extends Application {
    public static Object proxy;
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("xx","appcreate");

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context=base;
        try {
            Class activityManagerNative=Class.forName("android.app.ActivityManagerNative");
            Field gDefaultField=activityManagerNative.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefault=gDefaultField.get(null);

            Class SingletonClass=Class.forName("android.util.Singleton");
            Field mInstanceField=SingletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object mInstance=mInstanceField.get(gDefault);//获得当前应用的gDefault对象

            Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
            proxy=Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{iActivityManagerInterface},new ServiceProxy(base,mInstance));
            mInstanceField.set(gDefault,proxy);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("myApplacation",e.toString());
        } catch (NoSuchFieldException e) {
            Log.e("myApplacation",e.toString());
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e("myApplacation",e.toString());
        }
    }
}
