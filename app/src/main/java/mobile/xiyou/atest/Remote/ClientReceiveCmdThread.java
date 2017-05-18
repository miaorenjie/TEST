package mobile.xiyou.atest.Remote;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;

/**
 * Created by miaojie on 2017/5/5.
 */

public class ClientReceiveCmdThread extends Thread {
    String cmd;
    @Override
    public void run() {
        synchronized (ClientReceiveCmdThread.class)
        {
            while( RemoteUtil.socketClient.isConnected())
            {
                try {
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(RemoteUtil.socketClient.getInputStream()));
//                    Log.e("")
                   cmd= bufferedReader.readLine();
                   String[]cmds= cmd.split(",");
                    Log.e("ClientReceiveCmdThread",cmd);
                    receivceCmd(RemoteUtil.instrumentation,Integer.parseInt(cmds[0]),Double.parseDouble(cmds[1]),Double.parseDouble(cmds[2]));
//                    ObjectInputStream objectInputStream=new ObjectInputStream( RemoteUtil.socketClient.getInputStream());
//                    RemoteInfo remoteInfo= (RemoteInfo) objectInputStream.readObject();
//                    receivceCmd(RemoteUtil.instrumentation,remoteInfo.getCoordType(),remoteInfo.getCoordX(),remoteInfo.getCoordY());
                    Log.e("ClientReceiveCmdThread","执行成功");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e)
                {
                    Log.e("ClientReceiveCmdThread","他妈的大异常");
                }
            }
        }

    }

    private void receivceCmd(Instrumentation instrumentation,int type,double x,double y)
    {
        Log.e("尺寸：",RemoteUtil.screenWidth+"-----"+RemoteUtil.screenHeight);

        Log.e("receivceCmd","type:"+type+"--x："+x+"--y:"+y+"--RemoteUtil.socketClient.getInputStream()"+RemoteUtil.screenWidth);

        x=x*RemoteUtil.screenWidth;
        y=y*RemoteUtil.screenHeight;
        instrumentation.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),type,(float) x,(float) y,0));
    }
}
