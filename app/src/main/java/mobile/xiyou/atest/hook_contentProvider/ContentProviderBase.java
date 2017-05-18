package mobile.xiyou.atest.hook_contentProvider;

import android.app.Service;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Field;

import mobile.xiyou.atest.App;
import mobile.xiyou.atest.AppManagerService;
import mobile.xiyou.atest.MainActivity;
import mobile.xiyou.atest.MainApp;
import mobile.xiyou.atest.ServiceProxy;

/**
 * Created by miaojie on 2017/5/10.
 */

public class ContentProviderBase extends ContentProvider {
    public String AUTHORITY="mobile.xiyou.atest.hook_contentProvider.ContentProviderBase";
    private ContentProvider realContentProvider;
    @Override
    public boolean onCreate() {

            if (MainActivity.isfirst) {
                MainActivity.isfirst = false;
                Log.e("ContentProviderBase", "第一次");
                return false;
            }
            Log.e("ContentProviderBase", "onCreate");

//            Field contextField=ContentProviderBase.class.getDeclaredField("mContext");
//            contextField.setAccessible(true);
            if (MainApp.app == null)
                return false;
            ProviderInfo providerInfo = null;
            for (int i = 0; i < MainApp.app.getInfo().info.providers.length; i++) {
                if (MainApp.app.getInfo().info.providers[i].authority.equals(MainApp.contentProviderMap.get(AUTHORITY))) {
                    providerInfo = MainApp.app.getInfo().info.providers[i];
                    break;
                }
            }

//            Class TargetContentProviderClass=MainApp.app.getLoader().loadClass(MainApp.contentProviderMap.get(AUTHORITY));
//            realContentProvider= (ContentProvider) TargetContentProviderClass.newInstance();
//            realContentProvider.attachInfo(MainApp.app.getContext(),providerInfo);
//            realContentProvider.onCreate();
//            ServiceProxy.contentProviderName.get(0);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//            Log.e("ContentProviderBaseErr",e.toString());
//        } catch (IllegalAccessException e) {
//            Log.e("ContentProviderBaseErr",e.toString());
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            Log.e("ContentProviderBaseErr",e.toString());
//            e.printStackTrace();
//        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return realContentProvider.query(uri,projection,selection,selectionArgs,sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return realContentProvider.getType(uri);
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return realContentProvider.insert(uri,values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return realContentProvider.delete(uri,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return realContentProvider.update(uri,values,selection,selectionArgs);
    }
}
