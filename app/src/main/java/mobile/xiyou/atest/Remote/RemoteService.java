package mobile.xiyou.atest.Remote;

import android.app.Instrumentation;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by miaojie on 2017/5/3.
 */

public class RemoteService extends Service{


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("已开启","asd");
        new Thread()
        {
            @Override
            public void run() {

                ClientThread clientThread = new ClientThread(RemoteUtil.ipAdress,RemoteUtil.port);
                clientThread.start();
                ClientReceiveCmdThread receiveCmdThread=new ClientReceiveCmdThread();
                receiveCmdThread.start();

            }
        }.start();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
