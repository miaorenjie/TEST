package mobile.xiyou.atest;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.app.Instrumentation;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Window;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.PathClassLoader;

import static android.content.Context.CONTEXT_IGNORE_SECURITY;
import static mobile.xiyou.atest.Rf.*;

/**
 * Created by admin on 2017/3/1.
 */

public class App {

    public static final String EXTRA_TARGET_CLASS="an";
    public static final String EXTRA_ID="id";
    public static final String EXTRA_OLD_INTENT="old";

    private PkgInfo info;
    private ClassLoader loader,defaultLoader;
    private HashMap<String,Activity> activities;
    private Application application;
    private Resources res;
    private AssetManager am;
    private Resources.Theme theme;
    private HashMap<Integer,Resources.Theme> themes;
    private Context context,mcontext;
    private int id=0,appid=0;
    private boolean appAttached=false,taskAdded=false;
    private Object mThread=null,loadedApk=null;
    private Class mActivityClass=null,mServiceClass=null;

    public App(Context c,String packageName,int appid)
    {
        this.appid=appid;
        mcontext=c;
        context=c;
        defaultLoader=c.getClassLoader();
        try {
            mActivityClass=Class.forName("mobile.xiyou.atest.ActivityBase$A"+(appid+1));
            mServiceClass=Class.forName("mobile.xiyou.atest.ServiceBase$Service"+(appid+1));
            mThread=invoke(Class.forName("android.app.ActivityThread"),null,"currentActivityThread",new Class[]{});

            Context cc=c.createPackageContext(packageName, CONTEXT_IGNORE_SECURITY);

            String apkPath=cc.getPackageResourcePath();
            info=PkgInfo.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES|PackageManager.GET_META_DATA|PackageManager.GET_SHARED_LIBRARY_FILES|PackageManager.GET_RECEIVERS|PackageManager.GET_PROVIDERS);

            //info.info=c.getPackageManager().getPackageInfo(packageName,PackageManager.GET_ACTIVITIES);
            Intent i=new Intent(Intent.ACTION_MAIN, null);
            i.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> a=c.getPackageManager().queryIntentActivities(i,0);
            for (int ii=0;ii<a.size();ii++)
            {if (a.get(ii).activityInfo!=null) {


                if (a.get(ii).activityInfo.packageName.equals(packageName)) {
                    info.mainClass = a.get(ii).activityInfo.name;
                    //Log.e("xx", a.get(ii).activityInfo.name.toString());
                }
            }
            }
//            ApplicationInfo ai=(ApplicationInfo) readField(info.pkg.getClass(),info.pkg,"applicationInfo");
//            Log.e("xx",readField(ApplicationInfo.class,ai,"primaryCpuAbi").toString());
            //Init
            info.info.applicationInfo.dataDir="/data/data/"+info.info.packageName+"";
            loadedApk=ContextBase.loadApk(mThread,info.info.applicationInfo);
            //Log.e("xx","xx"+(readField(loadedApk.getClass(),loadedApk,"mPackageName")==null));
            //invoke(loadedApk.getClass(),loadedApk,"makeApplication",new Class[]{boolean.class,Instrumentation.class},false,null);
            //cc=ContextBase.createActivityContext(mThread,loadedApk);
            //context=cc;

            loader=new PathClassLoader(apkPath,cc.getApplicationInfo().nativeLibraryDir,ClassLoader.getSystemClassLoader());
            ClassLoader old=c.getClassLoader();
            setField(ClassLoader.class,loader,"parent",old.getParent());
            setField(ClassLoader.class,old,"parent",loader);
            //loader=c.getClassLoader();
            Class AT=Class.forName("android.app.ActivityThread");
            Map packages=(Map)readField(AT,mThread,"mPackages");
            packages.put(info.info.packageName,new WeakReference<Object>(loadedApk));
            setField(loadedApk.getClass(),loadedApk,"mClassLoader",loader);

            if (info.info.applicationInfo.className!=null) {
                application = (Application) loader.loadClass(info.info.applicationInfo.className).newInstance();
                //Log.e("xx","application:"+info.info.applicationInfo.className);
            }
            else
            application=new Application();

            Context ccc=null;
            ccc = context.createPackageContext(packageName, CONTEXT_IGNORE_SECURITY);
            String apkPatha=ccc.getPackageResourcePath();
            PkgInfo info1=PkgInfo.getPackageArchiveInfo(apkPatha, PackageManager.GET_PROVIDERS);
            ArrayList<ProviderInfo> providerInfos = new ArrayList<>();

            ProviderInfo[] providerInfos1 = info1.info.providers;
            Log.e("装在provider","asd");

            if(providerInfos1==null)
                return;
            for (int j = 0; j < providerInfos1.length; j++)
            {
                providerInfos.add(providerInfos1[j]);
                Log.e("getApkProvider",providerInfos1[j].authority);
            }
            Log.e("装在provider","asd");
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
            Log.e("装在provider","asd111111111");


            Log.e("装在provider","asd22222222222");
            setField(loadedApk.getClass(),loadedApk,"mApplication",application);
            themes=new HashMap<>();
            Log.e("装在provider","asd1");
            //Load resources:
            am = AssetManager.class.newInstance();
            Method add = am.getClass().getMethod("addAssetPath", String.class);
            add.invoke(am, apkPath);
            res = new Resources(am, c.getResources().getDisplayMetrics(), c.getResources().getConfiguration());
            theme = res.newTheme();
            theme.setTo(cc.getTheme());
            theme.applyStyle(info.info.applicationInfo.theme, true);
            installProvidersMethod.invoke(currentActivityThread, context, providerInfos);
            attachApplication(c);
            appAttached=true;
            setField(mThread,"mInitialApplication",application);

        } catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (InstantiationException e) {
            Log.e("xx",e.toString());
        } catch (InvocationTargetException e) {
            Throwable ee=e.getCause();
            while(ee!=null)
            {
                Log.e("xx","in app init:"+ee.toString());
                ee=ee.getCause();
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }
    }

    public Context getContext() {
        return context;
    }

    public void launch()
    {

    }

    private void staticRegister()
    {
        Log.e("xx","all Rec"+info.intents.receiver.size());
        if (info.info.receivers!=null)
        for (int i=0;i<info.info.receivers.length;i++)
        {
            //context.registerReceiver(getLoader().loadClass(info.info.receivers[i].name).newInstance());
            IntentFilter in=new IntentFilter();
            List<IntentFilter> lin=info.intents.receiver.get(info.info.receivers[i].name);
            if (lin==null)
                continue;
            BroadcastReceiver br=null;
            try {
                br= (BroadcastReceiver) getLoader().loadClass(info.info.receivers[i].name).newInstance();
            } catch (ClassNotFoundException e) {
                Log.e("xx",e.toString());
            } catch (InstantiationException e) {
                Log.e("xx",e.toString());
            } catch (IllegalAccessException e) {
                Log.e("xx",e.toString());
            }

            for (int j=0;j<lin.size();j++)
            {
                context.registerReceiver(br,lin.get(i));
                Log.i("xx","register :"+br.getClass().getName());
            }
        }
    }

    public ClassLoader getLoader()
    {
        return (PathClassLoader) invoke(loadedApk.getClass(),loadedApk,"getClassLoader",new Class[]{});
    }

    public ActivityInfo getActInfo(String name)
    {
        ActivityInfo[] x=info.info.activities;
        for (int i=0;i<x.length;i++)
        {
            if (x[i].name.equals(name))
                return x[i];
        }

        return null;
    }

    public Resources getRes()
    {
        return res;
    }

    public AssetManager getAm()
    {
        return am;
    }

    public Resources.Theme getTheme()
    {
        return theme;
    }

    public Resources.Theme getTheme(int i)
    {
        return themes.get(i);
    }

    public void newTheme(int t,int i)
    {
        Resources.Theme x=res.newTheme();
        x.setTo(theme);
        x.applyStyle(t, true);
        themes.put(i,x);
    }

    public void setTheme(Resources.Theme t)
    {
        theme=t;
    }

    public void setId(int i)
    {
        id=i;
    }

    public int getId()
    {
        return id;
    }

    public Intent startActivityIntent(String name)
    {
        id++;
        Intent i=new Intent(mcontext,mActivityClass);
        //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(EXTRA_TARGET_CLASS,name);
        i.putExtra(EXTRA_ID,id);
        return i;
    }

    public Intent startActivityIntent(Intent i)
    {

        if (i.getComponent()==null)
            return i;
        id++;
        Intent intent=new Intent();

        intent.putExtra(EXTRA_OLD_INTENT,i);
        intent.putExtra(EXTRA_TARGET_CLASS,i.getComponent().getClassName());
        intent.setComponent(new ComponentName(mcontext,mActivityClass));
        intent.putExtra(EXTRA_ID,id);
        return intent;
    }

    public Intent startServiceIntent(Intent i)
    {
        if (i.getComponent()==null)
            return i;
        id++;
        Intent intent=new Intent();

        intent.putExtra(EXTRA_OLD_INTENT,i);
        intent.putExtra(EXTRA_TARGET_CLASS,i.getComponent().getClassName());
        intent.setComponent(new ComponentName(mcontext,mServiceClass));
        intent.putExtra(EXTRA_ID,id);
        return intent;
    }

    public String getIntentClassName(Intent i)
    {
        String r=i.getStringExtra(EXTRA_TARGET_CLASS);
        return r!=null?r:info.mainClass;
    }

    public void solveIntent(ActivityBase c){
        Intent i=c.getIntent();
        if (!appAttached)
            attachApplication(c);
        String x;
        x=i.getStringExtra(EXTRA_TARGET_CLASS);
        if (x==null) {
            x = getInfo().mainClass;
        }else
        i=i.getParcelableExtra(EXTRA_OLD_INTENT);

        Activity a=getActivity(x);
        c.setRealActivity(a);
        attachActivity(c,a,i);
    }

    public Service solveIntent(ServiceBase c, Intent i)
    {
        if (!appAttached)
            attachApplication(c);
        String className=null;
        className=i.getStringExtra(EXTRA_TARGET_CLASS);
        if (className==null)
            className=getInfo().mainClass;
        Service s=null;
        s=c.getRealService(className);
        if (s!=null)
            return s;

        s=getService(className);
        attachService(c,s);

        return s;
    }

    public List getAppTask()
    {

        Object o=null;
        try {
            o=invoke(Class.forName("android.app.ActivityManagerNative"),null,"getDefault");
            o=invoke(o,"getAppTasks",new Class[]{String.class},"com.android.systemui");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (List)o;
    }

    public Activity getActivity(String name)
    {
        try {
            PathClassLoader l=(PathClassLoader) invoke(loadedApk.getClass(),loadedApk,"getClassLoader",new Class[]{});
            return (Activity) l.loadClass(name).newInstance();
        } catch (InstantiationException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }

        return null;
    }

    public Service getService(String name)
    {
        try {
            PathClassLoader l=(PathClassLoader) invoke(loadedApk.getClass(),loadedApk,"getClassLoader",new Class[]{});
            return (Service) l.loadClass(name).newInstance();
        } catch (InstantiationException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        } catch (ClassNotFoundException e) {
            Log.e("xx",e.toString());
        }

        return null;
    }

    public Application getApplication()
    {
        return application;
    }

    public Context getMContext()
    {
        return mcontext;
    }

    public PkgInfo getInfo()
    {
        return info;
    }

    public void attachApplication(Context c)
    {
        setField(mThread,"mInstrumentation",new PachInstr((Instrumentation) readField(mThread,"mInstrumentation"),this));
        try {
            context=new ContextBase(c,mThread,loadedApk,this,false);
            Method m = Application.class.getDeclaredMethod("attach", Context.class);
            m.setAccessible(true);
            m.invoke(application, context);
            setField(Application.class,application,"mLoadedApk",loadedApk);
            invoke(Application.class,application,"onCreate",new Class[]{});
            appAttached=true;
            staticRegister();

        }catch (NoSuchMethodException e) {
            Log.e("xx",e.toString());
        } catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        } catch (InvocationTargetException e) {
            Log.e("xx","in application attach"+e.getCause().toString());
        }
    }

    public void attachActivity(Activity base,Activity target,Intent ri){
        try{
            Log.i("xx","attach activity:"+target.getClass().getName());
            Object thread=readField(Activity.class,base,"mMainThread"),token=readField(Activity.class,base,"mToken"),ident=readField(Activity.class,base,"mIdent"),
                    mLastNonConfigurationInstances=readField(Activity.class,base,"mLastNonConfigurationInstances"),mCurrentConfig=readField(Activity.class,base,"mCurrentConfig"),mReferrer=readField(Activity.class,base,"mReferrer"),
                    mVoiceInteractor=readField(Activity.class,base,"mVoiceInteractor");
            ContextBase.init(thread,info.info.applicationInfo);
            Method ms[]=Activity.class.getDeclaredMethods();
            Method m=null;
            for (int i=0;i<ms.length;i++)
            {
                if (ms[i].getName().equals("attach"))
                {
                    m=ms[i];
                    m.setAccessible(true);
                    break;
                }
            }
            //m=Activity.class.getDeclaredMethod("attach",Context.class,thread.getClass(), Instrumentation.class, IBinder.class,
             //       int.class,Application.class,Intent.class, ActivityInfo.class,CharSequence.class,Activity.class,String.class,
             //       mLastNonConfigurationInstances.getClass(), Configuration.class,String.class,mVoiceInteractor.getClass());
            m.invoke(target,context,thread,readField(mThread,"mInstrumentation"),token,(int)ident,application,ri,getActInfo(target.getClass().getName()),
                   "title",null,"id",mLastNonConfigurationInstances,mCurrentConfig,mReferrer,mVoiceInteractor);

            //m.invoke(target, new Object[]{base, null, new Instrumentation(), null, 0, getApplication(), base.getIntent(), info.activities[0], "xxx", getParent(), "00", null, null, "", null});
            //setField(Activity.class,target,"mWindow", readField(Activity.class,base,"mWindow"));
            //base.getWindow().setUiOptions(getActInfo(getIntentClassName(base.getIntent())).uiOptions);
            Window window=base.getWindow();
            setField(Activity.class,target,"mWindow", window);
            window.setCallback(target);
            setField(Window.class,window,"mContext",target);
            setField(Window.class,window,"mFeatures",window.getDefaultFeatures(target));
            setField(Window.class,window,"mLocalFeatures",window.getDefaultFeatures(target));
            setField(window,"mLayoutInflater", LayoutInflater.from(target));
            setField(Activity.class,target,"mHandler",readField(Activity.class,base,"mHandler"));
            setField(Activity.class,target,"mManagedCursors",readField(Activity.class,base,"mManagedCursors"));
            setField(Activity.class,target,"mFragments",readField(Activity.class,base,"mFragments"));
            setField(Activity.class,target,"mUiThread",readField(Activity.class,base,"mUiThread"));
            setField(Activity.class,target,"mActivityTransitionState",readField(Activity.class,base,"mActivityTransitionState"));
            //setField(Activity.class,target,"mFragments",readField(Activity.class,base,"mFragments"));
            //setField(Activity.class,target,"mFragments",readField(Activity.class,base,"mFragments"));

            //setField(ContextThemeWrapper.class,target,"mTheme",getTheme());
            //setField(Activity.class,base,"mInstrumentation",new PachInstr(new Instrumentation()));
        }catch (InvocationTargetException e) {
            Log.e("xx","in acitivity attach:"+e.getCause().toString());
        }  catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        }
    }

    public void attachService(Service base,Service target)
    {
        try{
            Log.e("xx","attach service:"+target.getClass().getName());
            Object thread=readField(Service.class,base,"mThread"),token=readField(Service.class,base,"mToken"),ams=readField(Service.class,base,"mActivityManager");
            ContextBase.init(thread,info.info.applicationInfo);
            Method ms[]=Service.class.getDeclaredMethods();
            Method m=null;
            for (int i=0;i<ms.length;i++)
            {
                if (ms[i].getName().equals("attach"))
                {
                    m=ms[i];
                    m.setAccessible(true);
                    break;
                }
            }
            m.invoke(target,context,thread,target.getClass().getName(),token,application,ams);
        }catch (InvocationTargetException e) {
            Log.e("xx","in acitivity attach:"+e.getCause().toString());
        }  catch (IllegalAccessException e) {
            Log.e("xx",e.toString());
        }

        target.onCreate();
    }

}
