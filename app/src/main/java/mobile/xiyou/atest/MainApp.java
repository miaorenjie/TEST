package mobile.xiyou.atest;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by user on 2017/3/29.
 */

public class MainApp extends Application {
    public static App app=null;
    private String appName=null;
    private String processName=null;
    private int appid=0;
    private Application realapp;
    public static HashMap<String,String>contentProviderMap=new HashMap<>();
    public Context context;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);



//            Class activityManagerNative = Class.forName("android.app.ActivityManagerNative");
//            Field gDefaultField = activityManagerNative.getDeclaredField("gDefault");
//            gDefaultField.setAccessible(true);
//            Object gDefault = gDefaultField.get(null);
//
//            Class SingletonClass = Class.forName("android.util.Singleton");
//            Field mInstanceField = SingletonClass.getDeclaredField("mInstance");
//            mInstanceField.setAccessible(true);
//            Object mInstance = mInstanceField.get(gDefault);//获得当前应用的gDefault对象
//
//            Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");
//            Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iActivityManagerInterface}, new ServiceProxy(base, mInstance));
//            mInstanceField.set(gDefault, proxy);
            context=base;

            if (MainActivity.launchName==null)
            {
                Log.e("launchName","null");
                return;
            }
        Context cc= null;
        try {
            cc = context.createPackageContext(MainActivity.launchName, CONTEXT_IGNORE_SECURITY);
            String apkPath=cc.getPackageResourcePath();
            PkgInfo info=PkgInfo.getPackageArchiveInfo(apkPath, PackageManager.GET_PROVIDERS);

            ArrayList<ProviderInfo> providerInfos = new ArrayList<>();
            if(app==null)
                return;
            ProviderInfo[] providerInfos1 = info.info.providers;
            Log.e("get",(app.getInfo().info.providers==null)+"");
            if(providerInfos1==null)
                return;
            for (int i = 0; i < providerInfos1.length; i++)
            {
                providerInfos.add(providerInfos1[i]);
                Log.e("getApkProvider",providerInfos1[i].authority);
            }
            for (ProviderInfo providerInfo : providerInfos) {
                providerInfo.applicationInfo.packageName = context.getPackageName();
            }

//            Log.d("test", providerInfos.toString());
            Class<?> activityThreadClass = null;

            activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);
            Method installProvidersMethod = activityThreadClass.getDeclaredMethod("installContentProviders", Context.class, List.class);
            installProvidersMethod.setAccessible(true);
            installProvidersMethod.invoke(currentActivityThread, context, providerInfos);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    public MainApp()
    {

    }

    @Override
    public void onCreate() {
        super.onCreate();

        ActivityManager mActivityManager = (ActivityManager) this
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == Process.myPid()) {

                processName=appProcess.processName;
                break;
            }
        }

        startService(new Intent(this,AppManagerService.class));
        if (!processName.equals("mobile.xiyou.atest")&&!processName.equals("mobile.xiyou.atest:manager"))
        {
            Log.e("xx",processName);
            initApp();
        }


    }

    private void initApp()
    {
        byte[] cache=new byte[300];
        for (int i=0;i<300;i++)
            cache[i]=0;
        try {
            FileInputStream fis=openFileInput(AppManagerService.FILE_APPNAME);
            int n=fis.available();
            fis.read(cache,0,fis.available());
            String r[]=new String(cache,0,n).split("/");
            Log.e("xx",r[0]+r[1]);
            appName=r[0];
            appid=Integer.parseInt(r[1]);
        } catch (IOException e) {
            Log.e("xx",e.toString());
        }
        app=new App(this,appName,appid);
        realapp=app.getApplication();
    }

    public App getApp()
    {
        return app;
    }


    @Override
    public void registerActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        super.registerActivityLifecycleCallbacks(callback);
        app.getApplication().registerActivityLifecycleCallbacks(callback);
    }

    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        realapp.registerComponentCallbacks(callback);
        super.registerComponentCallbacks(callback);
    }

    @Override
    public void registerOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        realapp.registerOnProvideAssistDataListener(callback);
        super.registerOnProvideAssistDataListener(callback);
    }

    @Override
    public void unregisterActivityLifecycleCallbacks(ActivityLifecycleCallbacks callback) {
        realapp.unregisterActivityLifecycleCallbacks(callback);
        super.unregisterActivityLifecycleCallbacks(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        realapp.unregisterComponentCallbacks(callback);
        super.unregisterComponentCallbacks(callback);
    }

    @Override
    public void unregisterOnProvideAssistDataListener(OnProvideAssistDataListener callback) {
        realapp.unregisterOnProvideAssistDataListener(callback);
        super.unregisterOnProvideAssistDataListener(callback);
    }

}
