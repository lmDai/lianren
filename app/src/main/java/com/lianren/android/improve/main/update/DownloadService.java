package com.lianren.android.improve.main.update;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.widget.RemoteViews;
import android.app.Service;
import com.lianren.android.AppConfig;
import com.lianren.android.R;
import com.lianren.android.improve.main.MainActivity;

import net.oschina.common.utils.StreamUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @package: com.lianren.android.improve.main.update
 * @user:xhkj
 * @date:2020/1/3
 * @description:
 **/
public class DownloadService extends Service {
    public static boolean isDownload = false;

    private String mUrl;
    private String mTitle = "正在下载";
    private String saveFileName = AppConfig.DEFAULT_SAVE_FILE_PATH;

    private NotificationManager mNotificationManager;
//    private Notification mNotification;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mNotificationManager.cancel(0);
                    installApk();
                    break;
                case 1:
                    int rate = msg.arg1;
                    if (rate < 100) {
                        mBuilder.setProgress(100, rate, false);//显示下载进度
                        mNotification = mBuilder.build();
                        mNotificationManager.notify(0, mNotification);
                    } else {
                        // 下载完毕后变换通知形式
                        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                        mNotification.contentView = null;
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("completed", "yes");
                        PendingIntent contentIntent = PendingIntent.getActivity(
                                getApplicationContext(), 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    }
                    mNotificationManager.notify(0, mNotification);

                    break;
            }

        }
    };
    private NotificationCompat.Builder mBuilder;
    private Notification mNotification;

    public static void startService(Context context, String downloadUrl) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("url", downloadUrl);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isDownload = true;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUrl = intent.getStringExtra("url");
        File file = new File(saveFileName);
        if (!file.exists()) {
            file.mkdirs();
        }
        final File apkFile = new File(saveFileName + "lr.apk");
        setUpNotification();
        new Thread() {
            @Override
            public void run() {
                try {
                    downloadUpdateFile(mUrl, apkFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private long downloadUpdateFile(String downloadUrl, File saveFile) throws Exception {
        int downloadCount = 0;
        int currentSize = 0;
        long totalSize = 0;
        int updateTotalSize = 0;

        HttpURLConnection httpConnection = null;
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            URL url = new URL(downloadUrl);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(20000);
            updateTotalSize = httpConnection.getContentLength();
            if (httpConnection.getResponseCode() == 404) {
                throw new Exception("fail!");
            }
            is = httpConnection.getInputStream();
            fos = new FileOutputStream(saveFile, false);
            byte buffer[] = new byte[2048];
            int readSize = 0;
            while ((readSize = is.read(buffer)) > 0) {

                fos.write(buffer, 0, readSize);
                totalSize += readSize;
                if ((downloadCount == 0)
                        || (int) (totalSize * 100 / updateTotalSize) - 4 > downloadCount) {
                    downloadCount += 4;
                    Message msg = mHandler.obtainMessage();
                    msg.what = 1;
                    msg.arg1 = downloadCount;
                    mHandler.sendMessage(msg);
                }
            }

            mHandler.sendEmptyMessage(0);
            isDownload = false;

        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
            StreamUtil.close(is, fos);
            stopSelf();
        }
        return totalSize;
    }

    private void installApk() {
        File file = new File(saveFileName + "lr.apk");
        if (!file.exists())
            return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
//判断是否是AndroidN以及更高的版本
        String authority = getPackageName() + ".provider";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(this, authority, file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        startActivity(intent);
    }

    private void setUpNotification() {

        mNotificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//适配8.0,自行查看8.0的通知，主要是NotificationChannel
            NotificationChannel chan1 = new NotificationChannel("PRIMARY_CHANNEL",
                    "Primary Channel", NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLightColor(Color.GREEN);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            mNotificationManager.createNotificationChannel(chan1);
            mBuilder = new NotificationCompat.Builder(this, "PRIMARY_CHANNEL");
        } else {
            mBuilder = new NotificationCompat.Builder(this, null);
        }
        mBuilder.setContentText(saveFileName + "lr.apk")//notification的一些设置，具体的可以去官网查看
                .setContentTitle(this.getString(R.string.app_name))
                .setTicker("正在下载")
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher);
        mNotification = mBuilder.build();
        mNotificationManager.notify(0, mNotification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        isDownload = false;
        super.onDestroy();
    }
}

