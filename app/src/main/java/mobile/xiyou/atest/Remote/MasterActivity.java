package mobile.xiyou.atest.Remote;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.IOException;

import mobile.xiyou.atest.R;

/**
 * Created by miaojie on 2017/5/3.
 */

public class MasterActivity extends Activity {
    private Handler handler;
    private RemoteImage remoteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.master_activity);
        remoteView= (RemoteImage) findViewById(R.id.remote_image);
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                RemoteInfo remoteInfo= (RemoteInfo) msg.obj;
                byte[]remoteBytes= remoteInfo.getRemoteDesktop();
                Bitmap bitmap= BitmapFactory.decodeByteArray(remoteBytes,0,remoteBytes.length);
                remoteView.setImageBitmap(bitmap);
            }
        };

        try {
            MasterThread masterThread=new MasterThread(RemoteUtil.port,handler);
            masterThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
