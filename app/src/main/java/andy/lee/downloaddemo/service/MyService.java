package andy.lee.downloaddemo.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import andy.lee.downloaddemo.MainActivity;
import andy.lee.downloaddemo.R;

public class MyService extends Service {
    private static final String TAG = "MyService";

    private DownloadBinder mBinder = new DownloadBinder();

    public class DownloadBinder extends Binder {

        public void startDownload() {
            Log.d(TAG, "startDownload: ");
        }

        public int getProgress() {
            Log.d(TAG, "getProgress: ");
            return 0;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //onCreate方法只会在第一次启动服务的时候被调用
        Log.d(TAG, "onCreate: ");

        //
        Intent forGroundIntent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, forGroundIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("前台服务")
                .setContentText("这是前台服务")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setAutoCancel(false);
        //启动前台服务
        startForeground(1, builder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        //只要startService 则一定会调用这个方法
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
