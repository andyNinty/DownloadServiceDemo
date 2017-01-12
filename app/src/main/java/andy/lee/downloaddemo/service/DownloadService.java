package andy.lee.downloaddemo.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

import andy.lee.downloaddemo.DownloadListener;
import andy.lee.downloaddemo.MainActivity;
import andy.lee.downloaddemo.R;
import andy.lee.downloaddemo.net.DownloadTask;

public class DownloadService extends Service {
    private DownloadTask mDownloadTask;
    private String mUrl;
    private DownloadBinder mBinder = new DownloadBinder();
    public static final int NOTIFICATION_ID = 1;

    private DownloadListener mListener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(NOTIFICATION_ID, getNotification("下载中", progress));
        }

        @Override
        public void onSuccess() {
            mDownloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载成功", -1));
            Toast.makeText(DownloadService.this, "下载成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            mDownloadTask = null;
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("下载失败", -1));
            Toast.makeText(DownloadService.this, "下载失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPause() {
            mDownloadTask = null;
            Toast.makeText(DownloadService.this, "暂停下载", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCancel() {
            mDownloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "取消下载", Toast.LENGTH_SHORT).show();
        }
    };

    public class DownloadBinder extends Binder {

        public void startDownload(String url) {
            if (mDownloadTask == null) {
                mDownloadTask = new DownloadTask(mListener);
                mUrl = url;
                mDownloadTask.execute(mUrl);
                startForeground(NOTIFICATION_ID, getNotification("正在下载...", 0));
                Toast.makeText(DownloadService.this, "正在下载", Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload() {
            if (mDownloadTask != null) {
                mDownloadTask.pauseDownload();
                Toast.makeText(DownloadService.this, "暂停下载", Toast.LENGTH_SHORT).show();
            }
        }

        public void cancelDownload() {
            if (mDownloadTask != null) {
                mDownloadTask.cancelDownload();
            } else {
                if (mUrl != null) {
                    String fileName = mUrl.substring(mUrl.lastIndexOf("/"));
                    String fileDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(fileDir + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(NOTIFICATION_ID);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "取消下载", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (progress >= 0) {
            // 当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
}
