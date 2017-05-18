package mobile.xiyou.atest.Remote;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by miaojie on 2017/5/3.
 */

public class MasterThread extends Thread {
    private ServerSocket serverSocket;
    private Socket socketClient;
    private Handler handler;
    private int port;
    public MasterThread(int port,Handler handler) throws IOException {
        this.handler=handler;
        this.port=port;
        serverSocket=new ServerSocket(4396);
    }

    @Override
    public void run() {
        super.run();
        try {
            Log.e("MasterThread","等待");
            socketClient=serverSocket.accept();
            RemoteUtil.socketClient=socketClient;
            Log.e("MasterThread","链接");
            while(socketClient.isConnected())
            {
//                Log.e("MasterThread","传输"+socketClient.isInputShutdown());
                InputStream inputStream=socketClient.getInputStream();
                ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
////                byte[]bytes=new byte[1000];
////                int num=inputStream.read(bytes);
////                Log.e("MasterThread",new String(bytes,0,num));
////               socketClient.getInputStream();
//                Log.e("MasterThread","传输");
                RemoteInfo remoteInfo= (RemoteInfo) objectInputStream.readObject();
//                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
//                String pic=bufferedReader.readLine();
//                int num;
//                byte[]bytes=new byte[1000000];
//                num=inputStream.read(bytes);
//                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,num);
                Message message=new Message();
                message.what=1;
                message.obj=remoteInfo;
                handler.sendMessage(message);
//                Log.e("MasterThread",num+"");

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
