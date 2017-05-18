package mobile.xiyou.atest;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by miaojie on 2017/3/29.
 */

public class ServiceProxy implements InvocationHandler {
    private Object base;
    private Context context;
    public static ArrayList<String> servicename=new ArrayList<>();
    public static ArrayList<String> contentProviderName=new ArrayList<>();
    public static int ServiceNum=0;
    public String[]Authorities={
            "mobile.xiyou.atest.hook_contentProvider.ContentProviderBase",
            "mobile.xiyou.atest.hook_contentProvider.ContentProviderStub1",
            "mobile.xiyou.atest.hook_contentProvider.ContentProviderStub2",
            "mobile.xiyou.atest.hook_contentProvider.ContentProviderStub3",
            "mobile.xiyou.atest.hook_contentProvider.ContentProviderStub4",
            "mobile.xiyou.atest.hook_contentProvider.ContentProviderStub5",
            "mobile.xiyou.atest.hook_contentProvider.ContentProviderStub6",
            "mobile.xiyou.atest.hook_contentProvider.ContentProviderStub7",
            "mobile.xiyou.atest.hook_contentProvider.ContentProviderStub8",
            "mobile.xiyou.atest.hook_contentProvider.ContentProviderStub9",
    };
    public ServiceProxy(Context context, Object base) {
        this.context = context;
        this.base = base;
        servicename.add("asd");
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //   Log.e("找到了",method.getName());
//        if(method.getName().equals("startService")) {
//            ServiceNum++;
//            for (int i = 0; i < args.length; i++) {
//                if (args[i] instanceof Intent) {
//                    Intent targetService = ((Intent) args[i]);
//                    int startposition = targetService.getComponent().toString().indexOf("/") + 1;
//                    int len = targetService.getComponent().toString().length();
//                    servicename.add(targetService.getComponent().toString().substring(startposition, len - 1));
////                    AppManager.get().getApp(0).setRealservieName(servicename);
//                    Log.e("抓到的是：", servicename + "-----" + startposition);
//                    switch (ServiceNum)
//                    {
//                        case 1:
//                            args[i] = new Intent(context,Service1.class);
//                            break;
//                        case 2:
//                            args[i] = new Intent(context, ServiceBase.Service2.class);
//                            break;
//                        case 3:
//                            args[i] = new Intent(context, ServiceBase.Service3.class);
//                            break;
//                        case 4:
//                            args[i] = new Intent(context, ServiceBase.Service4.class);
//                            break;
//                        case 5:
//                            args[i] = new Intent(context, ServiceBase.Service5.class);
//                            break;
//
//                    }
//                    //  Log.e("invoke",servicename);
//
//
//                    break;
//                }
//            }
//        }
//
//        if (method.getName().equals("bindService")) {
//            ServiceNum++;
//            for (int i = 0; i < args.length; i++) {
//                if (args[i] instanceof Intent) {
//                    Intent targetService = ((Intent) args[i]);
//                    int startposition = targetService.getComponent().toString().indexOf("/") + 1;
//                    int len = targetService.getComponent().toString().length();
//                    servicename.add(targetService.getComponent().toString().substring(startposition, len - 1));
////                    AppManager.get().getApp(0).setRealservieName(servicename);
//                    Log.e("抓到的是：", servicename + "-----" + startposition);
//                    switch (ServiceNum)
//                    {
//                        case 1:
//                            args[i] = new Intent(context, Service1.class);
//                            break;
//                        case 2:
//                            args[i] = new Intent(context, ServiceBase.Service2.class);
//                            break;
//                        case 3:
//                            args[i] = new Intent(context, ServiceBase.Service3.class);
//                            break;
//                        case 4:
//                            args[i] = new Intent(context, ServiceBase.Service4.class);
//                            break;
//                        case 5:
//                            args[i] = new Intent(context, ServiceBase.Service5.class);
//                            break;
//
//                    }
//                    //  Log.e("invoke",servicename);
//                    break;
//                }
//            }
//        }
//        if(method.getName().equals("getContentProvider"))
//        {
//            for(int i=0;i<args.length;i++)
//            {
//                if(args[i]instanceof String&&(!args[i].equals("settings")))
//                {
//                    int size=MainApp.contentProviderMap.size();
//
//                    Log.e("getContentProvider", (String) args[i]);
//                    MainApp.contentProviderMap.put(Authorities[size], (String) args[i]);
//                    args[i]=Authorities[size];
//                    break;
//
//                }
//            }
//        }



        return method.invoke(base,args);
    }
}

